package bot.commands.concrete.music;

import bot.application.exceptions.InvalidVoiceChannelStateException;
import bot.commands.Command;
import bot.application.utils.music.MusicPlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.*;

import static bot.application.constants.Commands.JOIN_COMMAND;


public class JoinCommand extends Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(JoinCommand.class);
    private MusicPlayerManager musicPlayerManager;


    @Override
    public void execute(SlashCommandInteractionEvent event) {
        LOGGER.info("{} has requested the Join command - ENTER.", event.getUser().getName());
        var embedBuilder = new EmbedBuilder();
        try {
            var voiceChannel = event.getInteraction().getMember().getVoiceState().getChannel().asVoiceChannel();
            musicPlayerManager.joinVoiceChannel(event, voiceChannel);
            embedBuilder.addField("Bot joined voice channel.", voiceChannel.getName(), true).setColor(Color.YELLOW);
        } catch (NullPointerException e) {
            LOGGER.error("NPE detected: ", e);
            embedBuilder.addField("Error:", "To call the bot you have to be in a voice channel.", false);
            throw new InvalidVoiceChannelStateException("User is not in voice channel.");
        }
        LOGGER.info("{} has requested the Join command - LEAVE.", event.getUser().getName());
        earnPoints(event);
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
