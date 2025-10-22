package org.rest.languifybackend.Chat.Repository;

import org.rest.languifybackend.Chat.Model.Chat;
import org.rest.languifybackend.User.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepo extends JpaRepository<Chat, Long> {
    Optional<Chat> findByUsers();

    List<Chat> user(User user);
    Optional<Chat> findByUsers(String user1, String user2);

    //buscar chat existente entre dois usuarios
    @Query("SELECT c FROM Chat c WHERE "
            + "(c.user1.user_id = :user1Id AND c.user2.user_id = :user2Id) " +
            "OR " + "(c.user1.user_id = :user2Id AND c.user2.user_id = :user1Id)")

    Optional<Chat> findChatBetween(Long user1Id, Long user2Id);

    //listar todos os chats em que o user participa
    @Query("SELECT c FROM Chat c WHERE c.user1.user_id = :userId OR c.user2.user_id = :userId")
    List<Chat> findAllByUserId(Long userId);

    Optional<Chat> findChatsByUser1(User user1);

    List<Chat> findByUser1IdOrUser2Id(Long userId1, Long userId2);

    Optional<Chat> findMessageinChat(Long userId, Long chatId, Long messagedId);

    //findchatbetwweenuser é pra evitar duplicar chats entre as mesmas pessoas
    //ifndAllByuserid é pra listar conversas de um usuario

    @Query("SELECT m.chat FROM ChatRecord m WHERE m.chat.chat_id = :chatId AND m.id = :messageId")
    String findMessageInChat(@Param("chatId") Long chatId, @Param("messageId") Long messageId);

    List<Chat> findByUser1IdOrUser2IdAndActiveTrue(Long userId, Long userId1);
}
