package bot.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
public class DiscordUtilsImpl implements DiscordUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(DiscordUtilsImpl.class);

    @Override
    public String getAvailableFilename(String directory, String filename){
        File file = new File(new File(directory), filename);
        String trimmedFilename = filename;
        if(file.exists()){
            String suffix = filename.substring(filename.lastIndexOf("."));
            trimmedFilename = filename.replace(suffix,"_1");
            trimmedFilename = trimmedFilename.concat(suffix);
            getAvailableFilename(directory, trimmedFilename);
        }
        //replace .jpg/.jpeg with .png

        if (trimmedFilename.endsWith(".jpg")) {
            trimmedFilename = trimmedFilename.replace(".jpg", ".png");
        } else if (trimmedFilename.endsWith(".jpeg")){
            trimmedFilename = trimmedFilename.replace(".jpeg", ".png");
        }
        return trimmedFilename;
    }

    @Override
    public List<String> saveImagesReceived(String sender, EmbedBuilder embedBuilder, List<OptionMapping> attachments, String directory) {
        List<String> filenames = new ArrayList<>();
        for(OptionMapping attachment:attachments) {
            String attachmentUrl = attachment.getAsAttachment().getUrl();
            String fileName = attachment.getAsAttachment().getFileName();
            try {
                HttpURLConnection connection = getHttpURLConnection(attachmentUrl);

                BufferedImage image = ImageIO.read(connection.getInputStream());
                String finalFilename = getAvailableFilename(directory, fileName);
                File outputFile = new File(new File(directory), finalFilename);
                ImageIO.write(image, "png", outputFile);
                filenames.add(finalFilename);
            } catch (IOException e) {
                LOGGER.error("saveImagesReceived() - ERROR: ", e);
                embedBuilder.addField("Image upload failed!", "Please try again, if the error persists please contact the admins!" + sender, true);
            }

        }
        return filenames;
    }

    @Override
    public byte[] saveImagesReceived(String sender, EmbedBuilder embedBuilder, List<OptionMapping> attachments) {
        for(OptionMapping attachment:attachments) {
            String attachmentUrl = attachment.getAsAttachment().getUrl();
            try {
                //We convert from inputStream directly to byte[], this happens only in memory, the image is not saved in the file system
                HttpURLConnection connection = getHttpURLConnection(attachmentUrl);
                return IOUtils.toByteArray(connection.getInputStream());

            } catch (IOException e) {
                embedBuilder.addField("Image download failed!", "Please try again! If the error persists please contact the admins " + sender, true);
            }

        }
        return new byte[0];
    }

    @Override
    public File saveZipFilesReceived(String sender, EmbedBuilder embedBuilder, List<OptionMapping> attachments) {
        for (OptionMapping attachment : attachments) {
            String attachmentUrl = attachment.getAsAttachment().getUrl();
            try {
                HttpURLConnection connection = getHttpURLConnection(attachmentUrl);
                InputStream inputStream = connection.getInputStream();

                // Create a temporary file to save the zip data
                File tempFile = File.createTempFile("temp", ".zip");
                tempFile.deleteOnExit();

                // Save the zip data to the temporary file
                try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
                    IOUtils.copy(inputStream, outputStream);
                }

                return tempFile;
            } catch (IOException e) {
                embedBuilder.addField("Zip file upload failed!", "Please try again. If the error persists, please contact the admins! " + sender, true);
            }
        }
        return null;
    }

    @NotNull
    private HttpURLConnection getHttpURLConnection(String attachmentUrl) throws IOException {
        URL url = new URL(attachmentUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Set the headers to match the ones sent by a browser to trick the CDN and avoid error 403
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
        connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        connection.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
        connection.setRequestProperty("Referer", "https://discord.com/channels/");
        connection.setRequestProperty("Cookie", "cookies_here");
        return connection;
    }


}
