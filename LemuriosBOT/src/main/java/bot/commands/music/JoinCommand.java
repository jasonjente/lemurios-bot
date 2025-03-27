package bot.commands.music;

import bot.application.exceptions.InvalidBotStateException;
import bot.commands.Command;
import bot.application.music.player.MusicPlayerManager;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import org.springframework.beans.factory.annotation.Autowired;

import java.awt.*;

import static bot.application.constants.Commands.JOIN_COMMAND;


@Slf4j
public class JoinCommand extends Command {
    
    private MusicPlayerManager musicPlayerManager;


    @Override
    public void execute(SlashCommandInteractionEvent event) {
        log.info("{} has requested the Join command - ENTER.", event.getUser().getName());
        var embedBuilder = new EmbedBuilder();
        try {
            var voiceChannel = event.getInteraction().getMember().getVoiceState().getChannel().asVoiceChannel();
            musicPlayerManager.joinVoiceChannel(event, voiceChannel);
            embedBuilder.addField("Bot joined voice channel.", voiceChannel.getName(), true).setColor(Color.YELLOW);
        } catch (NullPointerException e) {
            log.error("NPE detected: ", e);
            embedBuilder.addField("Error:", "To call the bot you have to be in a voice channel.", false);
            throw new InvalidBotStateException("User is not in voice channel.");
        }
        log.info("{} has requested the Join command - LEAVE.", event.getUser().getName());
        
        event.getInteraction().getHook().editOriginalEmbeds(embedBuilder.build()).queue();
    }

    @Override
    public String getCommandDescription() {
        return "Summons the bot the voice channel the user is in.";
    }

    @Override
    public String getCommandName() {
        return JOIN_COMMAND.getCommandName();
    }

    @Autowired
    private void setMusicPlayerManager(final MusicPlayerManager musicPlayerManager) {
        this.musicPlayerManager = musicPlayerManager;
    }

}
