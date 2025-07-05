package com.amwojcik.newssorter.services;

import com.amwojcik.newssorter.models.chat.ChatMessage;
import com.amwojcik.newssorter.models.chat.ChatRequest;
import com.amwojcik.newssorter.models.chat.ChatResponse;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Service responsible for communication with the AI chat via OpenRouter.
 *
 * Allows selection of different AI models, though not all are free.
 */
@Service
public class OpenRouterService {

	private static final String OPENROUTER_URL = "https://openrouter.ai/api/v1/chat/completions";

	@Value("${openrouter.apiKey}")
	private String apiKey;

	private final RestTemplate restTemplate = new RestTemplate();

	public String getCompletion(String userPrompt) {
		ChatMessage userMessage = new ChatMessage("user", userPrompt);
		String gptMini = "openrouter/gpt-o4-mini";
		String deepseekFree = "deepseek/deepseek-r1-0528:free"; // free
		String deepseekFreeBest = "deepseek/deepseek-r1:free"; // free and good for this task
		ChatRequest request = new ChatRequest(deepseekFreeBest, List.of(userMessage));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + apiKey);

		HttpEntity<ChatRequest> entity = new HttpEntity<>(request, headers);

		ResponseEntity<ChatResponse> response = restTemplate.exchange(OPENROUTER_URL, HttpMethod.POST, entity,
				ChatResponse.class);

		if (response.getStatusCode().is2xxSuccessful()) {
			return response.getBody().getChoices().get(0).getMessage().getContent();
		} else {
			return "Processing error: error from OpenRouter API:" + response.getStatusCode();
		}
	}
}
