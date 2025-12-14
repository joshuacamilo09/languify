package com.languify.communication.conversation.util;

public class LanguageDetector {
  /**
   * Detects language from text using simple heuristics. This is a placeholder - OpenAI's Whisper
   * transcription may provide language metadata in the future. For now, we rely on character set
   * patterns to make an educated guess.
   *
   * @param text The text to analyze
   * @return ISO 639-1 language code (e.g., "en", "es", "zh")
   */
  public static String detectLanguage(String text) {
    if (text == null || text.trim().isEmpty()) {
      return "en"; // Default to English
    }

    // Check for specific character sets
    if (text.matches(".*[\\u4E00-\\u9FFF]+.*"))
      return "zh"; // Chinese
    if (text.matches(".*[\\u0600-\\u06FF]+.*"))
      return "ar"; // Arabic
    if (text.matches(".*[\\u0400-\\u04FF]+.*"))
      return "ru"; // Russian
    if (text.matches(".*[\\u3040-\\u309F\\u30A0-\\u30FF]+.*"))
      return "ja"; // Japanese
    if (text.matches(".*[\\uAC00-\\uD7AF]+.*"))
      return "ko"; // Korean

    // For Latin script, check for common words/patterns
    // This is a simplified approach - could be enhanced with a proper language detection library
    String lowerText = text.toLowerCase();

    // Spanish indicators
    if (lowerText.matches(".*(\\b(el|la|los|las|un|una|de|del|que|en|es|por|para)\\b).*"))
      return "es";

    // Portuguese indicators
    if (lowerText.matches(".*(\\b(o|a|os|as|um|uma|de|do|da|que|em|por|para)\\b).*"))
      return "pt";

    // French indicators
    if (lowerText.matches(".*(\\b(le|la|les|un|une|de|du|que|en|est|pour)\\b).*"))
      return "fr";

    // German indicators
    if (lowerText.matches(".*(\\b(der|die|das|ein|eine|und|von|zu|in|ist)\\b).*"))
      return "de";

    // Italian indicators
    if (lowerText.matches(".*(\\b(il|lo|la|i|gli|le|un|una|di|del|che|in|per)\\b).*"))
      return "it";

    // Default to English for Latin script
    return "en";
  }
}
