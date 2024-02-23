package dbhq;

import dbhq.bot.command.CommandManager;
import dbhq.bot.command.commands.CatPicture;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.intent.Intent;

/**
 * Main class to initialize and run the Discord bot.
 * <p>
 * This class sets up the Discord API connection using a token fetched from the configuration,
 * registers all necessary commands with the command manager, and starts listening for events.
 */
public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        // Load the Discord bot token from the configuration.
        String token = System.getenv("DISCORD_TOKEN");
        if (token == null || token.isBlank()) {
            logger.error("Discord bot token is not configured. Please check your configuration.");
            return; // Halt execution if the token is missing to prevent runtime errors.
        }

        try {
            // Initialize the Discord API with the fetched token and required intents.
            DiscordApi api = new DiscordApiBuilder()
                    .setToken(token)
                    .addIntents(Intent.MESSAGE_CONTENT) // Necessary for reading message content.
                    .login()
                    .join();

            // Setup the command manager with a default command prefix.
            String defaultPrefix = "!";
            CommandManager commandManager = new CommandManager(defaultPrefix);

            // Register commands with the command manager.
            registerCommands(commandManager);

            // Attach the command manager as a listener to handle incoming messages.
            api.addMessageCreateListener(commandManager::handle);

            logger.info("DBHQ Bot is now running!");
            // Log the invite URL for the bot, making it easier to add the bot to servers.
            logger.info("You can invite the bot by using the following URL: {}", api.createBotInvite());
        } catch (Exception e) {
            // Log any exceptions that occur during bot startup or configuration loading.
            logger.error("An error occurred while starting the bot: {}", e.getMessage(), e);
        }
    }

    /**
     * Registers all commands used by the bot.
     * <p>
     * This method centralizes command registration, making it easier to add new commands
     * and maintain existing ones.
     *
     * @param commandManager The manager responsible for handling command registration and execution.
     */
    private static void registerCommands(CommandManager commandManager) {
        commandManager.registerCommand(new CatPicture());
        // Additional commands can be registered here as the bot grows.
    }
}
