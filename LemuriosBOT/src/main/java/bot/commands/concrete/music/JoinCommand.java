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

@Service
public class JoinCommand extends Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(PauseCommand.class);
    private MusicPlayerManager musicPlayerManager;


    @Override
    public void execute(SlashCommandInteractionEvent event) {
        LOGGER.info("{} has requested the Join command - ENTER.", event.getUser().getName());
        EmbedBuilder embedBuilder = new EmbedBuilder();
        if (event.getInteraction().getMember().getVoiceState() != null) {
            VoiceChannel voiceChannel = event.getInteraction().getMember().getVoiceState().getChannel().asVoiceChannel();
            musicPlayerManager.connectToVoiceChannel(event, voiceChannel);
        } else {
            embedBuilder.addField("Error:", "To call the bot you have to be in a voice channel.", false);
        }
        LOGGER.info("{} has requested the Join command - LEAVE.", event.getUser().getName());
        event.getInteraction().getHook().editOriginalEmbeds(embedBuilder.build()).queue();
    }

    @Autowired
    private void setMusicBot(MusicPlayerManager musicPlayerManager){
        this.musicPlayerManager = musicPlayerManager;
    }

}
