package bot.commands.concrete.music;

import bot.commands.Command;
import bot.music.MusicPlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.Objects;

import static bot.constants.Commands.JOIN_COMMAND;

@Service
public class JoinCommand extends Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(JoinCommand.class);
    private MusicPlayerManager musicPlayerManager;


    @Override
    public void execute(SlashCommandInteractionEvent event) {
        LOGGER.info("{} has requested the Join command - ENTER.", event.getUser().getName());
        createHistoryEntry(event);
        EmbedBuilder embedBuilder = new EmbedBuilder();
        try{
            VoiceChannel voiceChannel = Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(event.getInteraction().getMember()).getVoiceState()).getChannel()).asVoiceChannel());
            musicPlayerManager.joinVoiceChannel(event, voiceChannel);
            embedBuilder.addField("Bot joined voice channel.", voiceChannel.getName(), true).setColor(Color.YELLOW);
        }catch (NullPointerException e){
            embedBuilder.addField("Error:", "To call the bot you have to be in a voice channel.", false);
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
    private void setMusicBot(MusicPlayerManager musicPlayerManager){
        this.musicPlayerManager = musicPlayerManager;
    }

}
