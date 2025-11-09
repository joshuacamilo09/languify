package io.languify.communication.conversation.controller;

import io.languify.communication.conversation.dto.GetConversationsDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/conversations")
public class ConversationController {
  @GetMapping
  public ResponseEntity<GetConversationsDTO> getConversations() {
    return ResponseEntity.ok().build();
  }
}
