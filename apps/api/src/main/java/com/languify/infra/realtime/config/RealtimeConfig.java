package com.languify.infra.realtime.config;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RealtimeConfig(List<String> modalities, String instructions, String voice,
        @JsonProperty("input_audio_format") String inputAudioFormat,
        @JsonProperty("output_audio_format") String outputAudioFormat,
        @JsonProperty("input_audio_transcription") InputAudioTranscription inputAudioTranscription,
        @JsonProperty("turn_detection") TurnDetection turnDetection, Double temperature) {

    public record InputAudioTranscription(String model) {
    }

    public record TurnDetection(String type) {
    }

    public static RealtimeConfig translation() {
        InputAudioTranscription inputAudioTranscription = new InputAudioTranscription("whisper-1");
        TurnDetection turnDetection = new TurnDetection("server_vad");

        return new RealtimeConfig(List.of("text", "audio"),
                "You are a real-time translator. When the user speaks, transcribe their speech accurately and provide a natural translation. Keep responses concise and focused on translation.",
                "alloy", "pcm16", "pcm16", inputAudioTranscription, turnDetection, 0.6);
    }
}
