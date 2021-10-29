package se.joas.mysoftware.redditbot;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkAdapter;
import net.dean.jraw.http.OkHttpNetworkAdapter;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.oauth.Credentials;
import net.dean.jraw.oauth.OAuthHelper;

public class Bot {

    public void readCredentialsFromFile() {

    }

    public void run(String platform, String appId, String version, String redditUsername,
                    String username, String password, String clientId, String clientSecret) {
        UserAgent userAgent = new UserAgent(platform, appId, version, redditUsername);
        Credentials credentials = Credentials.script(username, password, clientId, clientSecret);
        NetworkAdapter adapter = new OkHttpNetworkAdapter(userAgent);
        RedditClient redditClient = OAuthHelper.automatic(adapter, credentials);
    }


}
