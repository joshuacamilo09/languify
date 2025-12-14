package com.languify.infra.audio

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Base64
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

object AudioRecordManager {
    private const val SAMPLE_RATE = 24000
    private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
    private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    private const val BUFFER_SIZE_MULTIPLIER = 2

    private var audioRecord: AudioRecord? = null
    private var recordingJob: Job? = null
    private var isRecording = false

    private var onAudioChunk: ((String) -> Unit)? = null

    fun startRecording(onChunk: (String) -> Unit) {
        if (isRecording) return

        onAudioChunk = onChunk

        val bufferSize =
            AudioRecord.getMinBufferSize(
                SAMPLE_RATE,
                CHANNEL_CONFIG,
                AUDIO_FORMAT,
            ) * BUFFER_SIZE_MULTIPLIER

        audioRecord =
            AudioRecord(
                MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                CHANNEL_CONFIG,
                AUDIO_FORMAT,
                bufferSize,
            )

        audioRecord?.startRecording()
        isRecording = true

        recordingJob =
            CoroutineScope(Dispatchers.IO).launch {
                val buffer = ByteArray(4096)

                while (isRecording) {
                    val read = audioRecord?.read(buffer, 0, buffer.size) ?: 0

                    if (read > 0) {
                        val base64Audio = Base64.encodeToString(buffer, 0, read, Base64.NO_WRAP)
                        onAudioChunk?.invoke(base64Audio)
                    }
                }
            }
    }

    fun stopRecording() {
        isRecording = false
        recordingJob?.cancel()
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
        onAudioChunk = null
    }

    fun isCurrentlyRecording() = isRecording
}
