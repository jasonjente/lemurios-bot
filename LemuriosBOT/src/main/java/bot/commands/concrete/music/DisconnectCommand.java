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

import java.util.Objects;

@Service
public class DisconnectCommand extends Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(DisconnectCommand.class);
    private MusicPlayerManager musicPlayerManager;

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        LOGGER.info("{} has requested the Disconnect command - ENTER.", event.getUser().getName());
        EmbedBuilder embedBuilder = new EmbedBuilder();
        try{
            VoiceChannel voiceChannel = Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(event.getInteraction().getMember()).getVoiceState()).getChannel()).asVoiceChannel());
            musicPlayerManager.disconnectFromVoiceChannel(event, voiceChannel);
            embedBuilder.addField("Disconnecting..", "Bot stopped playing and disconnected from: " + voiceChannel.getName() , false);
        }catch (NullPointerException e){
            embedBuilder.addField("Error:", "The bot is not in a voice channel!", false);
        }
        LOGGER.info("{} has requested the Disconnect command - LEAVE.", event.getUser().getName());
        event.getInteraction().getHook().editOriginalEmbeds(embedBuilder.build()).queue();
    }

    @Autowired
    private void setMusicBot(MusicPlayerManager musicPlayerManager){
        this.musicPlayerManager = musicPlayerManager;
    }
}
