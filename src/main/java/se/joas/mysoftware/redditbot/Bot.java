package se.joas.mysoftware.redditbot;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkAdapter;
import net.dean.jraw.http.OkHttpNetworkAdapter;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.oauth.Credentials;
import net.dean.jraw.oauth.OAuthHelper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Bot {

    public static void main(String[] args) {
        Bot bot = new Bot();

        bot.auth(bot.readUserAgentFromFile(), bot.readCredentialsFromFile());
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
        RedditClient redditClient = OAuthHelper.automatic(adapter, credentials);
    }


}
