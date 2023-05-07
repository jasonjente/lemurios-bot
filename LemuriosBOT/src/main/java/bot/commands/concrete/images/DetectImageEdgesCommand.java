package bot.commands.concrete.images;

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

import java.io.*;
import java.util.List;

import static bot.constants.Commands.DETECT_IMAGE_EDGES_COMMAND;
import static bot.constants.Constants.IMAGE_DETECTION_IMAGE_IN_DIR;
import static bot.constants.Constants.IMAGE_DETECTION_IMAGE_OUT_DIR;

@Service
public class DetectImageEdgesCommand extends Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(DetectImageEdgesCommand.class);

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String sender = event.getUser().getName();
        LOGGER.info("{} has requested to detect some edges!", sender);
        createHistoryEntry(event);
        InputStream inputStream;
        EmbedBuilder embedBuilder = new EmbedBuilder().setImage("attachment://detectedImage.png") ;// we specify this in sendFile as "detectedImage.png"
        List<OptionMapping> attachments = event.getInteraction().getOptions();
        if (!attachments.isEmpty()) {
            List<String> files = saveImagesReceived(sender, embedBuilder, attachments, IMAGE_DETECTION_IMAGE_IN_DIR.getValue());
            for(String file:files){
                ImageProcessorBuilder imageProcessorBuilder;
                try {
                    String filename = getAvailableFilename(IMAGE_DETECTION_IMAGE_OUT_DIR.getValue(), file);
                    imageProcessorBuilder = new ImageProcessorBuilder(file).upscale(2)
                            .applyGaussianBlur()
                            .applyMedianFilter()
                            .applyBilateralFilter()
                            .normalizeImage()
                            .applyNoiseReduction()
                            .applyBinaryBlackAndWhite()
                            .applyErodeFilter()
                            .detectEdges()
                            .downscale(2);

                            imageProcessorBuilder.exportImage(filename.substring(0, filename.lastIndexOf(".")));

                    File convertedImage = new File(new File(IMAGE_DETECTION_IMAGE_OUT_DIR.getValue()), filename);
                    LOGGER.info("Filename old {}, Filename new: {} ,exists {},  full path to new file: {} ", file, filename, convertedImage.exists(), convertedImage.getAbsolutePath());

                    inputStream = new FileInputStream(convertedImage);
                    event.getChannel().sendFiles(FileUpload.fromData(inputStream, file)).queue();
                    event.getInteraction().getHook().editOriginalEmbeds(embedBuilder.build()).queue();
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
        }
    }

    @Override
    public String getCommandDescription() {
        return "Upload an image to detect its edges!";
    }

    @Override
    public String getCommandName() {
        return DETECT_IMAGE_EDGES_COMMAND.getCommandName();
    }
}
