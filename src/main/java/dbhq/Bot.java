package dbhq;

import dbhq.bot.command.CommandManager;
import dbhq.bot.command.commands.CatPicture;
import dbhq.bot.command.commands.HallOfFame;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.intent.Intent;


public class Bot {
    private static final Logger logger = LogManager.getLogger(Bot.class);
    private final DiscordApi api;
    private final CommandManager commandManager;

    /**
     * Creates a new bot instance with the specified token.
     *
     * @param token The bot token.
     */
    public Bot(String token) {
        this.api = new DiscordApiBuilder()
                .setToken(token)
                .addIntents(
                        Intent.GUILDS,
                        Intent.GUILD_MESSAGES,
                        Intent.MESSAGE_CONTENT,
                        Intent.GUILD_MESSAGE_REACTIONS,
                        Intent.DIRECT_MESSAGES,
                        Intent.DIRECT_MESSAGE_REACTIONS)
                .login()
                .join();
        this.commandManager = new CommandManager();
    }

    /**
     * Sets up the bot by registering commands and listeners.
     */
    private void setupBot() {
        registerCommands();
        api.addMessageCreateListener(commandManager::handle);
        logger.info("DBHQ Bot is now running!");
        logger.info("Invite URL: {}", api.createBotInvite());
    }

    /**
     * Registers all commands with the command manager.
     */
    private void registerCommands() {
        commandManager.registerCommand(new CatPicture());
        HallOfFame hallOfFame = new HallOfFame();
        commandManager.registerCommand(hallOfFame);
        api.addReactionAddListener(hallOfFame);
    }

    /**
     * Shuts down the bot and releases all resources.
     */
    public static void main(String[] args) {
        String token = System.getenv("DISCORD_TOKEN");
        if (token == null || token.isBlank()) {
            logger.error("Discord bot token is not configured. Please check your configuration.");
            System.exit(1);
        }

        Bot botApplication = new Bot(token);
        botApplication.setupBot();
    }
}
