package se.joas.mysoftware.redditbot;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BotTest {

    String[] credentials = new String[] {"username", "password", "clientId", "clientSecret"};

    Bot bot = new Bot();

    @Test
    void redditLoginCompletes() {
        //assertTrue(bot.login());
    }

    @Test
    void readsCredentialsFromFile() {
        String[] actual = bot.readCredentialsFromFile();
        assertEquals(credentials[0], actual[0]);
        assertEquals(credentials[1], actual[1]);
        assertEquals(credentials[2], actual[2]);
        assertEquals(credentials[3], actual[3]);
    }

}