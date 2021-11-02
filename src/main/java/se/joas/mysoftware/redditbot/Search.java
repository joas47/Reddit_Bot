package se.joas.mysoftware.redditbot;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

public class Search {

    private static final String PROPERTIES_FILENAME = "youtube.properties";
    private static final long NUMBER_OF_VIDEOS_RETURNED = 1;
    private static final String YOUTUBE_URL = "https://www.youtube.com/watch?v=";

    private static YouTube youtube;
    private static Bot redditBot;

    public static void main(String[] args) {
        Search search = new Search();
        search.postFirstYoutubeResultToReddit("MandaloreGaming", "secrettestsite2");
/*        Properties properties = new Properties();
        try {
            InputStream in = Search.class.getResourceAsStream("/" + PROPERTIES_FILENAME);
            properties.load(in);

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, new HttpRequestInitializer() {
                public void initialize(HttpRequest request) throws IOException {
                }
            }).setApplicationName("youtube-cmdline-search-sample").build();

            String queryTerm = getInputQuery();

            YouTube.Search.List search = youtube.search().list(Collections.singletonList("id,snippet"));

            String apiKey = properties.getProperty("youtube.apikey");
            search.setKey(apiKey);
            search.setQ(queryTerm);

            search.setType(Collections.singletonList("video"));

            search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
            search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);

            SearchListResponse searchResponse = search.execute();
            List<SearchResult> searchResultList = searchResponse.getItems();
            if (searchResultList != null) {
                prettyPrint(searchResultList.iterator(), queryTerm);
            }
        } catch (GoogleJsonResponseException e) {
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : ");
        } catch (IOException e) {
            System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }*/
    }

    private static String getInputQuery() throws IOException {

        String inputQuery;

        System.out.print("Please enter a search term: ");
        BufferedReader bReader = new BufferedReader(new InputStreamReader(System.in));
        inputQuery = bReader.readLine();

        if (inputQuery.length() < 1) {
            // Use the string "YouTube Developers Live" as a default.
            inputQuery = "YouTube Developers Live";
        }
        return inputQuery;
    }

    private static void prettyPrint(Iterator<SearchResult> iteratorSearchResults, String query) {

        System.out.println("\n=============================================================");
        System.out.println(
                "   First " + NUMBER_OF_VIDEOS_RETURNED + " videos for search on \"" + query + "\".");
        System.out.println("=============================================================\n");

        if (!iteratorSearchResults.hasNext()) {
            System.out.println(" There aren't any results for your query.");
        }

        while (iteratorSearchResults.hasNext()) {

            SearchResult singleVideo = iteratorSearchResults.next();
            ResourceId rId = singleVideo.getId();

            // Confirm that the result represents a video. Otherwise, the
            // item will not contain a video ID.
            if (rId.getKind().equals("youtube#video")) {
                Thumbnail thumbnail = singleVideo.getSnippet().getThumbnails().getDefault();

                System.out.println(" Video Id: " + rId.getVideoId());
                System.out.println(" Title: " + singleVideo.getSnippet().getTitle());
                System.out.println(" Thumbnail: " + thumbnail.getUrl());
                System.out.println("\n-------------------------------------------------------------\n");
            }
        }
    }

    public void postFirstYoutubeResultToReddit(String searchTerm, String subreddit) {
        try {
            YouTube.Search.List search = initYouTubeSearch();
            redditBot = new Bot();
            assembleSearch(searchTerm, search);

            SearchListResponse searchResponse = search.execute();
            List<SearchResult> searchResultList = searchResponse.getItems();
            Iterator<SearchResult> iteratorSearchResults = searchResultList.iterator();
            if (searchResultList != null) {
                if (!iteratorSearchResults.hasNext()) {
                    throw new IllegalArgumentException(" There aren't any results for this query.");
                }
                while (iteratorSearchResults.hasNext()) {

                    SearchResult singleVideo = iteratorSearchResults.next();
                    ResourceId rId = singleVideo.getId();

                    // Confirm that the result represents a video. Otherwise, the
                    // item will not contain a video ID.
                    if (rId.getKind().equals("youtube#video")) {
                        String videoId = YOUTUBE_URL + rId.getVideoId();
                        String videoTitle = singleVideo.getSnippet().getTitle();
                        redditBot.makeLinkPost(subreddit, videoTitle, videoId);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void assembleSearch(String searchTerm, YouTube.Search.List search) {
        String apiKey = getApiKey();
        search.setKey(apiKey);
        search.setQ(searchTerm);
        search.setType(Collections.singletonList("video"));
        search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
        search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
    }

    private String getApiKey() {
        Properties properties = new Properties();
        try {
            InputStream in = Search.class.getResourceAsStream("/" + PROPERTIES_FILENAME);
            properties.load(in);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties.getProperty("youtube.apikey");
    }

    private YouTube.Search.List initYouTubeSearch() throws IOException {
        youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, new HttpRequestInitializer() {
            public void initialize(HttpRequest request) throws IOException {
            }
        }).setApplicationName("youtube-cmdline-search-sample").build();

        return youtube.search().list(Collections.singletonList("id,snippet"));
    }
}
