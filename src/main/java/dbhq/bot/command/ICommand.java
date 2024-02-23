package dbhq.bot.command;

import org.javacord.api.event.message.MessageCreateEvent;
import java.util.List;

/**
 * Represents a command that can be executed by the bot.
 */
public interface ICommand {

    /**
     * Executes the command logic.
     *
     * @param event The message creation event that triggered the command.
     * @param args  A list of arguments passed to the command. This list excludes the command prefix and the command name itself.
     */
    void execute(MessageCreateEvent event, List<String> args);

    /**
     * Returns the name of the command. This is typically a single word that triggers the command.
     *
     * @return The command name.
     */
    String getName();

    /**
     * Returns a description of the command. This should provide a brief overview of what the command does and any parameters it accepts.
     *
     * @return The command description.
     */
    String getDescription();
}
