package org.rest.languifybackend.Chat.Service;

import lombok.RequiredArgsConstructor;
import org.rest.languifybackend.Chat.Model.*;
import org.rest.languifybackend.Chat.Repository.ChatRecRepo;
import org.rest.languifybackend.Chat.Repository.ChatRepo;
import org.rest.languifybackend.User.Model.User;
import org.rest.languifybackend.User.UserRepo.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

//Notificao de mensagem nao lida (webSockets)
//Enviar mensagem (webSockets)
//----------------------//

//apagar mensagem (deletechat: (chatid)

@Service
@RequiredArgsConstructor
public class ChatService
{
    private final UserRepository userRepo;
    private final ChatRepo chatRepo;
    private final ChatRecRepo chatRecordRepo;
    private final GeoService geoService;

    public Chat createOrgetChat(Long userId1, Long userId2, String userIp)
    {
        Optional<Chat> existingChat = chatRepo.findChatBetween(userId1, userId2);

        if (existingChat.isPresent()){
            return existingChat.get();
        }
        else{
           User user1 = userRepo.findById(Long.valueOf(userId1))
                   .orElseThrow(() -> new RuntimeException("User 1 not found"));

           User user2 = userRepo.findById(Long.valueOf(userId2))
                   .orElseThrow(() -> new RuntimeException("User 2 not found"));

           Chat chat = new Chat();
           chat.setUser1(user1);
           chat.setUser2(user2);
           chat.getOrigin_Idiom();
           chat.setDestination_Idiom("English Default");
           chat.setCreated_at(LocalDateTime.now());

           Location location = new Location();
           location.setCountry(geoService.getCountry(userIp));
           location.setCity(geoService.getCity(userIp));

           chat.setLocation(location);

           return chatRepo.save(chat);
        }
    }

    public List<Chat> listChatsByUser(Long userId)
    {

        return chatRepo.findByUser1IdOrUser2Id(userId, userId);
    }

    public String findMessage(Long chatId, Long messageId, Long id)
    {
        String message = String.valueOf(chatRecordRepo.findMessageInChat(chatId, messageId));

        if (message == null || message.isEmpty()) {
            System.out.println("Message not found");
            return null;
        }
        else {
            return message;
        }
    }

    public ChatRecord sendMessage(Long chatId, String originalText, String TranslatedText, Direction direction)
    {
        Chat chat = chatRepo.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found"));

        ChatRecord chatRecord = ChatRecord.builder()
                .Translatedtext(TranslatedText)
                .Originaltext(originalText)
                .timestamp(LocalDateTime.now())
                .direction(Direction.USER_TO_USER)
                .chat(chat)
                .build();

        return chatRecordRepo.save(chatRecord);
    }

    public Chat deleteChat (Long chatId, Long userId)
    {
        Chat chat = chatRepo.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found"));

        if (!chat.getUser1().getUser_id().equals(userId) && !chat.getUser2().getUser_id().equals(userId))
        {
            throw new RuntimeException("User don´t have permission to delete this chat");
        }
        chat.setActive(false);

        return chatRepo.save(chat);
    }

    public List<Chat> listActiveChats(Long userId){
        return chatRepo.findByUser1IdOrUser2IdAndActiveTrue(userId, userId);
    }
}