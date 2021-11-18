package com.bot.demo;

import com.amazonaws.services.lambda.runtime.Context;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TwitterBotTest {

    @Test
    public void testSendingTweet() {
        Context context = new TestContext();
        TwitterBot twitterBot = new TwitterBot();
        Map<String,String> event = new HashMap<>();

        event.put("ACCESS_TOKEN", System.getProperty("ACCESS_TOKEN"));
        event.put("ACCESS_TOKEN_SECRET", System.getProperty("ACCESS_TOKEN_SECRET"));
        event.put("API_KEY", System.getProperty("API_KEY"));
        event.put("API_SECRET_KEY", System.getProperty("API_SECRET_KEY"));
        event.put("USER_ID", System.getProperty("USER_ID"));

        twitterBot.pathToTweets = Paths.get("src/test/resources","tweets.txt");
        String result = twitterBot.tweetQuotes(event, context);

        assertTrue(result.contains("Tweet: " + event.get("quote")));
    }
}