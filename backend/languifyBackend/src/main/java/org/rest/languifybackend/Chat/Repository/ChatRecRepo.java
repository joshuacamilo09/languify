package org.rest.languifybackend.Chat.Repository;

import org.rest.languifybackend.Chat.Model.ChatRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRecRepo extends JpaRepository<ChatRecord, Long> {
    List<ChatRecord> findAllByChatIdOrderByTimestampAsc(Long chatId);

    @Query("SELECT c FROM ChatRecord c WHERE c.chat.chat_id = :chatId AND c.id = :messageId")
    Optional<ChatRecord> findMessageInChat(@Param("chatId") Long chatId, @Param("messageId") Long messageId);

}