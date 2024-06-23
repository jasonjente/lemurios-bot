package bot.commands.concrete.meme;

import bot.commands.Command;
import bot.application.services.meme.MemeService;
import bot.application.services.model.Meme;
import bot.application.utils.DiscordUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;

import static bot.application.constants.Commands.UPLOAD_BATCH_MEMES_COMMAND;
import static bot.application.utils.MemeUtils.createMemesFromFiles;
import static bot.application.utils.MemeUtils.extractZipFile;


public class UploadBatchMemesCommand extends Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadBatchMemesCommand.class);
    private DiscordUtils discordUtils;
    private MemeService memeService;

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String sender = event.getUser().getName();
        LOGGER.info("{} has requested to upload a    batch of memes!", sender);

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
            earnPoints(event, 2 * memes.size());
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
