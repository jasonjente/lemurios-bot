package bot.application.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
public class DiscordUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(DiscordUtils.class);

    public String getAvailableFilename(final String directory, final String filename) {
        var file = new File(new File(directory), filename);
        var trimmedFilename = filename;
        if (file.exists()) {
            var suffix = filename.substring(filename.lastIndexOf("."));
            trimmedFilename = filename.replace(suffix, "_1");
            trimmedFilename = trimmedFilename.concat(suffix);
            getAvailableFilename(directory, trimmedFilename);
        }
        //replace .jpg/.jpeg with .png

        if (trimmedFilename.endsWith(".jpg")) {
            trimmedFilename = trimmedFilename.replace(".jpg", ".png");
        } else if (trimmedFilename.endsWith(".jpeg")) {
            trimmedFilename = trimmedFilename.replace(".jpeg", ".png");
        }
        return trimmedFilename;
    }

    public List<String> saveImagesReceived(final String sender, final EmbedBuilder embedBuilder,
                                           final List<OptionMapping> attachments, final String directory) {
        var filenames = new ArrayList<String>();
        for (OptionMapping attachment : attachments) {
            var attachmentUrl = attachment.getAsAttachment().getUrl();
            var fileName = attachment.getAsAttachment().getFileName();
            try {
                var connection = getHttpURLConnection(attachmentUrl);

                var image = ImageIO.read(connection.getInputStream());
                var finalFilename = getAvailableFilename(directory, fileName);
                var outputFile = new File(new File(directory), finalFilename);
                ImageIO.write(image, "png", outputFile);
                filenames.add(finalFilename);
            } catch (IOException e) {
                LOGGER.error("saveImagesReceived() - ERROR: ", e);
                embedBuilder.addField("Image upload failed!", "Please try again, if the error persists please contact the admins!" + sender, true);
            }

        }
        return filenames;
    }

    public byte[] saveImagesReceived(final String sender, final EmbedBuilder embedBuilder,
                                     final List<OptionMapping> attachments) {
        for (var attachment : attachments) {
            var attachmentUrl = attachment.getAsAttachment().getUrl();
            try {
                //We convert from inputStream directly to byte[], this happens only in memory, the image is not saved in the file system
                var connection = getHttpURLConnection(attachmentUrl);
                return IOUtils.toByteArray(connection.getInputStream());

            } catch (IOException e) {
                embedBuilder.addField("Image download failed!", "Please try again! If the error persists please contact the admins " + sender, true);
            }

        }
        return new byte[0];
    }

    public File saveZipFilesReceived(final String sender, final EmbedBuilder embedBuilder,
                                     final List<OptionMapping> attachments) {
        for (var attachment : attachments) {
            var attachmentUrl = attachment.getAsAttachment().getUrl();
            try {
                var connection = getHttpURLConnection(attachmentUrl);
                var inputStream = connection.getInputStream();

                // Create a temporary file to save the zip data
                var tempFile = File.createTempFile("temp", ".zip");
                tempFile.deleteOnExit();

                // Save the zip data to the temporary file
                try (var outputStream = new FileOutputStream(tempFile)) {
                    IOUtils.copy(inputStream, outputStream);
                }

                return tempFile;
            } catch (IOException e) {
                embedBuilder.addField("Zip file upload failed!",
                        "Please try again. If the error persists, please contact the admins! " + sender, true);
            }
        }
        return null;
    }

    @NotNull
    private HttpURLConnection getHttpURLConnection(final String attachmentUrl) throws IOException {
        var url = new URL(attachmentUrl);
        var connection = (HttpURLConnection) url.openConnection();

        // Set the headers to match the ones sent by a browser to trick the CDN and avoid error 403
        connection.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
        connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        connection.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
        connection.setRequestProperty("Referer", "https://discord.com/channels/");
        connection.setRequestProperty("Cookie", "cookies_here");
        return connection;
    }


}
