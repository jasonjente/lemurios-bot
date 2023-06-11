package bot.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.io.File;
import java.util.List;

/**
 * Utility class that allows download of files (Image files, Zip Files etc.) via the web
 */
public interface DiscordUtils {
    String getAvailableFilename(String directory, String filename);

    List<String> saveImagesReceived(String sender, EmbedBuilder embedBuilder, List<OptionMapping> attachments, String directory);

    byte[] saveImagesReceived(String sender, EmbedBuilder embedBuilder, List<OptionMapping> attachments);
    File saveZipFilesReceived(String sender, EmbedBuilder embedBuilder, List<OptionMapping> attachments);
}
