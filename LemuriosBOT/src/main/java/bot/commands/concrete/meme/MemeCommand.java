package bot.commands.concrete.meme;

import bot.commands.Command;
import bot.application.services.meme.MemeService;
import bot.application.services.model.Meme;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.FileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;

import static bot.application.constants.Commands.MEME_COMMAND;
import static bot.application.constants.MessageConstants.SORRY_MSG;


/**
 * Gets a random meme in File format, then it converted to a buffered image, passed into a buffered stream and
 * then converted into an Input Stream.
 * The event parameter contains relevant information like the author and the chat endpoints.
 */

public class MemeCommand extends Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(MemeCommand.class);
    private MemeService memeService;
    private static final String ERROR_MESSAGE_SORRY = "Looks like there was an error while processing your request :( ... Sorry for that!";


    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String sender = event.getUser().getName();
        LOGGER.info("{} has requested the meme command.", sender);
        EmbedBuilder embedBuilder = new EmbedBuilder().setImage("attachment://meme.png");
        Meme meme = memeService.getRandomMeme(event);
        if (meme != null) {
            LOGGER.info("{} will have the {} meme.", sender, meme.getFilename());
            ByteArrayInputStream inputStream = new ByteArrayInputStream(meme.getImageData());
            event.getChannel().sendFiles(FileUpload.fromData(inputStream, "meme.png")).setEmbeds(embedBuilder.build()).queue();
        } else {
            embedBuilder.addField(SORRY_MSG.getValue(), ERROR_MESSAGE_SORRY, true);
        }
        event.getInteraction().getHook().editOriginalEmbeds(embedBuilder.build()).queue();
        earnPoints(event);
    }

    @Override
    public String getCommandDescription() {
        return "View a random meme.";
    }

    @Override
    public String getCommandName() {
        return MEME_COMMAND.getCommandName();
    }

    @Autowired
    public void setMemeService(MemeService memeService) {
        this.memeService = memeService;
    }
}
