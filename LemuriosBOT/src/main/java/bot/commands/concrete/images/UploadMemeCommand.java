package bot.commands.concrete.images;

import bot.commands.Command;
import bot.services.meme.MemeService;
import bot.services.model.Meme;
import bot.utils.DiscordUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static bot.constants.Commands.UPLOAD_MEME_COMMAND;
@Service
public class UploadMemeCommand extends Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadMemeCommand.class);
    private DiscordUtils discordUtils;
    private MemeService memeService;

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String sender = event.getUser().getName();
        LOGGER.info("{} has requested to upload a meme!", sender);
        createHistoryEntry(event);

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle(sender + ", thank you for your meme-tribution!");

        List<OptionMapping> attachments = event.getInteraction().getOptions();
        if (!attachments.isEmpty()) {
            byte[] imageData = discordUtils.saveImagesReceived(sender, embedBuilder, attachments);
            Meme meme = new Meme();
            meme.setFilename(event.getUser().getAsTag() + "_" + LocalDateTime.now() + "_meme");
            meme.setCreatedOn(Timestamp.valueOf(LocalDateTime.now()));
            meme.setImageData(imageData);
            memeService.storeMeme(meme);
            earnPoints(event);
        } else {
            embedBuilder.addField("Error","Please upload an image with the command!",true);
            embedBuilder.setTitle("Error..");
        }
        event.getInteraction().getHook().editOriginalEmbeds(embedBuilder.build()).queue();

    }

    @Override
    public String getCommandDescription() {
        return "Upload a meme that can be seen when the random meme is called!";
    }

    @Override
    public String getCommandName() {
        return UPLOAD_MEME_COMMAND.getCommandName();
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
