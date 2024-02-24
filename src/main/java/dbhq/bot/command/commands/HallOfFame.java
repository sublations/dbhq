package dbhq.bot.command.commands;

import dbhq.bot.command.ICommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.entity.emoji.Emoji;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAttachment;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.event.message.reaction.ReactionAddEvent;
import org.javacord.api.listener.message.reaction.ReactionAddListener;

import java.io.InputStream;
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.StreamSupport;

/**
 * A command that saves images & videos when reaching a certain threshold of reactions and can post them at random.
 * It also listens for reactions to save attachments to the hall of fame.
 */
public class HallOfFame implements ICommand, ReactionAddListener {
    private static final Logger logger = LogManager.getLogger(HallOfFame.class);

    // The base directory for hall of fame images (can be overridden by an environment variable)
    private static final Path BASE_HALL_OF_FAME_DIR = Paths.get(System.getenv().getOrDefault("HALL_OF_FAME_DIR", "./app/dbhq-images/hall-of-fame"));
    private static final int REACTION_THRESHOLD = 15; // Example threshold

    // Later make this agnostic so it's not hardcoded
    private static final String CUSTOM_EMOJI_ID = "599772724029685760";

    public HallOfFame() {
        ensureDirectory(BASE_HALL_OF_FAME_DIR);
    }

    /**
     * Executes the command logic.
     *
     * @param event The message creation event that triggered the command.
     * @param args  A list of arguments passed to the command. This list excludes the command prefix and the command name itself.
     */
    @Override
    public void execute(MessageCreateEvent event, List<String> args) {
        event.getServer().ifPresent(server -> {
            Path serverDirectory = BASE_HALL_OF_FAME_DIR.resolve(server.getIdAsString());
            ensureDirectory(serverDirectory);

            try (var paths = Files.newDirectoryStream(serverDirectory)) {
                var fileList = StreamSupport.stream(paths.spliterator(), false).toList();
                if (fileList.isEmpty()) {
                    event.getChannel().sendMessage("The hall of fame is currently empty.");
                    return;
                }

                var randomFile = fileList.get(ThreadLocalRandom.current().nextInt(fileList.size()));
                new MessageBuilder()
                        .addAttachment(randomFile.toFile()) // This method directly accepts a Path object
                        .send(event.getChannel());
            } catch (Exception e) {
                logger.error("Failed to send a hall of fame image", e);
                event.getChannel().sendMessage("Sorry, I couldn't retrieve a hall of fame image.");
            }
        });
    }

    /**
     * Handles the reaction add event to save attachments to the hall of fame.
     *
     * @param event The reaction add event.
     */
    @Override
    public void onReactionAdd(ReactionAddEvent event) {
        event.getEmoji().asCustomEmoji().ifPresent(emoji -> {
            if (emoji.getIdAsString().equals(CUSTOM_EMOJI_ID)) {
                event.requestMessage().thenAcceptAsync(message -> message.getServer().ifPresent(server -> {
                    checkAndSaveAttachmentsFromMessage(message, emoji, server.getIdAsString());
                }));
            }
        });
    }

    /**
     * Checks if the message has a certain threshold of reactions and saves the attachments to the hall of fame.
     *
     * @param message  The message to check.
     * @param emoji    The emoji to check for.
     * @param serverId The ID of the server where the message was sent.
     */
    private void checkAndSaveAttachmentsFromMessage(Message message, Emoji emoji, String serverId) {
        message.getReactions().stream()
                .filter(reaction -> reaction.getEmoji().equalsEmoji(emoji) && reaction.getCount() >= REACTION_THRESHOLD)
                .findFirst()
                .ifPresent(reaction -> message.getAttachments().stream()
                        .filter(attachment -> attachment.getFileName().matches(".*\\.(jpg|jpeg|png|gif|mp4|webp|mov)$"))
                        .forEach(attachment -> saveAttachment(attachment, serverId)));
    }

    /**
     * Saves the attachment to the hall of fame directory.
     *
     * @param attachment The attachment to save.
     * @param serverId   The ID of the server where the attachment was saved.
     */
    private void saveAttachment(MessageAttachment attachment, String serverId) {
        Path serverDirectory = BASE_HALL_OF_FAME_DIR.resolve(serverId);
        ensureDirectory(serverDirectory);

        try (InputStream in = attachment.asInputStream()) {
            String fileName = System.nanoTime() + "_" + attachment.getFileName();
            Path targetPath = serverDirectory.resolve(fileName);
            Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
            logger.info("Saved {} to the hall of fame for server {}", fileName, serverId);
        } catch (Exception e) {
            logger.error("Failed to save attachment for server {}", serverId, e);
        }
    }


    /**
     * Ensures that the specified directory exists.
     *
     * @param path The directory to ensure.
     */
    private void ensureDirectory(Path path) {
        try {
            Files.createDirectories(path);
            logger.info("Directory ensured: {}", path);
        } catch (Exception e) {
            logger.error("Failed to ensure directory: {}", path, e);
        }
    }

    @Override
    public String getName() {
        return "hall-of-fame";
    }

    @Override
    public String getDescription() {
        return "Saves images & videos when reaching a certain threshold of reactions and can post them at random.";
    }
}
