package bot.commands.concrete.music;

import bot.commands.Command;
import bot.music.MusicPlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlayCommand extends Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayCommand.class);
    private MusicPlayerManager musicPlayerManager;

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        LOGGER.info("{} has requested the Play command. full command: {} - ENTER", event.getUser().getName(), event.getFullCommandName());

        // Make sure we only respond to events that occur in a guild
        if (!event.isFromGuild()) return;

        // if this is not a bot make sure to check if this message is sent by yourself!
        if (event.getUser().isBot()) return;

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("LEMURIOS BOT - Now Playing:");
        if (event.getInteraction().getMember().getVoiceState() != null) {
            String song = event.getInteraction().getOptions().get(0).getAsString();
            TextChannel textChannel = event.getChannel().asTextChannel();
            VoiceChannel voiceChannel = event.getInteraction().getMember().getVoiceState().getChannel().asVoiceChannel();
            LOGGER.info("Voice channel {}", voiceChannel.getName());

            musicPlayerManager.loadAndPlay(textChannel, voiceChannel, song);
        } else {
            embedBuilder.addField("Error:", "To call the bot you have to be in a voice channel.", false);
        }

        event.getInteraction().getHook().editOriginalEmbeds(embedBuilder.build()).queue();
        createHistoryEntry(event);
        LOGGER.info("{} has requested the Play command. full command: {} - ENTER", event.getUser().getName(), event.getFullCommandName());

    }

    @Autowired
    private void setMusicBot(MusicPlayerManager musicPlayerManager){
        this.musicPlayerManager = musicPlayerManager;
    }


}
