package bot.commands.concrete;

import bot.LemuriosBOT;
import bot.commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

import static bot.constants.Constants.IMAGE_DETECTION_IMAGE_IN_DIR;

@Service
public class UploadMemeCommand extends Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadMemeCommand.class);

    @Override
    public void execute(MessageReceivedEvent event) {
        String sender = event.getAuthor().getName();
        LOGGER.info("{} has requested to upload a meme!", sender);
        createHistoryEntry(event);

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle(sender + ", thank you for your meme-tribution!");

        List<Message.Attachment> attachments = event.getMessage().getAttachments();
        if (!attachments.isEmpty()) {
            saveImagesReceived(sender, embedBuilder, attachments, IMAGE_DETECTION_IMAGE_IN_DIR.getValue());
        } else {
            embedBuilder.addField("Error","Please upload an image with the command!",true);
            embedBuilder.setTitle("Error..");
        }
        event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();

    }

}
