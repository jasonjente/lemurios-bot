package bot.commands.concrete.images;

import bot.commands.Command;
import bot.dataservice.meme.MemeService;
import bot.dataservice.model.Meme;
import bot.utils.DiscordUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;

import static bot.constants.Commands.UPLOAD_BATCH_MEMES_COMMAND;
import static bot.dataservice.meme.MemeUtils.createMemesFromFiles;
import static bot.dataservice.meme.MemeUtils.extractZipFile;

@Service
public class UploadBatchMemes extends Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadBatchMemes.class);
    private DiscordUtils discordUtils;
    private MemeService memeService;

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String sender = event.getUser().getName();
        LOGGER.info("{} has requested to upload a    batch of memes!", sender);
        createHistoryEntry(event);

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle(sender + ", thank you for your meme-tribution!");

        List<OptionMapping> attachments = event.getInteraction().getOptions();
        if (!attachments.isEmpty()) {
            try {
            File zipFile = discordUtils.saveZipFilesReceived(sender, embedBuilder, attachments);
            File tempFolder = Files.createTempDirectory("meme-upload").toFile();

            extractZipFile(zipFile, tempFolder);
            List<Meme> memes = createMemesFromFiles(Objects.requireNonNull(tempFolder.listFiles()));

            memeService.storeMemes(memes);
            } catch (IOException | NullPointerException e) {
                embedBuilder.addField("Error","Please upload an image with the command!",true);
            }
        } else {
            embedBuilder.addField("Error","Please upload an image with the command!",true);
        }
        event.getInteraction().getHook().editOriginalEmbeds(embedBuilder.build()).queue();
    }

    @Override
    public String getCommandDescription() {
        return "Upload a zip file containing .jpeg/.jpg/.png meme files so they can be shared! Beware the memes will be available on all servers worldwide!";
    }

    @Override
    public String getCommandName() {
        return UPLOAD_BATCH_MEMES_COMMAND.getCommandName();
    }

    @Autowired
    public void setDiscordUtils(DiscordUtils discordUtils){
        this.discordUtils = discordUtils;
    }

    @Autowired
    public void setMemeService(MemeService memeService){
        this.memeService = memeService;
    }
}
