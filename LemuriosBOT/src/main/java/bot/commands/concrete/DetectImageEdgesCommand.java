package bot.commands.concrete;

import bot.commands.Command;
import bot.exceptions.ImageProcessingException;
import bot.image.processing.builder.ImageProcessorBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.FileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static bot.constants.Constants.IMAGE_DETECTION_IMAGE_IN_DIR;
import static bot.constants.Constants.IMAGE_DETECTION_IMAGE_OUT_DIR;

@Service
public class DetectImageEdgesCommand extends Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(DetectImageEdgesCommand.class);

    @Override
    public void execute(MessageReceivedEvent event) {
        String sender = event.getAuthor().getName();
        LOGGER.info("{} has requested to upload a meme!", sender);
        createHistoryEntry(event);

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle(sender + " has requested Edge Detection in an image!");
        List<Message.Attachment> attachments = event.getMessage().getAttachments();
        if (!attachments.isEmpty()) {
            List<String> files = saveImagesReceived(sender, embedBuilder, attachments, IMAGE_DETECTION_IMAGE_IN_DIR.getValue());
            for(String file:files){
                ImageProcessorBuilder imageProcessorBuilder = null;
                try {
                    imageProcessorBuilder = new ImageProcessorBuilder(file).upscale(2)
                            .applyGaussianBlur()
                            .applyMedianFilter()
                            .applyBilateralFilter()
                            .normalizeImage()
                            .applyNoiseReduction()
                            .applyBinaryBlackAndWhite()
                            .applyErodeFilter()
                            .detectEdges()
                            .downscale(2)
                            .exportImage(getAvailableFilename(IMAGE_DETECTION_IMAGE_OUT_DIR.getValue(), file));

                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    ImageIO.write(imageProcessorBuilder.getImage(), "png", os);
                    InputStream ret = new ByteArrayInputStream(os.toByteArray());
                    event.getChannel().sendFiles(FileUpload.fromData(ret, file)).setEmbeds(embedBuilder.build()).queue();

                } catch (ImageProcessingException | IOException e) {
                    embedBuilder.addField("Error!", "There was en error! " + e.getCause(), true);
                    event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
                    return;
                }

            }
            embedBuilder.addField("Image upload completed!", "Here is your result: " + sender, true);
            event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
        }
    }
}
