package com.sumpositive.onboarding.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sumpositive.onboarding.dto.ChatRequest;
import com.sumpositive.onboarding.dto.ChatResponse;
import com.sumpositive.onboarding.service.ChatbotService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    private final ChatbotService chatbotService;

    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping("/ask")
    public ResponseEntity<ChatResponse> ask(
            @Valid @RequestBody ChatRequest request,
            Authentication authentication) {

        // authentication.getName() = email stored in the JWT subject
        String reply = chatbotService.reply(
                authentication.getName(),
                request.getMessage());

        return ResponseEntity.ok(new ChatResponse(reply));
    }
}