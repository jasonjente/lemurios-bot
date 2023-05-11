package bot.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.List;

public interface DiscordUtils {
    String getAvailableFilename(String directory, String filename);

    List<String> saveImagesReceived(String sender, EmbedBuilder embedBuilder, List<OptionMapping> attachments, String directory);
}
