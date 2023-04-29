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
public class ResumeCommand extends Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResumeCommand.class);
    private MusicPlayerManager musicPlayerManager;

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        LOGGER.info("{} has requested the Resume command.", event.getUser().getName());

        EmbedBuilder embedBuilder = new EmbedBuilder().setTitle("Lemurios Music BOT - Song Paused.");
        if(event.getInteraction().getMember() != null || event.getInteraction().getMember().getVoiceState() != null) {
            musicPlayerManager.resume(Objects.requireNonNull(event.getGuild()));
        }
        createHistoryEntry(event);
        event.getInteraction().getHook().editOriginalEmbeds(embedBuilder.build()).queue();

        LOGGER.info("{} has requested the Resume command.", event.getUser().getName());
    }

    @Autowired
    private void setMusicBot(MusicPlayerManager musicPlayerManager){
        this.musicPlayerManager = musicPlayerManager;
    }

}
