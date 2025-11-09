package io.languify.communication.chat.service;

import io.languify.communication.chat.model.Chat;
import io.languify.communication.chat.model.ChatRecord;
import io.languify.communication.chat.model.Direction;
import io.languify.communication.chat.model.Location;
import io.languify.communication.chat.repository.ChatRecRepo;
import io.languify.communication.chat.repository.ChatRepo;
import io.languify.identity.user.model.User;
import io.languify.identity.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// Notificao de mensagem nao lida (webSockets)
// Enviar mensagem (webSockets)
// ----------------------//

@Service
@RequiredArgsConstructor
public class ChatService {
  private final UserRepository userRepo;
  private final ChatRepo chatRepo;
  private final ChatRecRepo chatRecordRepo;
  private final LocationGeoService geoService;

  public Chat createOrgetChat(Long userId1, Long userId2, String userIp) {
    Optional<Chat> existingChat = chatRepo.findChatBetween(userId1, userId2);

    if (existingChat.isPresent()) {
      return existingChat.get();
    } else {
      User user1 =
          userRepo.findById(userId1).orElseThrow(() -> new RuntimeException("User 1 not found"));

      User user2 =
          userRepo.findById(userId2).orElseThrow(() -> new RuntimeException("User 2 not found"));

      Chat chat = new Chat();
      chat.setUser1(user1);
      chat.setUser2(user2);
      chat.setOrigin_Idiom(user1.getNative_idiom());
      chat.setDestination_Idiom("English Default");
      chat.setCreated_at(LocalDateTime.now());

      Map<String, String> loc = geoService.getLocationByIp(userIp);
      Location location = new Location();
      location.setChat(chat);
      location.setCountry(loc.get("Country: "));
      location.setCity(loc.get("City: "));

      chat.setLocation(location);

      return chatRepo.save(chat);
    }
  }

  public List<Chat> listChatsByUser(Long userId) {
    return chatRepo.findByUser1UserIdOrUser2UserId(userId, userId);
  }

  public String findMessage(Long chatId, Long messageId) {
    String message = String.valueOf(chatRecordRepo.findMessageInChat(chatId, messageId));

    if (message == null || message.isEmpty()) {
      System.out.println("Message not found");
      return null;
    } else {
      return message;
    }
  }

  public ChatRecord sendMessage(
      Long chatId, String originalText, String TranslatedText, Direction direction) {
    Chat chat = chatRepo.findById(chatId).orElseThrow(() -> new RuntimeException("Chat not found"));

    ChatRecord chatRecord =
        ChatRecord.builder()
            .Translatedtext(TranslatedText)
            .Originaltext(originalText)
            .timestamp(LocalDateTime.now())
            .direction(Direction.USER_TO_USER)
            .chat(chat)
            .build();

    return chatRecordRepo.save(chatRecord);
  }

  public Chat deleteChat(Long chatId, Long userId) {
    Chat chat = chatRepo.findById(chatId).orElseThrow(() -> new RuntimeException("Chat not found"));

    if (!chat.getUser1().getId().equals(userId)
        && !chat.getUser2().getId().equals(userId)) {
      throw new RuntimeException("User don´t have permission to delete this chat");
    }
    chat.setActive(false);
    return chatRepo.save(chat);
  }

  public List<Chat> listActiveChats(Long userId) {
    return chatRepo.findActiveChatsByUserId(userId);
  }
}
