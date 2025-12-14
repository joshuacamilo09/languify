package com.languify.infra.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.util.Base64
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentLinkedQueue

object AudioPlaybackManager {
    private const val SAMPLE_RATE = 24000
    private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_OUT_MONO
    private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT

    private var audioTrack: AudioTrack? = null
    private var playbackJob: Job? = null
    private val audioQueue = ConcurrentLinkedQueue<ByteArray>()
    private var isPlaying = false
    private var finishWhenDrained = false

    private var onPlaybackComplete: (() -> Unit)? = null
    private var onQueueDrained: (() -> Unit)? = null

    fun startPlayback(onComplete: () -> Unit) {
        if (isPlaying) return

        onPlaybackComplete = onComplete
        onQueueDrained = null

        val bufferSize =
            AudioTrack.getMinBufferSize(
                SAMPLE_RATE,
                CHANNEL_CONFIG,
                AUDIO_FORMAT,
            )

        audioTrack =
            AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build(),
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setSampleRate(SAMPLE_RATE)
                        .setChannelMask(CHANNEL_CONFIG)
                        .setEncoding(AUDIO_FORMAT)
                        .build(),
                )
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

        audioTrack?.play()
        isPlaying = true
        finishWhenDrained = false

        playbackJob =
            CoroutineScope(Dispatchers.IO).launch {
                while (isPlaying || audioQueue.isNotEmpty()) {
                    val chunk = audioQueue.poll()
                    if (chunk != null) {
                        audioTrack?.write(chunk, 0, chunk.size)
                    } else {
                        if (finishWhenDrained) {
                            isPlaying = false
                        }
                        Thread.sleep(10)
                    }
                }

                onPlaybackComplete?.invoke()
                onQueueDrained?.invoke()
            }
    }

    fun addAudioChunk(base64Audio: String) {
        val audioData = Base64.decode(base64Audio, Base64.NO_WRAP)
        audioQueue.offer(audioData)
    }

    fun stopPlayback() {
        isPlaying = false
        playbackJob?.cancel()
        playbackJob = null

        audioTrack?.let {
            try {
                it.stop()
            } catch (_: IllegalStateException) {
            }
            try {
                it.release()
            } catch (_: Exception) {
            }
        }

        audioTrack = null
        audioQueue.clear()
        finishWhenDrained = false
        onPlaybackComplete = null
        onQueueDrained = null
    }

    fun isCurrentlyPlaying() = isPlaying

    fun finishAfterQueue() {
        finishWhenDrained = true
    }

    fun onQueueDrained(callback: () -> Unit) {
        onQueueDrained = callback
        if (!isPlaying && audioQueue.isEmpty()) {
            callback()
        }
    }
}
