# DBHQ's Happy Bot

Repository for **DBHQ's Happy Bot**.

## Features

- **Cat Pictures**: Brighten your day with adorable and random cat pictures.
- **Scalable Command Structure**: Designed for easy addition of new commands.

## Getting Started

Follow these steps to get DBHQ's Happy Bot up and running on your server.

### Prerequisites

- Docker installed on your machine.

### Installation

1. **Clone the Repository**

   ```
   git clone https://github.com/sublations/dbhq
   cd dbhq
   ```

2. **Environment Variables**

   Set up the required environment variables for your Discord bot token and any other necessary configurations. This can be done by creating a `.env` file in the root directory with the following content:

   ```plaintext
   DISCORD_TOKEN=your_discord_token
   CAT_API=your_cat_api_key
   ```

3. **Build the Docker Image**

   ```
   docker build -t dbhq-happy-bot .
   ```

4. **Run the Bot**

   Start your bot using Docker:

   ```
   docker run -d --env-file .env dbhq-happy-bot
   ```

## Architecture

This bot is designed with modularity and scalability in mind. The architecture is centered around a command handling system that makes it simple to add new features.

- **Main**: Initializes the bot and sets up the command handling.
- **CommandManager**: Manages the registration and execution of commands.
- **Commands**: Individual implementations of the `ICommand` interface, representing actions the bot can perform.

## Adding Commands

To contribute a new command:

1. Implement the `ICommand` interface in a new class under `src/main/java/dbhq/bot/command/commands`.
2. Register this new command in the `CommandManager` to make it available for use.


## Getting The Cat API key

## Obtaining a Free API Key from TheCatAPI

To use the cat picture feature of DBHQ's Happy Bot, you'll need to obtain a free API key from TheCatAPI. This API provides a wide range of cat images that can be integrated into the bot for generating random cat pictures.

### How to Register for a Free API Key:

1. **Visit TheCatAPI Website**: Go to [TheCatAPI](https://thecatapi.com) in your web browser.

2. **Sign Up**: Click on the **Sign Up** button or link to create a new account. You will be required to provide an email address and create a password.

3. **Get Your API Key**: Once registered, your API key should be sent via email. You will have 10,000 requests a month for their free tier.

4. **Configure Your Bot**: Add the API key to your bot's environment variables as `CAT_API=your_cat_api_key`, replacing `your_cat_api_key` with the API key you obtained.

## License

DBHQ's Happy Bot is released under the MIT License. See the `LICENSE` file for more details.

## Acknowledgments

- Thanks to the Javacord library for the Discord API wrapper.
- fwds for being cool
