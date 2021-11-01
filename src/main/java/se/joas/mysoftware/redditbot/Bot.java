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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class Bot {

    private static RedditClient redditClient;

    public static void main(String[] args) {
        Bot bot = new Bot();
        bot.auth(bot.readUserAgentFromFile(), bot.readCredentialsFromFile());
    }

    // Maybe useful in future
    private void findThreadInSubredditBasedOnTitle(String subredditName, String postTitle) {
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

    private void makeCommentReplyInThread(String threadId, String commentId, String reply) {
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

    private void deleteCommentInThread(String threadId) {
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

    private void makeParentCommentInThread(String threadId, String comment) {
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

    private void listCommentsInThread(String threadId) {
        RootCommentNode rootCommentNode = redditClient.submission(threadId).comments();
        Iterator<CommentNode<PublicContribution<?>>> it = rootCommentNode.walkTree().iterator();
        while (it.hasNext()) {
            PublicContribution<?> thing = it.next().getSubject();
            if (thing instanceof Comment) {
                System.out.println(thing.getBody());
            }
        }
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

    // TODO: make file into .properties files
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
