package dbhq.bot.command;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.entity.DiscordEntity;
import org.javacord.api.event.message.MessageCreateEvent;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Enhances the CommandManager to inform users about rate limiting precisely once per cooldown period,
 * and advises them on their current punishment, ensuring clear communication without spamming notifications.
 */
public class CommandManager {
    private static final Logger logger = LogManager.getLogger(CommandManager.class);

    // The map of registered commands, indexed by their names.
    private final Map<String, ICommand> commands = new ConcurrentHashMap<>();

    // The map of user command statistics, indexed by user ID and server ID.
    private final ConcurrentHashMap<String, UserCommandStats> userCommandStats = new ConcurrentHashMap<>();

    // The default command prefix and initial cooldown period.
    private static final String defaultPrefix = "!";

    // The initial cooldown period in milliseconds.
    private static final long initialCooldown = TimeUnit.SECONDS.toMillis(5);

    // The executor service for command execution.
    private final ExecutorService commandExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    /**
     * Registers a command with the command manager.
     *
     * @param command The command to register.
     */
    public void registerCommand(ICommand command) {
        Objects.requireNonNull(command, "Command cannot be null.");
        String commandName = command.getName().toLowerCase(Locale.ROOT);
        if (commands.putIfAbsent(commandName, command) == null) {
            logger.info("Registered command: {}", commandName);
        } else {
            logger.warn("Attempted to register a duplicate command: {}", commandName);
        }
    }

    /**
     * Handles the creation of messages and executes commands if they are registered and not rate-limited.
     *
     * @param event The message creation event.
     */
    public void handle(MessageCreateEvent event) {
        String messageContent = event.getMessageContent();
        if (!messageContent.startsWith(defaultPrefix)) return;

        String userId = event.getMessageAuthor().getIdAsString();
        String serverId = event.getServer().map(DiscordEntity::getIdAsString).orElse("DM");
        String userServerKey = userId + ":" + serverId;

        long currentTime = Instant.now().toEpochMilli();

        // Get or create the user's command statistics.
        UserCommandStats stats = userCommandStats.computeIfAbsent(userServerKey, k -> new UserCommandStats());

        // Check if the user is rate-limited.
        synchronized (stats) {
            long timeSinceLastCommand = currentTime - stats.lastCommandTime.get();

            // If the user is rate-limited, update the cooldown period and notify the user if necessary.
            if (timeSinceLastCommand < stats.currentCooldown.get()) {
                if (stats.notified.compareAndSet(false, true)) {
                    // Notify the user only once per cooldown period.
                    event.getChannel().sendMessage(String.format("You're being rate-limited. Please wait %d seconds before trying again.", stats.currentCooldown.get() / 1000));
                }
                return;
            } else {
                // Reset stats and notify flag if the cooldown has expired.
                resetOrUpdateTimeStats(stats, currentTime);
                stats.notified.set(false);
            }
        }

        executeCommandIfPresent(event, messageContent);
    }

    /**
     * Executes the command if it is registered and not rate-limited.
     *
     * @param event          The message creation event that triggered the command.
     * @param messageContent The content of the message.
     */
    private void executeCommandIfPresent(MessageCreateEvent event, String messageContent) {
        String[] split = messageContent.substring(defaultPrefix.length()).trim().split("\\s+", 2);
        String commandName = split[0].toLowerCase(Locale.ROOT);
        List<String> args = split.length > 1 ? Arrays.asList(split[1].split("\\s+")) : Collections.emptyList();

        ICommand command = commands.get(commandName);
        if (command != null) {
            commandExecutor.submit(() -> {
                try {
                    command.execute(event, args);
                    logger.debug("Executed command: {}", command.getName());
                } catch (Exception e) {
                    logger.error("Error executing command '{}': {}", command.getName(), e.getMessage(), e);
                    event.getChannel().sendMessage("An error occurred while executing the command.");
                }
            });
        } else {
            event.getChannel().sendMessage("Command not found: " + commandName);
        }
    }

    /**
     * Resets or updates the time-based statistics for a user's command usage.
     *
     * @param stats      The user's command statistics.
     * @param currentTime The current time in milliseconds.
     */
    private void resetOrUpdateTimeStats(UserCommandStats stats, long currentTime) {
        stats.lastCommandTime.set(currentTime);
        stats.currentCooldown.set(initialCooldown);
        stats.violations.set(0);
        stats.notified.set(false); // Reset notification flag upon resetting stats.
    }

    /**
     * Represents the command usage statistics for a user.
     */
    static class UserCommandStats {
        final AtomicLong lastCommandTime = new AtomicLong(0);
        final AtomicLong currentCooldown = new AtomicLong(initialCooldown);
        final AtomicInteger violations = new AtomicInteger(0);
        final AtomicBoolean notified = new AtomicBoolean(false); // Tracks if the user has been notified about the rate limit.
    }
}
