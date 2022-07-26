package com.bot.demo.functions;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.redouane59.twitter.TwitterClient;
import io.github.redouane59.twitter.dto.endpoints.AdditionalParameters;
import io.github.redouane59.twitter.dto.tweet.TweetList;
import io.github.redouane59.twitter.signature.TwitterCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

public class TwitterBot implements Function<Map<String, String>, String> {

    private static final Logger logger = LoggerFactory.getLogger(TwitterBot.class);

    private static final Random random = new Random();

    String quotesFile = "src/main/resources/tweets.txt";

    @Override
    public String apply(Map<String, String> input) {
        try {
            while (true){
                logger.info("Selecting random quote");
                String quote = getRandomQuote();

                JsonObject twitterKeys = getTwitterKeys(input.get("REGION"), input.get("SECRET_NAME"));

                TwitterClient twitterClient = createTwitterClient(twitterKeys);

                TweetList tweetList = twitterClient.getUserTimeline(input.get("USER_ID"),
                        AdditionalParameters.builder().recursiveCall(false).maxResults(20).build());

                if(!hasQuoteBeenUsedRecently(tweetList, quote)){
                    logger.info("Sending tweet: {} ", quote);
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
        int randomLine = random.nextInt(Files.readAllLines(Paths.get(quotesFile)).size());
        return Files.readAllLines(Paths.get(quotesFile)).get(randomLine);
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

    public TwitterClient createTwitterClient(JsonObject twitterKeys){
        return new TwitterClient(TwitterCredentials.builder()
                .accessToken(twitterKeys.get("ACCESS_TOKEN").getAsString())
                .accessTokenSecret(twitterKeys.get("ACCESS_TOKEN_SECRET").getAsString())
                .apiKey(twitterKeys.get("API_KEY").getAsString())
                .apiSecretKey(twitterKeys.get("API_SECRET_KEY").getAsString())
                .build());
    }

    public boolean isThisTheFirstTweet(TweetList tweetList){
        return tweetList.getMeta().getResultCount() == 0;
    }

    public JsonObject getTwitterKeys(String region, String secretName) throws IOException {
        String secret = getAwsSecrets(region, secretName);
        return new JsonParser().parse(secret).getAsJsonObject();
    }

    private String getAwsSecrets(String region, String secretName) {
        AWSSecretsManager client  = AWSSecretsManagerClientBuilder.standard().withRegion(region).build();
        GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest().withSecretId(secretName);
        GetSecretValueResult getSecretValueResult = client.getSecretValue(getSecretValueRequest);

        return getSecretValueResult.getSecretString();
    }
}