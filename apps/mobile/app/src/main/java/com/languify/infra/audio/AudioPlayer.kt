package com.languify.infra.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.util.Base64
import kotlin.math.max
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class AudioPlayer {
  private var audioTrack: AudioTrack? = null
  private var isPlaying = false

  companion object {
    private const val SAMPLE_RATE = 16000
    private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_OUT_MONO
    private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    private const val BYTES_PER_SAMPLE = 2
  }

  suspend fun playAudio(base64Audio: String, onComplete: () -> Unit) =
    withContext(Dispatchers.IO) {
      try {
        val audioData = Base64.decode(base64Audio, Base64.NO_WRAP)
        val minBufferSize = AudioTrack.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)

        audioTrack =
          AudioTrack.Builder()
            .setAudioAttributes(
              AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build()
            )
            .setAudioFormat(
              AudioFormat.Builder()
                .setEncoding(AUDIO_FORMAT)
                .setSampleRate(SAMPLE_RATE)
                .setChannelMask(CHANNEL_CONFIG)
                .build()
            )
            .setBufferSizeInBytes(max(minBufferSize, audioData.size))
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()

        if (audioTrack?.state != AudioTrack.STATE_INITIALIZED) {
          throw IllegalStateException("AudioTrack initialization failed")
        }

        isPlaying = true
        audioTrack?.play()

        var offset = 0
        val chunkSize = minBufferSize

        while (offset < audioData.size && isPlaying) {
          val remaining = audioData.size - offset
          val writeSize = minOf(chunkSize, remaining)

          val written = audioTrack?.write(audioData, offset, writeSize) ?: 0
          if (written < 0) break

          offset += written
        }

        val totalFrames = audioData.size / BYTES_PER_SAMPLE
        while (isPlaying) {
          val headPosition = audioTrack?.playbackHeadPosition ?: break
          if (headPosition >= totalFrames) break
          delay(10)
        }

        audioTrack?.stop()
        audioTrack?.release()
        audioTrack = null
        isPlaying = false

        onComplete()
      } catch (e: Exception) {
        isPlaying = false
        audioTrack?.release()
        audioTrack = null
        onComplete()
      }
    }

  fun stop() {
    isPlaying = false
    audioTrack?.stop()
    audioTrack?.release()
    audioTrack = null
  }

  fun isPlaying(): Boolean = isPlaying
}
