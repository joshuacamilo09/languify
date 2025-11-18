package com.languify.infra.audio

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Build
import android.util.Base64
import kotlin.math.max
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext

class AudioRecorder {
  private var audioRecord: AudioRecord? = null
  private var isRecording = false
  private var audioSource: Int = selectPreferredAudioSource()

  companion object {
    private const val SAMPLE_RATE = 16000
    private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
    private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    private const val BYTES_PER_SAMPLE = 2
    private const val FRAME_DURATION_MS = 20
    private const val FRAMES_PER_BATCH = 5

    private val FRAME_SIZE_BYTES = ((SAMPLE_RATE / (1000 / FRAME_DURATION_MS)) * BYTES_PER_SAMPLE)
    private val BATCH_SIZE_BYTES = FRAME_SIZE_BYTES * FRAMES_PER_BATCH

    private fun selectPreferredAudioSource(): Int {
      return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        MediaRecorder.AudioSource.UNPROCESSED
      } else {
        MediaRecorder.AudioSource.VOICE_RECOGNITION
      }
    }
  }

  suspend fun startRecording(onAudioChunk: (String) -> Unit) =
    withContext(Dispatchers.IO) {
      val minBuffer = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)
      val bufferSize = max(minBuffer, BATCH_SIZE_BYTES * 2)

      audioRecord = createAudioRecord(bufferSize)

      if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
        throw IllegalStateException("AudioRecord initialization failed")
      }

      audioRecord?.startRecording()
      isRecording = true

      val frameBuffer = ByteArray(BATCH_SIZE_BYTES)
      var offset = 0

      while (isActive && isRecording) {
        val remaining = frameBuffer.size - offset
        val readBytes = audioRecord?.read(frameBuffer, offset, remaining) ?: 0

        if (readBytes < 0) continue
        if (readBytes == 0) continue

        offset += readBytes

        if (offset == frameBuffer.size) {
          val base64Audio = Base64.encodeToString(frameBuffer.copyOf(), Base64.NO_WRAP)
          onAudioChunk(base64Audio)
          offset = 0
        }
      }

      if (offset > 0) {
        val base64Audio = Base64.encodeToString(frameBuffer.copyOf(offset), Base64.NO_WRAP)
        onAudioChunk(base64Audio)
      }
    }

  fun stopRecording() {
    isRecording = false
    audioRecord?.takeIf { it.recordingState == AudioRecord.RECORDSTATE_RECORDING }?.stop()
    audioRecord?.release()
    audioRecord = null
  }

  fun isRecording(): Boolean = isRecording

  private fun createAudioRecord(bufferSize: Int): AudioRecord {
    val preferredSources = mutableListOf<Int>()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      preferredSources.add(MediaRecorder.AudioSource.UNPROCESSED)
    }
    preferredSources.add(MediaRecorder.AudioSource.VOICE_RECOGNITION)
    preferredSources.add(MediaRecorder.AudioSource.MIC)

    for (source in preferredSources) {
      try {
        val record = AudioRecord(source, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, bufferSize)
        if (record.state == AudioRecord.STATE_INITIALIZED) {
          audioSource = source
          return record
        }
        record.release()
      } catch (e: Exception) {
        continue
      }
    }

    throw IllegalStateException("Unable to initialize AudioRecord with any audio source")
  }
}
