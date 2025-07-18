package com.amwojcik.newssorter.models.chat;

import java.util.List;

/**
 * Response from the AI to the OpenRouterService containing an answer.
 */
public class ChatResponse {
	private List<Choice> choices;

	public static class Choice {
		private Message message;

		public Message getMessage() {
			return message;
		}

		public void setMessage(Message message) {
			this.message = message;
		}
	}

	public static class Message {
		private String role;
		private String content;

		public String getRole() {
			return role;
		}

		public void setRole(String role) {
			this.role = role;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}
	}

	public List<Choice> getChoices() {
		return choices;
	}

	public void setChoices(List<Choice> choices) {
		this.choices = choices;
	}
}
