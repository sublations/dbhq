package dbhq.bot.command.commands;

import dbhq.bot.command.ICommand;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.json.JSONArray;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CatPicture implements ICommand {
    private static final Logger logger = LogManager.getLogger(CatPicture.class);
    private static final String CAT_API_URL = "https://api.thecatapi.com/v1/images/search";
    private static final int TIMEOUT = 5000; // milliseconds

    @Override
    public void execute(MessageCreateEvent event, List<String> args) {
        // Configure timeout settings for the HTTP client
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(TIMEOUT)
                .setConnectionRequestTimeout(TIMEOUT)
                .setSocketTimeout(TIMEOUT)
                .build();

        try (CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(config).build()) {
            HttpGet request = new HttpGet(CAT_API_URL);
            String apiKey = System.getenv("CAT_API");
            if (apiKey != null) {
                request.setHeader("x-api-key", apiKey);
            }

            String json = EntityUtils.toString(httpClient.execute(request).getEntity());
            JSONArray jsonArray = new JSONArray(json);
            String imageUrl = jsonArray.getJSONObject(0).getString("url");

            // Asynchronously send the cat image to avoid blocking the event handling thread
            CompletableFuture.runAsync(() ->
                    event.getChannel().sendMessage(new EmbedBuilder().setImage(imageUrl))
            ).exceptionally(ex -> {
                logger.error("Failed to send cat image: {}", ex.getMessage());
                event.getChannel().sendMessage("Sorry, I couldn't fetch a cat image for you.");
                return null;
            });
        } catch (Exception e) {
            logger.error("Error fetching cat image: {}", e.getMessage(), e);
            event.getChannel().sendMessage("Sorry, I encountered an issue fetching a cat image.");
        }
    }

    @Override
    public String getName() {
        return "cat";
    }

    @Override
    public String getDescription() {
        return "Responds with a random cat picture!";
    }
}
