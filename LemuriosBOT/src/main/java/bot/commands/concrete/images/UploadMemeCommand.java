package bot.commands.concrete.images;

import bot.commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

import static bot.constants.Constants.DATA_IN_DIR;
@Service
public class UploadMemeCommand extends Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadMemeCommand.class);

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String sender = event.getUser().getName();
        LOGGER.info("{} has requested to upload a meme!", sender);
        createHistoryEntry(event);

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle(sender + ", thank you for your meme-tribution!");

        List<OptionMapping> attachments = event.getInteraction().getOptions();
        if (!attachments.isEmpty()) {
            saveImagesReceived(sender, embedBuilder, attachments, DATA_IN_DIR.getValue());
        } else {
            embedBuilder.addField("Error","Please upload an image with the command!",true);
            embedBuilder.setTitle("Error..");
        }
        event.getInteraction().getHook().editOriginalEmbeds(embedBuilder.build()).queue();

    }

}