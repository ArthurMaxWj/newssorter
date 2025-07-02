package com.amwojcik.newssorter.controllers;

import com.amwojcik.newssorter.services.OpenRouterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;

import org.springframework.core.io.ClassPathResource;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class ChatController {

    private final Pattern REGEXP_PATTERN = Pattern.compile("[a-zA-Z\\s.']+(,[a-zA-Z\\s.']+)*");

    @Autowired
    private OpenRouterService openRouterService;

    @GetMapping("/")
    public String home() {
      return "redirect:talkai";
    }

    @GetMapping("/talkai")
    @ResponseBody
    public String talkai(@RequestParam(value = "city", defaultValue = "Oklahoma City") String city) {

        String query;
        try {
            query = chatQuery();
        } catch (Exception e) {
            return "Internal files of articles or cities might have been corrupted.";
        }

        // return String.format("You asked: <pre>%s</pre>", query);
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
        
        // searchResult openRouterService.getCompletion(query);

        if (isResultOk(searchResult)) {
            return String.format("Result of search: <pre>%s</pre>", obtainSignificantPart(searchResult));
        } else {
            return "AI returned something I can't parse.";
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

    private String obtainSignificantPart(String res) {
        Pattern pattern = REGEXP_PATTERN;
        Matcher matcher = pattern.matcher(res);
        boolean matchFound = matcher.find();
        return matcher.group(0);
    }

}
