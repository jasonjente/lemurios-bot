package bot.commands.concrete;

import bot.commands.Command;
import bot.exceptions.ImageProcessingException;
import bot.image.processing.builder.ImageProcessorBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.utils.FileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.io.*;
import java.util.List;

import static bot.constants.Constants.IMAGE_DETECTION_IMAGE_IN_DIR;
import static bot.constants.Constants.IMAGE_DETECTION_IMAGE_OUT_DIR;

@Service
public class DetectImageEdgesCommand extends Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(DetectImageEdgesCommand.class);

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String sender = event.getUser().getName();
        LOGGER.info("{} has requested to upload a meme!", sender);
        createHistoryEntry(event);

        EmbedBuilder embedBuilder = new EmbedBuilder();
        List<OptionMapping> attachments = event.getInteraction().getOptions();
        if (!attachments.isEmpty()) {
            List<String> files = saveImagesReceived(sender, embedBuilder, attachments, IMAGE_DETECTION_IMAGE_IN_DIR.getValue());
            for(String file:files){
                ImageProcessorBuilder imageProcessorBuilder;
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
            embedBuilder.setTitle(sender + " has requested Edge Detection in an image!")
                    .addField("Image upload completed!", "Here is your result: " + sender, true)
                    .addField("Filters used prior to edge detection:","In this order: " +
                    "\nGaussian Blur" +
                    "\nMedian Filter" +
                    "\nBilateral Filter" +
                    "\nImage Normalization" +
                    "\nNoise Reduction" +
                    "\nConverted to Black and White" +
                    "\nErosion Filter", true);
            event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();

            cleanUpDirectory(IMAGE_DETECTION_IMAGE_IN_DIR.getValue(), new File(IMAGE_DETECTION_IMAGE_IN_DIR.getValue()));
            cleanUpDirectory(IMAGE_DETECTION_IMAGE_OUT_DIR.getValue(), new File(IMAGE_DETECTION_IMAGE_OUT_DIR.getValue()));
        }
    }

    private void cleanUpDirectory(String directory, File fileDirectories){
        for(String filename:fileDirectories.list()){
            File file = new File(directory, filename);
            if (file.exists()) {
                file.delete();
            }
        }
    }
}
