package com.languify.core.utils

import android.annotation.SuppressLint
import android.media.*
import android.util.Base64
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AudioStreamManager {

    companion object {
        private const val SAMPLE_RATE = 24000
        private const val CHANNEL_IN = AudioFormat.CHANNEL_IN_MONO
        private const val CHANNEL_OUT = AudioFormat.CHANNEL_OUT_MONO
        private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    }

    private var audioRecord: AudioRecord? = null
    private var audioTrack: AudioTrack? = null
    private var isRecording = false

    init {
        val minBufferSize = AudioTrack.getMinBufferSize(SAMPLE_RATE, CHANNEL_OUT, AUDIO_FORMAT)
        val bufferSize = minBufferSize * 4 // buffer maior para evitar engasgos
        audioTrack = AudioTrack.Builder()
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
                    .setChannelMask(CHANNEL_OUT)
                    .build()
            )
            .setBufferSizeInBytes(bufferSize)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()
        audioTrack?.play()
    }

    // Inicia gravação do microfone e retorna chunks em Base64
    @SuppressLint("MissingPermission")
    fun startRecording(onAudioChunk: (String) -> Unit) {
        if (isRecording) return

        val minBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_IN, AUDIO_FORMAT)
        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            SAMPLE_RATE,
            CHANNEL_IN,
            AUDIO_FORMAT,
            minBufferSize
        )
        audioRecord?.startRecording()
        isRecording = true

        CoroutineScope(Dispatchers.IO).launch {
            val buffer = ByteArray(3200)
            while (isRecording) {
                val read = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                if (read > 0) {
                    val base64 = Base64.encodeToString(buffer, 0, read, Base64.NO_WRAP)
                    onAudioChunk(base64)
                }
            }
        }
    }

    fun stopRecording() {
        isRecording = false
        try {
            audioRecord?.stop()
            audioRecord?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        audioRecord = null
    }

    // Reproduz áudio recebido em Base64
    fun playAudioChunk(base64Audio: String) {
        try {
            val decoded = Base64.decode(base64Audio, Base64.NO_WRAP)
            audioTrack?.write(decoded, 0, decoded.size)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun release() {
        stopRecording()
        audioTrack?.release()
        audioTrack = null
    }
}
