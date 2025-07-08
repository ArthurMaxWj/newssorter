package com.amwojcik.newssorter.models.articles;

import com.amwojcik.newssorter.models.articles.Article;
import java.util.List;

public class ArticlesJsonResponse {
	private boolean success;
	private List<Article> articles;
	private String error;

	public ArticlesJsonResponse() {
	}

	public ArticlesJsonResponse(boolean success, List<Article> articles, String error) {
		this.success = success;
		this.articles = articles;
		this.error = error;
	}

	public static ArticlesJsonResponse success(List<Article> articles) {
		return new ArticlesJsonResponse(true, articles, null);
	}

	public static ArticlesJsonResponse failure(String error) {
		return new ArticlesJsonResponse(false, null, error);
	}

	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}

	public List<Article> getArticles() {
		return articles;
	}
	public void setArticles(List<Article> articles) {
		this.articles = articles;
	}

	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
}
