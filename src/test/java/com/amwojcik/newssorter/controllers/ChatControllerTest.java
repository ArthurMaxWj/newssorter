package com.amwojcik.newssorter.controllers;

import com.amwojcik.newssorter.services.OpenRouterService;

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

	@Test
	void staticAll_shouldReturnSuccessJson_whenNoError() throws Exception {
		mockMvc.perform(get("/api/static-all")).andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value("true")).andExpect(jsonPath("$.articles").exists());
	}

	@Test
	void staticAll_shouldReturnFailureJson_whenReadFileFails() throws Exception {
		doThrow(new IOException("Simulated file read failure")).when(chatController).readFileAsString(anyString());

		mockMvc.perform(get("/api/static-all")).andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value("false"))
				.andExpect(jsonPath("$.error").value("JSON Error: Can't read or deserialize articles-local.json file"));
	}

	@Test
	void dynamicAll_shouldReturnSuccessJson_whenNoError() throws Exception {
		mockMvc.perform(get("/api/dynamic-all").param("forcememo", "false")).andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value("true")).andExpect(jsonPath("$.articles").exists());
	}

	@Test
	void dynamicAll_shouldReturnFailureJson_whenReadFileFails() throws Exception {
		doThrow(new IOException("Simulated file read failure")).when(chatController).readFileAsString(anyString());

		mockMvc.perform(get("/api/dynamic-all")).andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value("false")).andExpect(jsonPath("$.error")
						.value("Processing Error: Internal files of articles or cities might have been corrupted."));
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
}
