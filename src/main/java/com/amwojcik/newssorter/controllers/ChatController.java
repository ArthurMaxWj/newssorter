package com.amwojcik.newssorter.controllers;

import com.amwojcik.newssorter.models.articles.Article;
import com.amwojcik.newssorter.services.OpenRouterService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

@RestController
@RequestMapping("/api")
public class ChatController {

	private final Pattern REGEXP_PATTERN = Pattern.compile("[a-zA-Z\\s.']+(,[a-zA-Z\\s.']+)*");

	@Autowired
	private OpenRouterService openRouterService;

	@GetMapping("/static-all")
	public String staticall(Model model) {
		Optional<List<Article>> articlesMaybe = articlesFromInternalJsonFile("articles-local");
		if (articlesMaybe.isEmpty()) {
			return wrapFailure("JSON Error: Can't read or deserialize articles-local.json file");
		}
		List<Article> articles = articlesMaybe.get();

		for (int i = 0; i < articles.size(); i++) {
			Article a = articles.get(i);
			a.setCity("Unknown");
			a.setKind(Article.ArticleKind.LOCAL);
			articles.set(i, a);
		}

		Optional<List<Article>> articlesGlobalMaybe = articlesFromInternalJsonFile("articles-global");
		if (articlesMaybe.isEmpty()) {
			return wrapFailure("JSON Error: Can't read or deserialize articles-global.json file");
		}
		List<Article> articlesGlobal = articlesGlobalMaybe.get();

		for (int i = 0; i < articlesGlobal.size(); i++) {
			Article a = articlesGlobal.get(i);
			a.setCity("Global");
			a.setKind(Article.ArticleKind.GLOBAL);
			articles.add(a); // append to articles
		}

		Optional<String> resMaybe = articlesToJson(articles);
		if (resMaybe.isEmpty()) {
			return wrapFailure("JSON Error: Can't serialize articles into JSON");
		}

		return wrapSuccess(resMaybe.get());
	}

	@GetMapping("/dynamic-all")
	public String dynamicall(@RequestParam(name = "forcememo", required = false) String forceMemorizedValue,
			Model model) {
		boolean isMemoForced = "true".equals(forceMemorizedValue); // in case it's null

		String result = processAi(isMemoForced);
		if (result.contains("Error:")) {
			return wrapFailure(result);
		}

		List<String> cities = new ArrayList<>();
		for (String city : result.split(",")) {
			cities.add(city.trim());
		}

		Optional<List<Article>> articlesMaybe = articlesFromInternalJsonFile("articles-local");
		if (articlesMaybe.isEmpty()) {
			return wrapFailure("JSON Error: Can't read or deserialize articles-local.json file");
		}
		List<Article> articles = articlesMaybe.get();

		for (int i = 0; i < articles.size(); i++) {
			Article a = articles.get(i);
			a.setCity(cities.get(i));
			a.setKind(Article.ArticleKind.LOCAL);
			articles.set(i, a);
		}

		Optional<List<Article>> articlesGlobalMaybe = articlesFromInternalJsonFile("articles-global");
		if (articlesMaybe.isEmpty()) {
			return wrapFailure("JSON Error: Can't read or deserialize articles-global.json file");
		}
		List<Article> articlesGlobal = articlesGlobalMaybe.get();

		for (int i = 0; i < articlesGlobal.size(); i++) {
			Article a = articlesGlobal.get(i);
			a.setCity("Global");
			a.setKind(Article.ArticleKind.GLOBAL);
			articles.add(a); // append to articles
		}

		Optional<String> resMaybe = articlesToJson(articles);
		if (resMaybe.isEmpty()) {
			return wrapFailure("JSON Error: Can't serialize articles into JSON");
		}

		return wrapSuccess(resMaybe.get());
	}

