package com.amwojcik.newssorter.models.chat;

import java.util.List;

/**
 * Message sent to the AI chat from the OpenRouterService.
 */
public class ChatRequest {
	private String model;
	private List<ChatMessage> messages;

	public ChatRequest() {
	}

	public ChatRequest(String model, List<ChatMessage> messages) {
		this.model = model;
		this.messages = messages;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public List<ChatMessage> getMessages() {
		return messages;
	}

	public void setMessages(List<ChatMessage> messages) {
		this.messages = messages;
	}
}
