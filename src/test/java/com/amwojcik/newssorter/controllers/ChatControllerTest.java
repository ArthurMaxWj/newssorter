package com.amwojcik.newssorter.controllers;

import com.amwojcik.newssorter.models.articles.Article;
import com.amwojcik.newssorter.services.OpenRouterService;
import com.amwojcik.newssorter.services.ArticleHandlingService;
import com.amwojcik.newssorter.services.CitiesFromAiService;

import java.util.List;
import java.util.ArrayList;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.http.HttpStatus;
import org.springframework.boot.test.mock.mockito.SpyBean;
import java.io.IOException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyString;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;

@WebMvcTest(ChatController.class)
class ChatControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private OpenRouterService openRouterService;

	@SpyBean
	private ChatController chatController;

	@Autowired
	private ObjectMapper objectMapper;

	@SpyBean
	private CitiesFromAiService citiesFromAiService;

	@MockBean
	private ArticleHandlingService articleHandlingService;

	@Test
	void staticAll_shouldReturnSuccessJson_whenNoError() throws Exception {
		mockMvc.perform(get("/api/static-all")).andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value("true")).andExpect(jsonPath("$.articles").exists());
	}

	@Test
	void staticAll_shouldReturnFailureJson_whenReadFileFails() throws Exception {
		doThrow(new IOException("Simulated file read failure")).when(chatController).readFileAsString(anyString());

		mockMvc.perform(get("/api/static-all")).andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value("false")).andExpect(jsonPath("$.error").value(
						"JSON Error: Can't read or deserialize articles-local.json or articles-global.json file"));
	}

	@Test
	void dynamicAll_shouldReturnSuccessJson_whenNoError() throws Exception {
		when(openRouterService.getCompletion(anyString())).thenReturn("Seattle,New York,Boston");

		when(articleHandlingService.normalizeArticlesFromCities(anyList(), anyList(), any()))
    	.thenReturn(new ArrayList<>(List.of(
			makeArticle("Seattle"),
			makeArticle("New York"),
			makeArticle("Boston")
		)));

		when(articleHandlingService.normalizeArticles(anyList(), anyString(), any()))
		.thenReturn(new ArrayList<>());

		mockMvc.perform(get("/api/dynamic-all").param("forcememo", "false")).andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value("true")).andExpect(jsonPath("$.articles").exists());
	}

	@Test
	void dynamicAll_shouldReturnFailureJson_whenReadFileFails() throws Exception {
		doThrow(new IOException("Simulated file read failure")).when(chatController).readFileAsString(anyString());

		mockMvc.perform(get("/api/dynamic-all")).andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value("false"))
				.andExpect(jsonPath("$.error").value("File Error: Can't read cities.csv or articles-local.json"));
	}

	@Test
	void dynamicAll_shouldReturnFailureJson_whenOpenRouterServiceThrows() throws Exception {
		String errorMsg = "Processing Error: Probably wrong credentials for AI API";
		HttpClientErrorException ex = new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Mock AI failure");

		when(openRouterService.getCompletion(anyString())).thenThrow(ex);
		mockMvc.perform(get("/api/dynamic-all").param("forcememo", "false")).andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value("false"))
				.andExpect(jsonPath("$.error").value(containsString(errorMsg)));
	}

	private Article makeArticle(String city) {
		return new Article("my title", "my content", "2025", Article.ArticleKind.LOCAL, city);
	}
}
