package bot.commands.concrete.music;

import bot.commands.Command;
import bot.music.MusicPlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class PauseCommand extends Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(JoinCommand.class);
    private MusicPlayerManager musicPlayerManager;

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        LOGGER.info("{} has requested the Pause command.", event.getUser().getName());
        EmbedBuilder embedBuilder = new EmbedBuilder().setTitle("Lemurios Music BOT - Song Paused.");
        if(event.getInteraction().getMember() != null || event.getInteraction().getMember().getVoiceState() != null) {
            musicPlayerManager.pause(Objects.requireNonNull(event.getGuild()));
        }
        createHistoryEntry(event);
        event.getInteraction().getHook().editOriginalEmbeds(embedBuilder.build()).queue();
    }

    @Autowired
    public void setMusicPlayer(MusicPlayerManager musicPlayerManager) {
        this.musicPlayerManager = musicPlayerManager;
    }
}
