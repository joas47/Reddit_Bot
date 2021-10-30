package se.joas.mysoftware.redditbot;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkAdapter;
import net.dean.jraw.http.OkHttpNetworkAdapter;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.models.Flair;
import net.dean.jraw.models.SubmissionKind;
import net.dean.jraw.oauth.Credentials;
import net.dean.jraw.oauth.OAuthHelper;
import net.dean.jraw.references.SubredditReference;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class Bot {

    private static RedditClient redditClient;

    public static void main(String[] args) {
        Bot bot = new Bot();
        bot.auth(bot.readUserAgentFromFile(), bot.readCredentialsFromFile());

    }

    private void makeSelfPost(String subredditName, String title, String content) {
        SubredditReference subredditReference = redditClient.subreddit(subredditName);
        subredditReference.submit(SubmissionKind.SELF, title, content, false);
    }

    // Sets user flair to the first item in the list
    private void setUserFlair(String subredditName) {
        SubredditReference subreddit = redditClient.subreddit(subredditName);
        List<Flair> userFlairOptions = subreddit.userFlairOptions();
        if (!userFlairOptions.isEmpty()) {
            Flair newFlair = userFlairOptions.get(0);
            subreddit.selfUserFlair().updateToTemplate(newFlair.getId(), "");
        }
    }

    // TODO:
    private void commentInSubreddit(SubredditReference subredditReference, String threadId) {
        redditClient.comment("mm5xu9");
        //RootCommentNode  root = redditClient.submission(threadId).comments();
    }

    public String[] readCredentialsFromFile() {
        try {
            FileReader fileReader = new FileReader("src/main/java/se/joas/mysoftware/redditbot/credentials.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine();
            return line.split(",");

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String[0];
    }

    public String[] readUserAgentFromFile() {
        try {
            FileReader fileReader = new FileReader("src/main/java/se/joas/mysoftware/redditbot/userAgent.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine();
            return line.split(",");

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String[0];
    }

    public void auth(String[] userAgents, String[] credential) {
        UserAgent userAgent = new UserAgent(userAgents[0], userAgents[1], userAgents[2], userAgents[3]);
        Credentials credentials = Credentials.script(credential[0], credential[1], credential[2], credential[3]);
        NetworkAdapter adapter = new OkHttpNetworkAdapter(userAgent);
        redditClient = OAuthHelper.automatic(adapter, credentials);
    }


}
