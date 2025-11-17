package com.languify.infra.audio

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext

class AudioRecorder {
  private var audioRecord: AudioRecord? = null
  private var isRecording = false

  companion object {
    private const val SAMPLE_RATE = 16000
    private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
    private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    private const val BUFFER_SIZE_MULTIPLIER = 2
  }

  suspend fun startRecording(onAudioChunk: (String) -> Unit) = withContext(Dispatchers.IO) {
    val bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT) * BUFFER_SIZE_MULTIPLIER

    try {
      audioRecord = AudioRecord(
        MediaRecorder.AudioSource.MIC,
        SAMPLE_RATE,
        CHANNEL_CONFIG,
        AUDIO_FORMAT,
        bufferSize
      )
    } catch (e: SecurityException) {
      e.printStackTrace()
    }


    if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) throw IllegalStateException("AudioRecord initialization failed")

    audioRecord?.startRecording()
    isRecording = true

    val buffer = ByteArray(bufferSize)

    while (isActive && isRecording) {
      val readBytes = audioRecord?.read(buffer, 0, buffer.size) ?: 0
      if (readBytes <= 0) continue

      val audioData = buffer.copyOf(readBytes)
      val base64Audio = Base64.encodeToString(audioData, Base64.NO_WRAP)

      onAudioChunk(base64Audio)
    }
  }

  fun stopRecording() {
    isRecording = false
    audioRecord?.stop()
    audioRecord?.release()
    audioRecord = null
  }

  fun isRecording(): Boolean = isRecording
}
