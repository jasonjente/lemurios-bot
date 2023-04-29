package bot.commands;

import bot.commands.history.HistoryEntry;
import bot.commands.history.HistoryEntryRepository;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static bot.constants.Constants.HELLO;

@Service
public abstract class Command {

    @Autowired
    private HistoryEntryRepository repository;

    public abstract void execute(SlashCommandInteractionEvent event);

    protected void createHistoryEntry(SlashCommandInteractionEvent event){
        HistoryEntry entry = new HistoryEntry();
        entry.setCommandIssued(event.getFullCommandName());
        entry.setFullTag(event.getUser().getAsTag());
        entry.setCreatedOn(Timestamp.from(Instant.now()));
        repository.save(entry);
    }
    protected String getAvailableFilename(String directory, String filename){
        File file = new File(new File(directory), filename);
        String trimmedFilename = filename;
        if(file.exists()){
            String suffix = filename.substring(filename.lastIndexOf("."));
            trimmedFilename = filename.replace(suffix,"_1");
            trimmedFilename = trimmedFilename.concat(suffix);
            getAvailableFilename(directory, trimmedFilename);
        }
        //replace .jpg/.jpeg with .png
        trimmedFilename = trimmedFilename.endsWith(".jpg") ? trimmedFilename.replace(".jpg", ".png") : trimmedFilename.endsWith(".jpeg") ? trimmedFilename.replace(".jpeg", ".png") : trimmedFilename;
        return trimmedFilename;
    }

    protected List<String> saveImagesReceived(String sender, EmbedBuilder embedBuilder, List<OptionMapping> attachments, String directory) {
        List<String> filenames = new ArrayList<>();
        for(OptionMapping attachment:attachments) {
            String attachmentUrl = attachment.getAsAttachment().getUrl();
            String fileName = attachment.getAsAttachment().getFileName();
            try {
                URL url = new URL(attachmentUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                // Set the headers to match the ones sent by a browser to trick the CDN and avoid error 403
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
                connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
                connection.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
                connection.setRequestProperty("Referer", "https://discord.com/channels/");
                connection.setRequestProperty("Cookie", "cookies_here");

                BufferedImage image = ImageIO.read(connection.getInputStream());
                String finalFilename = getAvailableFilename(directory, fileName);
                File outputFile = new File(new File(directory), finalFilename);
                ImageIO.write(image, "png", outputFile);
                filenames.add(finalFilename);
            } catch (IOException e) {
                embedBuilder.addField("Image upload failed!", "Please try again, if the error persists please contact the admins!" + sender, true);
            }

        }
        return filenames;
    }

    public void functionalityNotReadyYet(SlashCommandInteractionEvent event) {
        createHistoryEntry(event);
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("LEMURIOS BOT Help Center.")
                .setDescription(HELLO.getValue()+ event.getUser().getName() + ", unfortunately this functionality has not been setup yet :/.")
                .setColor(Color.MAGENTA)
                .setFooter("NOW GTFO HERE!\n With Best Regards Lemurios BOT-DEV Team.");
        event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
    }
}
