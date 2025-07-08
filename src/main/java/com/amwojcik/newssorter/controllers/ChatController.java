package com.amwojcik.newssorter.controllers;

import com.amwojcik.newssorter.models.articles.Article;
import com.amwojcik.newssorter.models.articles.ArticlesJsonResponse;
import com.amwojcik.newssorter.services.OpenRouterService;
import com.amwojcik.newssorter.services.ArticleHandlingService;
import com.amwojcik.newssorter.services.CitiesFromAiService;
import com.amwojcik.newssorter.services.CitiesFromAiService.AiCommunicator;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handles API requests to /api and responds with JSON.
 *
 * The "success" field determines whether articles are returned. - If "success"
 * is true, the "articles" field will contain the articles. - If "success" is
 * false, the "error" field will contain an error message.
 *
 * The error message should always include the string "Error:".
 */
@RestController
@RequestMapping("/api")
public class ChatController {
	@Autowired
	private OpenRouterService openRouterService;

	@Autowired
	private ArticleHandlingService articleHandlingService;

	@Autowired
	private CitiesFromAiService citiesFromAiService;

	@GetMapping("/static-all")
	public ArticlesJsonResponse staticall() {
		Optional<List<Article>> articlesMaybe = articlesFromInternalJsonFile("articles-local.json");
		Optional<List<Article>> articlesGlobalMaybe = articlesFromInternalJsonFile("articles-global.json");
		if (articlesMaybe.isEmpty() || articlesGlobalMaybe.isEmpty()) {
			return ArticlesJsonResponse
					.failure("JSON Error: Can't read or deserialize articles-local.json or articles-global.json file");
		}
		List<Article> articles = articlesMaybe.get();
		List<Article> articlesGlobal = articlesGlobalMaybe.get();

		articles = articleHandlingService.normalizeArticles(articles, "Unknown", Article.ArticleKind.LOCAL);
		articlesGlobal = articleHandlingService.normalizeArticles(articlesGlobal, "Global", Article.ArticleKind.GLOBAL);
		articles.addAll(articlesGlobal);

		return ArticlesJsonResponse.success(articles);
	}

	@GetMapping("/dynamic-all")
	public ArticlesJsonResponse dynamicall(
			@RequestParam(name = "forcememo", required = false) String forceMemorizedValue) {
		boolean isMemoForced = "true".equals(forceMemorizedValue); // in case it's null

		String citiesData;
		String articlesData;
		try {
			citiesData = readFileAsString("internal/cities.csv");
			articlesData = readFileAsString("internal/articles-local.json");
		} catch (Exception e) {
			return ArticlesJsonResponse.failure("File Error: Can't read cities.csv or articles-local.json");
		}

		AiCommunicator talkai = query -> openRouterService.getCompletion(query);
		String result = citiesFromAiService.processAi(talkai, isMemoForced, citiesData, articlesData);
		if (result.contains("Error:")) {
			return ArticlesJsonResponse.failure(result);
		}

		List<String> cities = new ArrayList<>();
		for (String city : result.split(",")) {
			cities.add(city.trim());
		}

		Optional<List<Article>> articlesMaybe = articlesFromInternalJsonFile("articles-local.json");
		Optional<List<Article>> articlesGlobalMaybe = articlesFromInternalJsonFile("articles-global.json");
		if (articlesMaybe.isEmpty() || articlesGlobalMaybe.isEmpty()) {
			return ArticlesJsonResponse
					.failure("JSON Error: Can't read or deserialize articles-local.json or articles-global.json file");
		}
		List<Article> articles = articlesMaybe.get();
		List<Article> articlesGlobal = articlesGlobalMaybe.get();

		articles = articleHandlingService.normalizeArticlesFromCities(articles, cities, Article.ArticleKind.LOCAL);
		articlesGlobal = articleHandlingService.normalizeArticles(articlesGlobal, "Global", Article.ArticleKind.GLOBAL);
		articles.addAll(articlesGlobal);

		return ArticlesJsonResponse.success(articles);
	}

	/**
	 * Public for testability — allows mocking in unit tests.
	 */
	public String readFileAsString(String classpathFile) throws Exception {
		ClassPathResource resource = new ClassPathResource(classpathFile);
		try (InputStream inputStream = resource.getInputStream()) {
			return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
		}
	}

	private Optional<List<Article>> articlesFromInternalJsonFile(String filenamePart) {
		String json;
		List<Article> articles;
		try {
			json = readFileAsString("internal/" + filenamePart);
			ObjectMapper mapper = new ObjectMapper();
			return Optional.of(mapper.readValue(json, new TypeReference<List<Article>>() {
			}));
		} catch (Exception e) {
			return Optional.empty();
		}
	}
}
