package io.languify.communication.conversation.controller;

import io.languify.communication.conversation.dto.GetConversationsDTO;
import io.languify.communication.conversation.model.Conversation;
import io.languify.communication.conversation.repository.ConversationRepository;
import io.languify.identity.auth.model.Session;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/conversations")
public class ConversationController {
  private ConversationRepository conversationRepository;

  @GetMapping
  public ResponseEntity<List<GetConversationsDTO>> getConversations(
      @AuthenticationPrincipal Session session) {
    List<Conversation> conversations =
        this.conversationRepository.findConversationsByUserId(session.getUser().getId());

    List<GetConversationsDTO> dtos =
        conversations.stream()
            .map(
                (conversation) ->
                    new GetConversationsDTO(
                        conversation.getId(),
                        conversation.getUser().getId(),
                        conversation.getTitle(),
                        conversation.getSummary(),
                        conversation.getFromLanguage(),
                        conversation.getToLanguage(),
                        conversation.getCreatedAt()))
            .toList();

    return ResponseEntity.ok(dtos);
  }
}
