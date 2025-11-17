package io.languify.communication.conversation.controller;

import io.languify.communication.conversation.dto.GetConversationTranscriptionsDTO;
import io.languify.communication.conversation.dto.GetConversationsDTO;
import io.languify.communication.conversation.dto.UpdateConversationDTO;
import io.languify.communication.conversation.model.Conversation;
import io.languify.communication.conversation.model.ConversationTranscription;
import io.languify.communication.conversation.repository.ConversationRepository;
import io.languify.communication.conversation.repository.ConversationTranscriptionRepository;
import io.languify.communication.conversation.service.ConversationService;
import io.languify.identity.auth.model.Session;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/conversations")
public class ConversationController {
  private ConversationService service;

  private ConversationRepository repository;
  private ConversationTranscriptionRepository transcriptionRepository;

  @GetMapping
  public ResponseEntity<List<GetConversationsDTO>> getConversations(
      @AuthenticationPrincipal Session session) {
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
  }

  @PutMapping("/{conversationId}")
  public ResponseEntity<?> updateConversation(
      @PathVariable UUID conversationId,
      @Validated UpdateConversationDTO req,
      @AuthenticationPrincipal Session session) {
      return authorizeConversation(
              conversationId,
              session,
              (conversation) -> {
                  String title = req.getTitle();

                  if (title != null) {
                      conversation.setTitle(req.getTitle());
                  }

                  String summary = req.getSummary();

                  if (summary != null) {
                      conversation.setSummary(summary);
                  }

                  this.repository.save(conversation);
                  return ResponseEntity.noContent().build();
              });
  }

  @DeleteMapping("/{conversationId}")
  public ResponseEntity<?> deleteConversation(
      @PathVariable UUID conversationId, @AuthenticationPrincipal Session session) {
      return authorizeConversation(
              conversationId,
              session,
              (conversation) -> {
                  this.repository.delete(conversation);
                  return ResponseEntity.ok().build();
              });
  }

  @GetMapping("/{conversationId}/transcriptions")
  public ResponseEntity<?> getConversationTranscriptions(
      @PathVariable UUID conversationId, @AuthenticationPrincipal Session session) {
      return authorizeConversation(
              conversationId,
              session,
              conversation -> {
                  List<ConversationTranscription> transcriptions =
                          this.transcriptionRepository.findConversationTranscriptionsByConversationId(
                                  conversation.getId());

                  List<GetConversationTranscriptionsDTO> dtos =
                          transcriptions.stream()
                                  .map(
                                          (transcription) ->
                                                  new GetConversationTranscriptionsDTO(
                                                          transcription.getId(),
                                                          transcription.getConversation().getId(),
                                                          transcription.getOriginalTranscript(),
                                                          transcription.getTranslatedTranscript(),
                                                          transcription.getCreatedAt()))
                                  .toList();

                  return ResponseEntity.ok(dtos);
              });
  }

  private ResponseEntity<?> authorizeConversation(
      UUID conversationId,
      Session session,
      java.util.function.Function<Conversation, ResponseEntity<?>> fn) {
    Optional<Conversation> optionalConversation =
        this.repository.findConversationById(conversationId);

    if (optionalConversation.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    Conversation conversation = optionalConversation.get();

    if (!Objects.equals(session.getUser().getId(), conversation.getUser().getId())) {
      return ResponseEntity.status(403).build();
    }

    return fn.apply(conversation);
  }
}
