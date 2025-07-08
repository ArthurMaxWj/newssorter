package com.amwojcik.newssorter.services;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class CitiesFromAiService {

	public final Pattern REGEXP_PATTERN = Pattern.compile("[a-zA-Z\\s.']+(,[a-zA-Z\\s.']+)*");

	@FunctionalInterface
	public interface AiCommunicator {
		String runQuery(String query);
	}

	public String processAi(AiCommunicator talkai, boolean forceMemo, String cities, String articles) {
		String query = chatQuery(cities, articles);

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
				searchResult = talkai.runQuery(query);
			} catch (HttpClientErrorException e) {
				return "Processing Error: Probably wrong credentials for AI API";
			}
		}

		// some modles don't return only CSV format
		if (isResultOk(searchResult)) {
			return obtainSignificantPart(searchResult);
		} else {
			return "Processing Error: AI returned something I can't parse.";
		}
	}

	private String chatQuery(String cities, String articles) {
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
	 * Filters out unnecessary information from the response, extracting only the
	 * relevant data if the result is embedded in a larger answer.
	 */
	private String obtainSignificantPart(String res) {
		Pattern pattern = REGEXP_PATTERN;
		Matcher matcher = pattern.matcher(res);
		boolean matchFound = matcher.find();
		return matcher.group(0);
	}
}
