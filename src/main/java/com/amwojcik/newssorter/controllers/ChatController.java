package com.amwojcik.newssorter.controllers;

import com.amwojcik.newssorter.services.OpenRouterService;
import com.amwojcik.newssorter.models.articles.Article;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;


import org.springframework.core.io.ClassPathResource;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.List;
import java.util.ArrayList;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


@Controller
public class ChatController {

    private final Pattern REGEXP_PATTERN = Pattern.compile("[a-zA-Z\\s.']+(,[a-zA-Z\\s.']+)*");

    @Autowired
    private OpenRouterService openRouterService;

    @GetMapping("/")
    public String home() {
        return "articles/home";
    }

    @GetMapping("/static-all")
    public String staticall(Model model) {
        List<Article> articles = new ArrayList<Article>();
        articles.add(new Article("hiii", "content", "2025", Article.ArticleKind.LOCAL));
        articles.add(new Article("hello", "more content", "2025", Article.ArticleKind.LOCAL));
        articles.add(new Article("heyy", "some content lol", "2025", Article.ArticleKind.LOCAL));

        model.addAttribute("articleslist", articles);
        return "articles/staticall";
    }

    @GetMapping("/dynamic-all")
    public String dynamicall(@RequestParam(name = "forcememo", required = false) String forceMemorizedValue, Model model) {
        boolean isMemoForced = "on".equals(forceMemorizedValue);

        // TODO: use parameter later in project
        String result = processAi(true); 
        if (result.startsWith("Processing error:")) {
            model.addAttribute("error", result);
            return "articles/error";
        }

        List<String> cities = new ArrayList<>();
        for (String city : result.split(",")) {
            cities.add(city.trim());
        }

        // JSON into List of Articles
        String json;
        List<Article> articles;
        try {
            json = readFileAsString("internal/articles.json");
            ObjectMapper mapper = new ObjectMapper();
            articles = mapper.readValue(json, new TypeReference<List<Article>>() {});
        } catch (Exception e) {
            model.addAttribute("error", "Can't read or deserialize articles.json file");
            return "articles/error";
        }
        
        
        for (int i = 0; i < articles.size(); i++) {
            Article a = articles.get(i);
            a.setCity(cities.get(i));
            articles.set(i, a);
        }

        model.addAttribute("articleslist", articles);
        return "articles/dynamicall";
    }

    public String processAi(boolean forceMemo) {
        String query;
        try {
            query = chatQuery();
        } catch (Exception e) {
            return "Processing error: Internal files of articles or cities might have been corrupted.";
        }

        String searchResult = """
                Perrysburg, Toledo, Toledo, Toledo, Toledo, Oklahoma City, Oklahoma City,
                Oklahoma City, Oklahoma City, Portland, Portland, Portland, Portland, Portland, Portland,
                Portland, Portland, Portland, Portland, Perrysburg, Seattle, Seattle, Seattle, Seattle, Seattle,
                Seattle, Seattle, San Jose, San Jose, San Jose, Denver, Aurora, Phoenix, Tempe, Richmond, Charleston,
                Unknown, Boston, Madison, Unknown, Omaha, Omaha, Omaha, Omaha, Omaha, Omaha, Omaha, Omaha, Omaha, Omaha,
                Omaha, Omaha, San Antonio, Indianapolis, Unknown, Cleveland, Albuquerque, Tampa, Milwaukee, Unknown,
                Las Vegas, Louisville, Nashville, Salt Lake City, Charlotte, Detroit, Columbus, Memphis, Oklahoma City,
                Salt Lake City, Charlotte, Nashville, Grand Rapids, Boise, Albuquerque, Cleveland, Durham, Wichita,
                St. Louis, Reno, Anchorage, Madison, Little Rock,
                Spokane, Des Moines, Fayetteville, Huntsville, Augusta, Sioux Falls, Unknown, Topeka, Akron
                """;
        
        if (!forceMemo) {
            openRouterService.getCompletion(query);
        }

        if (searchResult.startsWith("Processing error:")) { // API error
            return searchResult;
        }

        // some modles don't return only CSV format
        if (isResultOk(searchResult)) {
            return obtainSignificantPart(searchResult);
        } else {
            return "Processing error: AI returned something I can't parse.";
        }


    }

    

    private String readFileAsString(String classpathFile) throws Exception {
        ClassPathResource resource = new ClassPathResource(classpathFile);
        try (InputStream inputStream = resource.getInputStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private String chatQuery() throws Exception {
        String cities = readFileAsString("internal/cities.csv");
        String articles = readFileAsString("internal/articles.json");

        String query = """
                I will send you a JSON file. Each record in the array is a new article.
                I want you to use the title and content to determine which city the article might belong to.
                Send me only a list of cities separated with commas (in order preserving correspondence to articles).
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
     * If the result is contained in the answer, 
     * but there is also other unnecessary information is present,
     * filter only the data we need.
     */
    private String obtainSignificantPart(String res) {
        Pattern pattern = REGEXP_PATTERN;
        Matcher matcher = pattern.matcher(res);
        boolean matchFound = matcher.find();
        return matcher.group(0);
    }

}
