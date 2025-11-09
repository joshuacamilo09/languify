package io.languify.communication.conversation.controller;

import io.languify.communication.conversation.dto.GetConversationsDTO;
import io.languify.communication.conversation.model.Conversation;
import io.languify.communication.conversation.repository.ConversationRepository;
import io.languify.communication.conversation.service.ConversationService;
import io.languify.identity.auth.model.Session;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/conversations")
public class ConversationController {
  private ConversationService service;
  private ConversationRepository repository;

  @GetMapping
  public ResponseEntity<List<GetConversationsDTO>> getConversations(
      @AuthenticationPrincipal Session session) {
    try {
      List<Conversation> conversations =
          this.repository.findConversationsByUserId(session.getUser().getId());

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
    } catch (Exception e) {
      return ResponseEntity.internalServerError().build();
    }
  }

  @DeleteMapping("/conversations/{conversationId}")
  public ResponseEntity<?> deleteConversation(
      @PathVariable String conversationId, @AuthenticationPrincipal Session session) {
    try {
      this.service.deleteConversation(session.getUser().getId(), conversationId);
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      return ResponseEntity.internalServerError().build();
    }
  }
}
