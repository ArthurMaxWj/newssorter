package com.amwojcik.newssorter.services;

import com.amwojcik.newssorter.models.articles.Article;
import java.util.List;
import org.springframework.stereotype.Service;

import static com.amwojcik.newssorter.models.articles.Article.ArticleKind;

@Service
public class ArticleHandlingService {
	public List<Article> normalizeArticles(List<Article> articles, String city, ArticleKind kind) {
		for (int i = 0; i < articles.size(); i++) {
			Article a = articles.get(i);
			a.setCity(city);
			a.setKind(kind);
			articles.set(i, a);
		}
		return articles;
	}

	public List<Article> normalizeArticlesFromCities(List<Article> articles, List<String> cities, ArticleKind kind) {
		for (int i = 0; i < articles.size(); i++) {
			Article a = articles.get(i);
			a.setCity(cities.get(i));
			a.setKind(Article.ArticleKind.LOCAL);
			articles.set(i, a);
		}
		return articles;
	}
}
