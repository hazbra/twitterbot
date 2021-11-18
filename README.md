# Twitter Bot

Silly simple Java AWS lambda function used to maintain a Twitter bot

Send regular random tweets, from a predefined list

## Prerequisites & Steps

* Twitter developer account
  * Create a Twitter account for your bot
  * Using this account, apply for a developer account
  * In the dashboard portal, edit the App Permissions to allow read and write access (in order to send tweets)
  * Generate access token and secret and take a note of them (access token, access token secret, api key, and api key secret)
  * You will also need your Twitter User ID
  * Replace `src/main/resources/tweets.txt` with your own list  
  

* AWS account 
    * **Create a lambda function**
      * Runtime: :older_woman: :floppy_disk: Java 8 Amazon Linux 2
      * Handler: `com.bot.demo.TwitterBot::tweetQuotes`
      * Architecture: x86_64  
    * [Upload the jar](#build)
    * Try a test event with [JSON event document below](#json-input) 
    * **Add a trigger**
      * Eventbridge - Create a new rule 
        * Schedule
        * Cron expression (eg `45 19 * * ? *` for every day at 7.45pm :clock1945:)
        * Target - your lambda function
        * Configure input with [JSON constant](#json-input) 

## Build

```bash
mvn clean package shade:shade -DskipTests
```

## Test
Tests are borrowed from [aws examples](https://github.com/awsdocs/aws-lambda-developer-guide/tree/main/sample-apps/java-basic)   
:exclamation: Note: this uses your real access tokens and will send an actual tweet  #sorrynotsorry

```bash
mvn clean install -DACCESS_TOKEN=YOUR_ACCESS_TOKEN -DACCESS_TOKEN_SECRET=YOUR_ACCESS_TOKEN_SECRET -DAPI_KEY=YOUR_API_KEY -DAPI_SECRET_KEY=YOUR_API_SECRET_KEY -DUSER_ID=YOUR_USER_ID
```

## Json Input

```json
{
    "API_KEY": "YOUR_API_KEY", 
    "ACCESS_TOKEN": "YOUR_ACCESS_TOKEN", 
    "API_SECRET_KEY": "YOUR_API_SECRET_KEY",
    "ACCESS_TOKEN_SECRET": "YOUR_ACCESS_TOKEN_SECRET",
    "USER_ID": "YOUR_USER_ID"
}
```

## Example Bot
[![Twitter](https://img.shields.io/twitter/follow/SrMichaelBot.svg?style=social&label=@SrMichaelBot)](https://twitter.com/SrMichaelBot)  
Using a list of quotes from the tv show Derry Girls: [Sister Michael Bot](https://twitter.com/SrMichaelBot) 

## License
[MIT](https://choosealicense.com/licenses/mit/)