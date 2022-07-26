package com.bot.demo.functions;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TwitterBotTest {

    @Test
    public void testSendingTweet() {
        TwitterBot twitterBot = new TwitterBot();
        Map<String,String> event = new HashMap<>();

        event.put("SECRET_NAME", System.getProperty("SECRET_NAME"));
        event.put("REGION", System.getProperty("REGION"));
        event.put("USER_ID", System.getProperty("USER_ID"));

        twitterBot.quotesFile = "src/test/resources/tweets.txt";
        String result = twitterBot.apply(event);

        assertTrue(result.contains("Tweet: " + event.get("quote")));
    }
}