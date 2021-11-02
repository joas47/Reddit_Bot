package se.joas.mysoftware.redditbot;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkAdapter;
import net.dean.jraw.http.OkHttpNetworkAdapter;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.models.*;
import net.dean.jraw.oauth.Credentials;
import net.dean.jraw.oauth.OAuthHelper;
import net.dean.jraw.pagination.DefaultPaginator;
import net.dean.jraw.references.CommentReference;
import net.dean.jraw.references.SubmissionReference;
import net.dean.jraw.references.SubredditReference;
import net.dean.jraw.tree.CommentNode;
import net.dean.jraw.tree.RootCommentNode;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

public class Bot {

    private static RedditClient redditClient;
    private static final String CREDENTIALS_FILENAME = "credentials.properties";
    private static final String USER_AGENT_FILENAME = "userAgent.properties";

    public static void main(String[] args) {

    }

    public Bot() {
        authenticate();
    }

    // Maybe useful in future
    public void findThreadInSubredditBasedOnTitle(String subredditName, String postTitle) {
        SubredditReference subreddit = redditClient.subreddit(subredditName);
        DefaultPaginator<Submission> subredditNew = subreddit.posts().sorting(SubredditSort.NEW).limit(10).build();
        Listing<Submission> submissions = subredditNew.next();
        String submissionId = "";
        for (Submission s : submissions) {
            if (s.getTitle().equals(postTitle)) {
                submissionId = s.getId();
            }
        }
    }

    public void makeCommentReplyInThread(String threadId, String commentId, String reply) {
        RootCommentNode rootCommentNode = redditClient.submission(threadId).comments();
        Iterator<CommentNode<PublicContribution<?>>> it = rootCommentNode.walkTree().iterator();
        while (it.hasNext()) {
            PublicContribution<?> thing = it.next().getSubject();
            if (thing instanceof Comment && thing.getId().equals(commentId)) {
                CommentReference commentReference = new CommentReference(redditClient, thing.getId());
                commentReference.reply(reply);
            }
        }
    }

    public void deleteCommentInThread(String threadId) {
        RootCommentNode rootCommentNode = redditClient.submission(threadId).comments();
        Iterator<CommentNode<PublicContribution<?>>> it = rootCommentNode.walkTree().iterator();
        while (it.hasNext()) {
            PublicContribution<?> thing = it.next().getSubject();
            if (thing instanceof Comment) {
                CommentReference commentReference = new CommentReference(redditClient, thing.getId());
                commentReference.delete();
            }
        }
    }

    public void makeParentCommentInThread(String threadId, String comment) {
        RootCommentNode rootCommentNode = redditClient.submission(threadId).comments();
        Iterator<CommentNode<PublicContribution<?>>> it = rootCommentNode.walkTree().iterator();
        while (it.hasNext()) {
            PublicContribution<?> thing = it.next().getSubject();
            if (thing instanceof Submission) {
                SubmissionReference submissionReference = new SubmissionReference(redditClient, thing.getId());
                submissionReference.reply(comment);
            }
        }
    }

    public void makeSelfPost(String subredditName, String title, String content) {
        SubredditReference subredditReference = redditClient.subreddit(subredditName);
        subredditReference.submit(SubmissionKind.SELF, title, content, false);
    }

    public void makeLinkPost(String subredditName, String title, String url) {
        SubredditReference subredditReference = redditClient.subreddit(subredditName);
        subredditReference.submit(SubmissionKind.LINK, title, url, false);
    }

    // Sets user flair to the first item in the list
    public void setUserFlair(String subredditName) {
        SubredditReference subreddit = redditClient.subreddit(subredditName);
        List<Flair> userFlairOptions = subreddit.userFlairOptions();
        if (!userFlairOptions.isEmpty()) {
            Flair newFlair = userFlairOptions.get(0);
            subreddit.selfUserFlair().updateToTemplate(newFlair.getId(), "");
        }
    }

    public void listCommentsInThread(String threadId) {
        RootCommentNode rootCommentNode = redditClient.submission(threadId).comments();
        Iterator<CommentNode<PublicContribution<?>>> it = rootCommentNode.walkTree().iterator();
        while (it.hasNext()) {
            PublicContribution<?> thing = it.next().getSubject();
            if (thing instanceof Comment) {
                System.out.println(thing.getBody());
            }
        }
    }

    private Credentials readCredentials() {
        Properties properties = new Properties();
        try {
            InputStream in = Bot.class.getResourceAsStream("/" + CREDENTIALS_FILENAME);
            properties.load(in);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return Credentials.script(
                properties.getProperty("reddit.accountName"),
                properties.getProperty("reddit.password"),
                properties.getProperty("reddit.clientId"),
                properties.getProperty("reddit.secretId")
        );
    }

    private UserAgent readUserAgent() {
        Properties properties = new Properties();
        try {
            InputStream in = Bot.class.getResourceAsStream("/" + USER_AGENT_FILENAME);
            properties.load(in);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new UserAgent(
                properties.getProperty("client"),
                properties.getProperty("domain"),
                properties.getProperty("version"),
                properties.getProperty("reddit.accountName")
        );
    }

    private void authenticate() {
        NetworkAdapter adapter = new OkHttpNetworkAdapter(readUserAgent());
        redditClient = OAuthHelper.automatic(adapter, readCredentials());
    }


}
