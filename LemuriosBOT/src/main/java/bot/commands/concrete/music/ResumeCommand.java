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

import static bot.constants.Commands.RESUME_COMMAND;

@Service
public class ResumeCommand extends Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResumeCommand.class);
    private MusicPlayerManager musicPlayerManager;

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        LOGGER.info("{} has requested the Resume command.", event.getUser().getName());

        EmbedBuilder embedBuilder = new EmbedBuilder().setTitle("Lemurios Music BOT - Song Resumed.");
        if(event.getInteraction().getMember() != null || event.getInteraction().getMember().getVoiceState() != null) {
            if(event.getGuild() == null){
                embedBuilder.addField("Cannot resume player if it is not playing a song!", "Please Use the /play command first", true);
                event.getInteraction().getHook().editOriginalEmbeds(embedBuilder.build()).queue();
                return;
            }
            String currentlyPlayingValue = musicPlayerManager.getTimeRemaining(event.getGuild());
            embedBuilder.addField("Time: ", currentlyPlayingValue,false);
            musicPlayerManager.resume(Objects.requireNonNull(event.getGuild()));
        }
        createHistoryEntry(event);
        event.getInteraction().getHook().editOriginalEmbeds(embedBuilder.build()).queue();

        LOGGER.info("{} has requested the Resume command.", event.getUser().getName());
    }

    @Override
    public String getCommandDescription() {
        return "Unpauses the music player and resumes the track that was paused.";
    }

    @Override
    public String getCommandName() {
        return RESUME_COMMAND.getCommandName();
    }

    @Autowired
    private void setMusicBot(MusicPlayerManager musicPlayerManager){
        this.musicPlayerManager = musicPlayerManager;
    }

}
