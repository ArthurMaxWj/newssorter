package com.example.openrouter.service;

import com.example.openrouter.dto.ChatMessage;
import com.example.openrouter.dto.ChatRequest;
import com.example.openrouter.dto.ChatResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import io.github.cdimascio.dotenv.Dotenv;

@Service
public class OpenRouterService {

    private static final String OPENROUTER_URL = "https://openrouter.ai/api/v1/chat/completions";
    
    private final Dotenv dotenv = Dotenv.load();
    private String apiKey = dotenv.get("OPENROUTER_API_KEY");

    private final RestTemplate restTemplate = new RestTemplate();

    public String getCompletion(String userPrompt) {
        ChatMessage userMessage = new ChatMessage("user", userPrompt);
        String gptMini = "openrouter/gpt-o4-mini";
        String deepseekFree = "deepseek/deepseek-r1-0528:free"; // free
        String deepseekFreeBest =  "deepseek/deepseek-r1:free"; // free and good for this task
        ChatRequest request = new ChatRequest(deepseekFreeBest, List.of(userMessage));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        HttpEntity<ChatRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<ChatResponse> response = restTemplate.exchange(
                OPENROUTER_URL,
                HttpMethod.POST,
                entity,
                ChatResponse.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody()
                    .getChoices()
                    .get(0)
                    .getMessage()
                    .getContent();
        } else {
            return "Error: " + response.getStatusCode();
        }
    }
}
