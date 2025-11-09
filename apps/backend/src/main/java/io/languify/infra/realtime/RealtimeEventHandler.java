package io.languify.infra.realtime;

public interface RealtimeEventHandler {
  void onOriginalTranscription(String transcript);

  void onTranslatedTranscription(String transcript);

  void onAudioDelta(String audioDelta);

  void onAudioDone();

  void onTranslationDone();
}
