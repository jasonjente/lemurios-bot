package bot.commands.concrete.music;

import bot.commands.Command;
import bot.commands.concrete.music.youtube.YoutubeResult;
import bot.commands.concrete.music.youtube.YoutubeSearcher;
import bot.application.exceptions.YoutubeSearchException;
import bot.application.utils.music.MusicPlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.regex.Pattern;

import static bot.application.constants.Commands.PLAY_COMMAND;

@Service
public class PlayCommand extends Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayCommand.class);
    private MusicPlayerManager musicPlayerManager;
    private YoutubeSearcher youtubeSearcher;

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        LOGGER.info("{} has requested the Play command. full command: {} - ENTER", event.getUser().getName(), event.getFullCommandName());

        // Make sure we only respond to events that occur in a guild
        if (!event.isFromGuild()) return;

        // if this is not a bot make sure to check if this message is sent by yourself!
        if (event.getUser().isBot()) return;

        EmbedBuilder embedBuilder = new EmbedBuilder();
        if (event.getInteraction().getMember().getVoiceState() != null) {
            String song = event.getInteraction().getOptions().get(0).getAsString();
            if (!(isValidUrl(song) || isValidYoutubeURL(song))){
                //if the user did not provide a url then we have to construct the URL by searching YouTube for the title
                // and (lazily) getting the first result
                try {
                    YoutubeResult youtubeResult = youtubeSearcher.search(song);
                    if (youtubeResult.getPlaylistUrl() != null){
                        song = youtubeResult.getPlaylistUrl();
                    } else {
                        song = youtubeResult.getVideoURL();
                    }
                } catch (YoutubeSearchException e) {
                    LOGGER.error("Error while searching for a video with title {} \n ", event.getInteraction().getOptions().get(0).getAsString(), e);
                }
            }
            TextChannel textChannel = event.getChannel().asTextChannel();
            VoiceChannel voiceChannel = event.getInteraction().getMember().getVoiceState().getChannel().asVoiceChannel();
            LOGGER.info("Voice channel {}", voiceChannel.getName());
            embedBuilder.setTitle(":musical_note: Lemurios BOT - Music Player :musical_note:");
            musicPlayerManager.loadAndPlay(event, textChannel, voiceChannel, song, embedBuilder);
        } else {
            embedBuilder.setTitle("Lemurios Music BOT - Error.").setColor(Color.RED);
            embedBuilder.addField("Error:", "To call the bot you have to be in a voice channel.", false);
        }

        earnPoints(event);
        LOGGER.info("{} has requested the Play command. full command: {} - ENTER", event.getUser().getName(), event.getFullCommandName());

    }

    private boolean isValidUrl(String song) {
        return UrlValidator.getInstance().isValid(song);
    }



    //based on this: https://stackoverflow.com/questions/24030892/android-java-check-if-url-is-valid-youtube-url
    private boolean isValidYoutubeURL(String song) {
        Pattern youtubePattern = Pattern.compile( "^(http(s)?:\\/\\/)?((w){3}.)?youtu(be|.be)?(\\.com)?\\/.+" );
        return youtubePattern.matcher(song).matches();
    }

    @Override
    public String getCommandDescription() {
        return "Use with a youtube URL to summon the bot and add the songs to the queue";
    }

    @Override
    public String getCommandName() {
        return PLAY_COMMAND.getCommandName();
    }

    @Autowired
    public void setMusicBot(MusicPlayerManager musicPlayerManager){
        this.musicPlayerManager = musicPlayerManager;
    }

    @Autowired
    public void setYoutubeSearcher(YoutubeSearcher youtubeSearcher) {
        this.youtubeSearcher = youtubeSearcher;
    }
}
