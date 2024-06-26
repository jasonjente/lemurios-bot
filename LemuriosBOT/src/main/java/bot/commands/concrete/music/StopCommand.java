package bot.commands.concrete.music;

import bot.commands.Command;
import bot.application.utils.music.MusicPlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.*;
import java.util.Objects;

import static bot.application.constants.Commands.STOP_COMMAND;


public class StopCommand extends Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(StopCommand.class);
    private MusicPlayerManager musicPlayerManager;

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        LOGGER.info("{} has requested the Stop command - ENTER.", event.getUser().getName());
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("LEMURIOS BOT - Now Playing:").setColor(Color.ORANGE);
        try {
            musicPlayerManager.stop(Objects.requireNonNull(event.getGuild()));
            embedBuilder.addField("Stopped!", "Queue is also emptied.", true);
        }catch (NullPointerException e){
            embedBuilder.addField("No songs were playing anyway!!", "Use /play to add songs to the queue", true);
        }
        earnPoints(event);
        event.getInteraction().getHook().editOriginalEmbeds(embedBuilder.build()).queue();
        LOGGER.info("{} has requested the Stop command - LEAVE.", event.getUser().getName());
    }

    @Override
    public String getCommandDescription() {
        return "Empties the song queue and stops playing. ";
    }

    @Override
    public String getCommandName() {
        return STOP_COMMAND.getCommandName();
    }

    @Autowired
    private void setMusicBot(MusicPlayerManager musicPlayerManager){
        this.musicPlayerManager = musicPlayerManager;
    }
}
