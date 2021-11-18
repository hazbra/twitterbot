package com.bot.demo;

import com.amazonaws.services.lambda.runtime.Context;
import io.github.redouane59.twitter.TwitterClient;
import io.github.redouane59.twitter.dto.endpoints.AdditionalParameters;
import io.github.redouane59.twitter.dto.tweet.TweetList;
import io.github.redouane59.twitter.signature.TwitterCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Random;

public class TwitterBot {

    private static final Logger logger = LoggerFactory.getLogger(TwitterBot.class);

    private static final Random random = new Random();

    Path pathToTweets = Paths.get("tweets.txt");

    public String tweetQuotes(Map<String, String> input, Context context) {
        try {
            while (true){
                logger.info("Selecting random quote");
                String quote = getRandomQuote();
                TwitterClient twitterClient = createTwitterClient(input);

                TweetList tweetList = twitterClient.getUserTimeline(input.get("USER_ID"),
                        AdditionalParameters.builder().recursiveCall(false).maxResults(20).build());

                if(!hasQuoteBeenUsedRecently(tweetList, quote)){
                    context.getLogger().log("Sending tweet: " + quote);
                    twitterClient.postTweet(quote);
                    input.put("quote", quote);
                    break;
                } else {
                    logger.info("Not Sending tweet: {}", quote);
                }
            }
        } catch (IOException e) {
            logger.error("Oops! Something went wrong");
            e.printStackTrace();
        }
        return "Tweet: " + input.get("quote");
    }

    public String getRandomQuote() throws IOException {
        int randomLine = random.nextInt(Files.readAllLines(pathToTweets).size());
        return Files.readAllLines(pathToTweets).get(randomLine);
    }

    public boolean hasQuoteBeenUsedRecently(TweetList tweetList, String quote){
        if (isThisTheFirstTweet(tweetList)){
            logger.info("This will be the first tweet!");
            return false;
        }
        return isFoundInLast20Tweets(tweetList, quote);
    }

    public boolean isFoundInLast20Tweets(TweetList tweetList, String quote) {
        for (int i = 0; i< tweetList.getData().size(); i++){
            if (tweetList.getData().get(i).getText().contains(quote)){
                logger.info("Selected quote \"{}\" has been used in the last 20 days", quote);
                return true;
            }
        }
        return false;
    }

    public TwitterClient createTwitterClient(Map<String, String> input){
        return new TwitterClient(TwitterCredentials.builder()
                .accessToken(input.get("ACCESS_TOKEN"))
                .accessTokenSecret(input.get("ACCESS_TOKEN_SECRET"))
                .apiKey(input.get("API_KEY"))
                .apiSecretKey(input.get("API_SECRET_KEY"))
                .build());
    }

    public boolean isThisTheFirstTweet(TweetList tweetList){
        return tweetList.getMeta().getResultCount() == 0;
    }
}