package com.example.android.booklistingapp;

import android.text.TextUtils;
import android.util.Log;

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

/**
 * Created by Steven on 24/02/2017.
 */

public final class HelperMethods {

    //used in the log for specifying where an exception occured
    private static final String LOG_KEY = HelperMethods.class.toString();
    //private constructor; this class is merely to store helper methods, any users should not
    //make an object of this class
    private HelperMethods() {
    }

    /**
     *  method to make a URL out of a String
     * @param s : input String which specifies the URL to request the data
     * @return : the URL whcih will be used for an HTTP request
     */
    private static URL createUrl(String s) {
        URL url = null;
        try {
            url = new URL(s);
        } catch (MalformedURLException e) {
            return null;
        }
        return url;
    }

    /**
     * method which takes in a URL and then performs the HTTP request
     * and returns a rsponse String which is the JSON response corresponding
     * with the input URL
     * @param url : url to get the data from
     * @return : String consisting of the response; i.e. the JSON response
     * @throws IOException
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String response = "";
        HttpURLConnection urlConnection = null;
        InputStream input = null;
        if (url == null) {
            return response;
        }
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            input = urlConnection.getInputStream();
            response = readFromStream(input);
        } catch (IOException e) {
            Log.e(LOG_KEY, "Failed to make HTTP request");
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (input != null) {
                input.close();
            }
        }
        return response;
    }

    /**
     * method used in the HTTPRequest method in order to read the input data
     * @param input : InputStream corresponding with the input data stream of the urlConnection
     * @return : String corresponding with the JSON response
     * @throws IOException
     */
    private static String readFromStream(InputStream input) throws IOException {
        StringBuilder output = new StringBuilder();
        if (input != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(input, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            while (line != null) {
                output.append(line);
                line = bufferedReader.readLine();
            }
        }

        return output.toString();
    }

    /**
     * method used to extract the desired fields from the JSON response, i.e. the JSON response
     * is read and the fields needed to create Book Objects are extracted from it
     * @param bookJSON : JSON response string
     * @return List of Book Objects, where every Book object contains Title, Authors,...
     */
    public static List<Book> extractFeaturesFromJson(String bookJSON) {

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(bookJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding Books to
        List<Book> bookList = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // build up a list of Book objects with the corresponding data.
            JSONObject root = new JSONObject(bookJSON);
            JSONArray items = root.getJSONArray("items");

            for (int i = 0; i < items.length(); i++) {
                JSONObject currentBook = items.getJSONObject(i);
                JSONObject bookInfo = currentBook.getJSONObject("volumeInfo");
                String title = bookInfo.getString("title");
                // not every currentBook has a subtitle, so here we first declare it zero. If the
                // currentBook does have a subtitle, we modify the value to the right value in
                //the if statement.
                String subtitle =null;
                if(!bookInfo.isNull("subtitle")){
                    subtitle = bookInfo.getString("subtitle");
                }

                JSONArray authorsArray = bookInfo.getJSONArray("authors");
                StringBuilder authors = new StringBuilder();
                for (int j = 0; j < authorsArray.length(); j++) {
                    authors.append(authorsArray.get(j).toString() + "\n");
                }

                // same reasoning as above
                String description = null;
                if(!bookInfo.isNull("description")){
                    description = bookInfo.getString("description");
                }

                Book bookToAdd = new Book(title, authors.toString(),subtitle,description);
                bookList.add(bookToAdd);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e(LOG_KEY, "Problem parsing the Book JSON results", e);
        }

        // Return the list of Books
        return bookList;
    }

    /**
     * method combining all the necessary steps to create a URL from an input string, making an
     * HTTP request,, reading the data from the connection and extracting the required fields from
     * the JSON response in order create a List of Book Objects
     * @param requestUrl : String containing the url to start with
     * @return : the Lidt of Book objects
     */
    public static List<Book> fetchBooks(String requestUrl) {

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_KEY, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Earthquake}s
        List<Book> bookList = extractFeaturesFromJson(jsonResponse);

        // Return the list of {@link Earthquake}s
        return bookList;
    }
}
