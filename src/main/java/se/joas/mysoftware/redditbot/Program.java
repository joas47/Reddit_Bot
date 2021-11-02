package se.joas.mysoftware.redditbot;

import java.util.Scanner;

public class Program {

    private final Scanner input = new Scanner(System.in);

    private static Search youTubeSearch;
    private static Bot redditBot;

    public static void main(String[] args) {
        Program program = new Program();
        program.run();

    }

    private void run() {
        initRedditBot();
        initYouTubeSearcher();
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
        String subreddit = userInput("Which subreddit to post in?> ");
        youTubeSearch.postFirstYoutubeResultToReddit(searchTerm, subreddit);
    }

    private void printError() {
        System.err.println("Error: Unknown command. ");
        printCommands();
    }

    private void printCommands() {
        String[] commands = {"exit", "find thread"};
        System.out.println("The following commands exists: ");
        for (String string : commands) {
            System.out.println("* " + string);
        }
    }

    private String readCommand() {
        System.out.print("Command?> ");
        return input.nextLine();
    }

    private void initYouTubeSearcher() {
        youTubeSearch = new Search();
    }

    private void initRedditBot() {
        redditBot = new Bot();
    }

}
