package com.example.android.newsfeed.Data;


import android.text.TextUtils;
import android.util.Log;

import com.example.android.newsfeed.Article;
import com.example.android.newsfeed.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public final class QueryUtils {

    /**
     * Variable to store the JSON response for a USGS query
     */
    private static String JSON_RESPONSE;

    /**
     * Variable to store the Guardian Request Url
     */
    private static String GUARDIAN_REQUEST_URL = "https://content.guardianapis.com/search?q=";

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    /**
     * This is a sample json response to help us test the last function
     */
    private String SAMPLE_JSON_RESPONSE = theSampleJson();

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {

        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            Log.e(LOG_TAG, "Error with creating URL", exception);
            return null;
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */

    private static String makeHttpRequest(java.net.URL url) throws IOException {
        String jsonResponse = "";

        // Check if the url is null
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            // The internet connection is needed before executing the next line !
            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect(); // Did the connexion lead to a successful transfert ? That's a good question !

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream(); // Check if the inputStream has a bad value, like null, right ?
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code " + urlConnection.getResponseCode());
            }

        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the Article JSON results.", e);
            // TODO: Handle the exception

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // function must handle java.io.IOException here
                inputStream.close();
            }
        }
        return jsonResponse;

        // Returns the actual Json from the URL
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Return a list of {@link Article} by Fetching data from the USGS server
     */

    public static ArrayList<Article> fetchArticlesData(String requestUrl) {

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = "";
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            // TODO Handle the IOException
            Log.e(LOG_TAG, "Problem retrieving the Article JSON results.", e);
        }
        Log.w(LOG_TAG, "This is the \"fetchArticleData\" method");
        return extractArticles(jsonResponse);
    }

    /**
     * Return a list of {@link Article} objects that has been built up from
     * parsing a JSON response.
     */

    public static ArrayList<Article> extractArticles(String jsonResponse) {

        // If the JSON string is empty or null, then return null.
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding Articles to
        ArrayList<Article> articles = new ArrayList<>();

        // Try to parse the jsonResponse. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Convert the string of Json received from the Gardian API to a JSONObject
            // and JSONArray
            JSONObject JSONResponseObject = new JSONObject(jsonResponse);
            JSONArray JSONArticleArray = JSONResponseObject.optJSONArray("results");

            // Loop through the JSONArticleArray to extract informations about each Article
            for (int i = 0; i < JSONArticleArray.length(); i++) {

                // From the VolumeInfo object, get datas (title,averageRating, ratingsCount & infoLink)
                // to create a new Article inside the Article's ArrayList
                JSONObject JSONArticleObject = JSONArticleArray.optJSONObject(i);
                JSONObject JSONArticleFieldObject = JSONArticleObject.optJSONObject("fields");

                // Test the API response, period
                // TesT THE RESPONSE WITH A SAMPLE Json, period !
                articles.add(new Article(JSONArticleObject.optString("webTitle"),
                        JSONArticleFieldObject.optString("trailText"),
                        JSONArticleFieldObject.optString("thumbnail"),
                        JSONArticleObject.optString("webUrl"),
                        JSONArticleObject.optString("webPublicationDate"))) ;
            // Next : Test the API response
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the Article JSON results", e);
        }

        return articles;
    }

    /**
     * This function will extract the authors from the JsonArray and return a List of String
     * that contains all the authors
     */
    private static List<String> extractAuthors(JSONArray JSONAuthorsArray) {

        List<String> authors = new ArrayList<String>();

        if (JSONAuthorsArray != null && JSONAuthorsArray.length() > 0) {
            for (int i = 0; i < JSONAuthorsArray.length(); i++) {
                authors.add(JSONAuthorsArray.optString(i)); // What to do now ?
            }
        }
        return authors;
    }

    /**
     * Returns a String that represent a Json Response from the Gardian API
     */

    public static String theSampleJson() {return "";}

}
