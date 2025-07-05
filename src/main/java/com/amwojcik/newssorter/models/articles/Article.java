package com.amwojcik.newssorter.models.articles;

/**
 * Article model used by ChatController.
 *
 * The "kind" field determines whether the article is local or global news.
 */
public class Article {
	private String title;
	private String content;
	private String date;
	private ArticleKind kind;
	private String city;

	public enum ArticleKind {
		LOCAL, GLOBAL;
	}

	public Article() {
	}

	public Article(String title, String content, String date, ArticleKind kind, String city) {
		this.title = title;
		this.content = content;
		this.date = date;
		this.kind = kind;
		this.city = city;
	}

	public Article(String title, String content, String date, ArticleKind kind) {
		this(title, content, date, kind, "Unknown");
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public ArticleKind getKind() {
		return kind;
	}

	public void setKind(ArticleKind kind) {
		this.kind = kind;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}
}
