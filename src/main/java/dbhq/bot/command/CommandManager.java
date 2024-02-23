package dbhq.bot.command;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.event.message.MessageCreateEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CommandManager {
    private static final Logger logger = LogManager.getLogger(CommandManager.class);
    private final Map<String, ICommand> commands = new ConcurrentHashMap<>(); // Thread-safe
    private final String defaultPrefix;

    public CommandManager(String defaultPrefix) {
        this.defaultPrefix = defaultPrefix;
    }

    public void registerCommand(ICommand command) {
        String commandName = command.getName().toLowerCase();
        if (!commands.containsKey(commandName)) {
            commands.put(commandName, command);
            logger.info("Registered command: {}", commandName);
        } else {
            logger.warn("Attempted to register a command that already exists: {}", commandName);
        }
    }

    public void handle(MessageCreateEvent event) {
        String message = event.getMessageContent();
        if (!message.startsWith(defaultPrefix)) return;

        String[] split = message.substring(defaultPrefix.length()).split("\\s+");
        String commandName = split[0].toLowerCase();
        List<String> args = Collections.unmodifiableList(Arrays.asList(split).subList(1, split.length)); // Immutable list for safety

        ICommand command = commands.get(commandName);
        if (command != null) {
            try {
                command.execute(event, args);
                logger.debug("Executed command: {}", commandName);
            } catch (Exception e) {
                logger.error("Error executing command '{}': {}", commandName, e.getMessage(), e);
                event.getChannel().sendMessage("An error occurred while executing the command. Please try again later.");
            }
        } else {
            logger.warn("Attempted to execute an unknown command: {}", commandName);
            // Optionally inform the user that the command does not exist
            event.getChannel().sendMessage("Command not found: " + commandName);
        }
    }
}
