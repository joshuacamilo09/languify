package org.rest.languifybackend.Chat.Repository;

import org.rest.languifybackend.Chat.Model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepo extends JpaRepository<Chat, Long> {

    // Buscar chat entre dois usuários (evita duplicidade)
    @Query("SELECT c FROM Chat c WHERE " +
            "(c.user1.userId = :user1Id AND c.user2.userId = :user2Id) OR " +
            "(c.user1.userId = :user2Id AND c.user2.userId = :user1Id)")
    Optional<Chat> findChatBetween(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);

    // Listar todos os chats em que o usuário participa
    @Query("SELECT c FROM Chat c WHERE c.user1.userId = :userId OR c.user2.userId = :userId")
    List<Chat> findAllByUserId(@Param("userId") Long userId);

    // Listar apenas chats ativos de um usuário
    @Query("SELECT c FROM Chat c WHERE (c.user1.userId = :userId OR c.user2.userId = :userId) AND c.active = true")
    List<Chat> findActiveChatsByUserId(@Param("userId") Long userId);

    // Buscar mensagem específica em um chat
    @Query("SELECT m.Originaltext FROM ChatRecord m WHERE m.chat.chat_id = :chatId AND m.id = :messageId")
    String findMessageInChat(@Param("chatId") Long chatId, @Param("messageId") Long messageId);

    // Alternativa automática do Spring Data para listar chats de um usuário
    List<Chat> findByUser1UserIdOrUser2UserId(Long user1Id, Long user2Id);
}