	public String processAi(boolean forceMemo) { // don't wrap results at this stage
		String query;
		try {
			query = chatQuery();
		} catch (Exception e) {
			return "Processing Error: Internal files of articles or cities might have been corrupted.";
		}

		String searchResult = """
				Perrysburg, Toledo, Toledo, Toledo, Toledo, Oklahoma City, Oklahoma City,
				Oklahoma City, Oklahoma City, Portland, Portland, Portland, Portland, Portland,
				    Portland, Portland, Portland, Portland, Portland, Perrysburg, Seattle, Seattle,
				    Seattle, Seattle, Seattle, Seattle, Seattle, San Jose, San Jose, San Jose, Denver,
				    Aurora, Phoenix, Tempe, Richmond, Charleston, Unknown, Boston, Madison, Unknown,
				    Omaha, Omaha, Omaha, Omaha, Omaha, Omaha, Omaha, Omaha, Omaha, Omaha,Omaha,
				    Omaha, San Antonio, Indianapolis, Unknown, Cleveland, Albuquerque, Tampa, Milwaukee,
				    Unknown, Las Vegas, Louisville, Nashville, Salt Lake City, Charlotte, Detroit, Columbus,
				    Memphis, Oklahoma City, Salt Lake City, Charlotte, Nashville, Grand Rapids, Boise,
				    Albuquerque, Cleveland, Durham, Wichita, St. Louis, Reno, Anchorage, Madison,
				    Little Rock, Spokane, Des Moines, Fayetteville, Huntsville, Augusta, Sioux Falls,
				    Unknown, Topeka, Akron
				""";

		if (!forceMemo) {
			try {
				openRouterService.getCompletion(query);
			} catch (HttpClientErrorException e) {
				return "Processing Error: Probably wrong credentials for AI API";
			}
		}

		if (searchResult.contains("Error:")) { // API error
			return searchResult;
		}

		// some modles don't return only CSV format
		if (isResultOk(searchResult)) {
			return obtainSignificantPart(searchResult);
		} else {
			return "Processing Error: AI returned something I can't parse.";
		}
	}

	private String chatQuery() throws Exception {
		String cities = readFileAsString("internal/cities.csv");
		String articles = readFileAsString("internal/articles-local.json");

		String query = """
				I will send you a JSON file. Each record in the array is a new article.
				I want you to use the title and content to determine
				    which city the article might belong to.
				Send me only a list of cities separated with commas
				    (in order preserving correspondence to articles).
				If you can't match the article to the city, mark it as Unknown.
				Plese inlcude the following cities in your consideration: %s.

				Also consider big landmarks and well-known streets as well as counties' names.
				Here is the JSON with articles: %s.
				""";
		return String.format(query, cities, articles);
	}

	private boolean isResultOk(String res) {
		Pattern pattern = REGEXP_PATTERN;
		Matcher matcher = pattern.matcher(res);

		return matcher.find();
	}

	/**
	 * If the result is contained in the answer, but there is also other unnecessary
	 * information is present, filter only the data we need.
	 */
	private String obtainSignificantPart(String res) {
		Pattern pattern = REGEXP_PATTERN;
		Matcher matcher = pattern.matcher(res);
		boolean matchFound = matcher.find();
		return matcher.group(0);
	}

	public String readFileAsString(String classpathFile) throws Exception {
		ClassPathResource resource = new ClassPathResource(classpathFile);
		try (InputStream inputStream = resource.getInputStream()) {
			return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
		}
	}

	private Optional<String> articlesToJson(List<Article> articles) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return Optional.of(mapper.writeValueAsString(articles));
		} catch (JsonProcessingException e) {
			return Optional.empty();
		}
	}

	private Optional<List<Article>> articlesFromInternalJsonFile(String partialFileName) {
		String fullFilename = String.format("internal/%s.json", partialFileName);

		String json;
		List<Article> articles;
		try {
			json = readFileAsString(fullFilename);
			ObjectMapper mapper = new ObjectMapper();
			return Optional.of(mapper.readValue(json, new TypeReference<List<Article>>() {
			}));
		} catch (Exception e) {
			return Optional.empty();
		}
	}

	private String wrapFailure(String error) {
		return String.format("""
				{
				    "success": "false",
				    "error": "%s"
				}
				""", error);
	}

	private String wrapSuccess(String articlesJson) {
		return String.format("""
				{
				    "success": "true",
				    "articles": %s
				}
				""", articlesJson);
	}
}
