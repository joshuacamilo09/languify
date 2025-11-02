package io.languify.communication.chat.controller;

import io.languify.communication.chat.model.Chat;
import io.languify.communication.chat.model.ChatRecord;
import io.languify.communication.chat.model.Direction;
import io.languify.communication.chat.service.ChatService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/languify/chat")
@RequiredArgsConstructor
public class ChatController {
  private final ChatService chatService;

  @PostMapping("/createOrGet")
  public ResponseEntity<Chat> createOrGet(
      @RequestParam Long userId1, @RequestParam Long userId2, HttpServletRequest request) {
    String userIp = getClientIp(request);
    Chat chat = chatService.createOrgetChat(userId1, userId2, userIp);
    return ResponseEntity.ok(chat);
  }

  private String getClientIp(HttpServletRequest request) {
    String ip = request.getHeader("X-Forwarded-For");
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getRemoteAddr();
    } else {
      ip = ip.split(",")[0];
    }
    return ip;
  }

  @GetMapping("/getChats/user/{userId}")
  public ResponseEntity<List<Chat>> getChatsFromUser(@PathVariable Long userId) {
    List<Chat> chats = chatService.listActiveChats(userId);
    return ResponseEntity.ok(chats);
  }

  @GetMapping("/getMessage/chat/{chatId}/message/{messageId}")
  public ResponseEntity<String> getMessage(
      @PathVariable Long chatId, @PathVariable Long messageId) {

    String message = chatService.findMessage(chatId, messageId);

    if (message == null) {
      return ResponseEntity.notFound().build();
    } else {
      return ResponseEntity.ok(message);
    }
  }

  @PostMapping("/sendMessage")
  public ResponseEntity<ChatRecord> sendMessage(
      @RequestParam Long chatId,
      @RequestParam String originalText,
      @RequestParam String translatedText,
      @RequestParam Direction direction) {

    ChatRecord record = chatService.sendMessage(chatId, originalText, translatedText, direction);
    return ResponseEntity.ok(record);
  }

  @DeleteMapping("/deleteChat/{chatId}")
  public ResponseEntity<String> deleteChat(@RequestParam Long userId, @RequestParam Long chatId) {
    Chat deletedchat = chatService.deleteChat(userId, chatId);
    return ResponseEntity.ok("deleted");
  }
}
