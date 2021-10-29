package se.joas.mysoftware.redditbot;

import org.junit.jupiter.api.Test;

class BotTest {

    String[] credentials = new String[] {"username", "password", "clientId", "clientSecret"};

    Bot bot = new Bot();

    @Test
    void redditLoginCompletes() {
        //assertTrue(bot.login());
    }

}