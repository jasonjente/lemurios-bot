package bot.commands.concrete.music;

import bot.commands.Command;
import bot.application.utils.music.MusicPlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

import static bot.application.constants.Commands.PAUSE_COMMAND;


public class PauseCommand extends Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(PauseCommand.class);
    private MusicPlayerManager musicPlayerManager;

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        LOGGER.info("{} has requested the Pause command.", event.getUser().getName());
        EmbedBuilder embedBuilder = new EmbedBuilder().setTitle("Lemurios Music BOT - Song Paused.");
        try {
            if (event.getInteraction().getMember() != null || event.getInteraction().getMember().getVoiceState() != null) {
                musicPlayerManager.pause(Objects.requireNonNull(event.getGuild()));
            }
            String currentlyPlayingValue = musicPlayerManager.getTimeRemaining(event.getGuild());
            embedBuilder.addField("Time: ", currentlyPlayingValue,false);
        }catch (NullPointerException e){
            embedBuilder.addField("Could not pause!", "To pause, verify that you are connected to a voice channel or that the bot has access to the voice channel.", true);
        }
        earnPoints(event);
        event.getInteraction().getHook().editOriginalEmbeds(embedBuilder.build()).queue();
    }

    @Override
    public String getCommandDescription() {
        return "Pause the bot if it is playing music.";
    }

    @Override
    public String getCommandName() {
        return PAUSE_COMMAND.getCommandName();
    }

    @Autowired
    public void setMusicPlayer(MusicPlayerManager musicPlayerManager) {
        this.musicPlayerManager = musicPlayerManager;
    }
}
