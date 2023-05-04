package bot.commands.concrete.chat;


import bot.commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static bot.constants.Commands.*;
import static bot.constants.Constants.*;

@Service
public class HelpCommand extends Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(HelpCommand.class);


    @Override
    public void execute(SlashCommandInteractionEvent event) {
        LOGGER.info("helpCommand - ENTER - {} called for help", event.getUser().getName());
        createHistoryEntry(event);
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("LEMURIOS BOT Help Center.")
                .setDescription(HELLO.getValue()+ event.getUser().getName() + HELP_COMMENT.getValue())
                .addField(ASSEMLEMURS_COMMAND.getValue(),"pings all lemurs that belong in the LEMURIOI role. \nTo call the other lemurs you will have to be a member of lemurs :).", true)
                .addField(CREDITS_COMMAND.getValue(),"View the credits.", true)
                .addField(TAKEN_NAMES.getValue(),"View available names].", true)
                .addField(MEME_COMMAND.getValue(),"View a random meme.", true)
                .addField(UPLOAD_MEME_COMMAND.getValue(),"Upload a meme that can be seen when the random meme is called!", true)
                .addField(DETECT_IMAGE_EDGES_COMMAND.getValue(),"Upload an image to detect its edges!", true)
                .addField(HISTORY_COMMAND.getValue(),"View command history.", true)
                .addField(PLAY_COMMAND.getValue(),"Use with a youtube URL to summon the bot and add the songs to the queue", true)
                .addField(PAUSE_COMMAND.getValue(),"Pause the bot if it is playing music. [new!]", true)
                .addField(SKIP_COMMAND.getValue(),"Skips current song playing and goes to the next song in the queue. [new!]", true)
                .addField(STOP_COMMAND.getValue(),"Empties the song queue and stops playing. [new!]", true)
                .addField(JOIN_COMMAND.getValue(),"Summons the bot the voice channel the user is in. [new!]", true)
                .addField(DISCONNECT_COMMAND.getValue(),"Disconnects the bot the voice channel the user is currently in. [new!]", true)
                .addField(NOW_PLAYING.getValue(),"Prints the songs in the queue. [new!]", true)
                .setColor(java.awt.Color.PINK)
                .setFooter("NOW GTFO HERE!\n With Best Regards Lemurios BOT.");
        event.getInteraction().getHook().editOriginalEmbeds(embedBuilder.build()).queue();
        LOGGER.info("helpCommand - LEAVE");
    }
}
