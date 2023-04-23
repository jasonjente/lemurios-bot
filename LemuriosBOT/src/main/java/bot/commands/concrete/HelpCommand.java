package bot.commands.concrete;


import bot.commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static bot.constants.Constants.*;

@Service
public class HelpCommand extends Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(HelpCommand.class);


    @Override
    public void execute(MessageReceivedEvent event) {
        LOGGER.info("helpCommand - ENTER - {} called for help", event.getAuthor().getName());
        createHistoryEntry(event);
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("LEMURIOS BOT Help Center.")
                .setDescription(HELLO.getValue()+ event.getAuthor().getName() + HELP_COMMENT.getValue())
                .addField(ASSEMLEMURS_COMMAND.getValue(),"pings all lemurs that belong in the LEMURIOI role. \nTo call the other lemurs you will have to be a member of lemurs :).", true)
                .addField(CREDITS_COMMAND.getValue(),"View the credits.", true)
                .addField(AVAILABLE_NAMES.getValue(),"View available names [new!].", true)
                .addField(MEME_COMMAND.getValue(),"View a random meme [new!].", true)
                .addField(UPLOAD_MEME_COMMAND.getValue(),"Upload a meme that can be seen when the random meme is called! [new!]", true)
                .addField(DETECT_IMAGE_EDGES.getValue(),"Upload an image to detect its edges! [new!]", true)
                .addField(HISTORY_COMMAND.getValue(),"View command history. [new!]", true)
                .addField("Coming very soon:","!play <song URL>, !stop, !skip, !pause", true)
                .setColor(java.awt.Color.PINK)
                .setFooter("NOW GTFO HERE!\n With Best Regards Lemurios BOT.");
        event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
        LOGGER.info("helpCommand - LEAVE");
    }
}
