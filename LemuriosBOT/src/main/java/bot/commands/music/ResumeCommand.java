package bot.commands.music;

import bot.commands.Command;
import bot.application.music.player.MusicPlayerManager;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

import static bot.application.constants.Commands.RESUME_COMMAND;


@Slf4j
public class ResumeCommand extends Command {
    
    private MusicPlayerManager musicPlayerManager;

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        log.info("{} has requested the Resume command.", event.getUser().getName());

        EmbedBuilder embedBuilder = new EmbedBuilder().setTitle("Lemurios Music BOT - Song Resumed.");
        if (event.getInteraction().getMember() != null || event.getInteraction().getMember().getVoiceState() != null) {
            if (event.getGuild() == null){
                embedBuilder.addField("Cannot resume player if it is not playing a song!", "Please Use the /play command first", true);
                event.getInteraction().getHook().editOriginalEmbeds(embedBuilder.build()).queue();
                return;
            }
            String currentlyPlayingValue = musicPlayerManager.getTimeRemaining(event.getGuild());
            embedBuilder.addField("Time: ", currentlyPlayingValue,false);
            musicPlayerManager.resume(Objects.requireNonNull(event.getGuild()));
        }
        
        event.getInteraction().getHook().editOriginalEmbeds(embedBuilder.build()).queue();

        log.info("{} has requested the Resume command.", event.getUser().getName());
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
