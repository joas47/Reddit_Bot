package se.joas.mysoftware.redditbot;

import java.util.ArrayList;
import java.util.Scanner;

public class Program {

    private final Scanner input = new Scanner(System.in);

    private static Search youTubeSearch = new Search();
    private static Bot redditBot = new Bot();

    private ArrayList<String> seenVideos = new ArrayList<>();

    public static void main(String[] args) {
        Program program = new Program();
        program.run();
    }

    private void run() {
        runCommandLoop();
        exit();
    }

    private void exit() {
        input.close();
        System.exit(0);
    }

    private void runCommandLoop() {
        String command;
        do {
            command = readCommand();
            handleCommand(command);
        } while (!command.equals("exit"));
    }

    // TODO: add cases
    private void handleCommand(String command) {
        switch (command) {
            case "youtube search":
            case "ys":
                youTubeSearch();
                break;
            case "find thread":
                findThread();
                break;
            case "exit":
                break;
            default:
                printError();
        }
    }

    private String userInput(String message) {
        String output;
        do {
            System.out.print(message);
            output = input.nextLine();
            if (output.isBlank()) {
                System.err.println("Error: input can't be empty!");
                System.out.println();
            }
        } while (output.isBlank());
        return output.toLowerCase();
    }

    private void findThread() {
        String subreddit = userInput("Name of subreddit?> ");
        String title = userInput("Name of thread?> ");
        redditBot.findThreadInSubredditBasedOnTitle(subreddit, title);
    }

    private void youTubeSearch() {
        String searchTerm = userInput("Search term?> ");
        String[] searchResult = youTubeSearch.searchResult(searchTerm);
        System.out.println("Search result: " + searchResult[0]);

        if (!seenVideos.contains(searchResult[1])) {
            System.out.println("Video not seen before!");
            makeRedditPost(searchResult);
        } else {
            System.out.println("Video already seen...");
        }
    }

    private void makeRedditPost(String[] searchResult) {
        String subreddit = userInput("Which subreddit to post in?> ");
        String videoTitle = searchResult[0];
        String videoUrl =  "https://www.youtube.com/watch?v=" + searchResult[1];
        redditBot.makeLinkPost(subreddit, videoTitle, videoUrl);
        seenVideos.add(searchResult[1]);
        System.out.println("Post made in " + subreddit);
    }

    private void printError() {
        System.out.println("Error: Unknown command. ");
        printCommands();
    }

    private void printCommands() {
        String[] commands = {"exit", "find thread", "youtube search"};
        System.out.println("The following commands exists: ");
        for (String string : commands) {
            System.out.println("* " + string);
        }
    }

    private String readCommand() {
        System.out.print("Command?> ");
        return input.nextLine();
    }

}
