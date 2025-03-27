package bot.commands.music;

import bot.commands.Command;
import bot.commands.music.youtube.YoutubeResult;
import bot.commands.music.youtube.YoutubeSearcher;
import bot.application.exceptions.YoutubeSearchException;
import bot.application.music.player.MusicPlayerManager;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.apache.commons.validator.routines.UrlValidator;

import org.springframework.beans.factory.annotation.Autowired;

import java.awt.*;
import java.util.Objects;
import java.util.regex.Pattern;

import static bot.application.constants.Commands.PLAY_COMMAND;


@Slf4j
public class PlayCommand extends Command {
    
    private MusicPlayerManager musicPlayerManager;
    private YoutubeSearcher youtubeSearcher;

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        log.info("{} has requested the Play command. full command: {} - ENTER", 
                event.getUser().getName(), event.getFullCommandName());

        // Make sure we only respond to events that occur in a guild
        if (!event.isFromGuild()) return;

        // if this is not a bot make sure to check if this message is sent by yourself!
        if (event.getUser().isBot()) return;

        EmbedBuilder embedBuilder = new EmbedBuilder();
        if (Objects.requireNonNull(event.getInteraction().getMember()).getVoiceState() != null) {
            String song = event.getInteraction().getOptions().getFirst().getAsString();
            if (isValidURL(song)){
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
                    log.error("Error while searching for a video with title {} \n ", 
                            event.getInteraction().getOptions().get(0).getAsString(), e);
                }
            }
            try {
                musicPlayerManager.loadAndPlay(event, song, embedBuilder);
            } catch (Exception exception) {
                log.error("error", exception);
            }
        } else {
            embedBuilder.setTitle("Lemurios Music BOT - Error.").setColor(Color.RED);
            embedBuilder.addField("Error:", 
                    "To call the bot you have to be in a voice channel.", false);
        }

        
        log.info("{} has requested the Play command. full command: {} - ENTER", 
                event.getUser().getName(), event.getFullCommandName());

    }

    private boolean isValidURL(final String song) {
        return !(urlStructureIsValid(song) || isValidYoutubeURL(song));
    }

    private boolean urlStructureIsValid(final String song) {
        return UrlValidator.getInstance().isValid(song);
    }

    //based on this: https://stackoverflow.com/questions/24030892/android-java-check-if-url-is-valid-youtube-url
    private boolean isValidYoutubeURL(final String song) {
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
