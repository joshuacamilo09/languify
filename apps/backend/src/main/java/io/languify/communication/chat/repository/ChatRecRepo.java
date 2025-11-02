package io.languify.communication.chat.repository;

import io.languify.communication.chat.model.ChatRecord;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRecRepo extends JpaRepository<ChatRecord, Long> {

  @Query("SELECT c FROM ChatRecord c WHERE c.chat.chat_id = :chatId AND c.id = :messageId")
  Optional<ChatRecord> findMessageInChat(
      @Param("chatId") Long chatId, @Param("messageId") Long messageId);
}
