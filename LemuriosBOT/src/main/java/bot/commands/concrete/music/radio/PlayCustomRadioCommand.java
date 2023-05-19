package bot.commands.concrete.music.radio;

import bot.commands.Command;
import bot.dataservice.DataService;
import bot.dataservice.leveling.repositories.CustomLinkRepository;
import bot.dataservice.model.CustomLink;
import bot.music.MusicPlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.List;

import static bot.constants.Commands.PLAY_RADIO;

@Service
public class PlayCustomRadioCommand extends Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayCustomRadioCommand.class);
    private MusicPlayerManager musicPlayerManager;
    private DataService dataService;

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        LOGGER.info("{} has requested the Play Custom Radio command. full command: {} - ENTER", event.getUser().getName(), event.getFullCommandName());
        EmbedBuilder embedBuilder = new EmbedBuilder();
        // Make sure we only respond to events that occur in a guild
        if (!event.isFromGuild()) return;
        // if this is not a bot make sure to check if this message is sent by yourself!
        if (event.getUser().isBot()) return;
        String guildId = event.getGuild().getId();
        String genre = event.getInteraction().getOptions().get(0).getAsString();
        List<CustomLink> list = dataService.findCustomLinksByDiscordServerAndGenre(guildId);
        CustomLink customLink = null;

        for(CustomLink entry:list){
            if (genre.equals(entry.getGenre())){
                customLink = entry;
                break;
            }
        }

        if(customLink != null){
            if (event.getInteraction().getMember().getVoiceState() != null) {
                String song = customLink.getUrl();
                TextChannel textChannel = event.getChannel().asTextChannel();
                VoiceChannel voiceChannel = event.getInteraction().getMember().getVoiceState().getChannel().asVoiceChannel();
                LOGGER.info("Voice channel {}", voiceChannel.getName());
                embedBuilder.setTitle("Lemurios Music BOT - Started playing Custom URL!" + song).setColor(Color.YELLOW);
                musicPlayerManager.stopAndLoadAndPlay(textChannel, voiceChannel, song);
            } else {
                embedBuilder.setTitle("Lemurios Music BOT - Error.").setColor(Color.RED);
                embedBuilder.addField("Error:", "To call the bot you have to be in a voice channel.", false);
            }
        } else {
            embedBuilder.setTitle("Lemurios Music BOT - Error.").setColor(Color.RED);
            embedBuilder.addField("Error:", "Please register first a URL by using the /set-radio command!", false);
        }

        event.getInteraction().getHook().editOriginalEmbeds(embedBuilder.build()).queue();
        createHistoryEntry(event);
        LOGGER.info("{} has requested the PlayCustomRadioCommand command. full command: {} - ENTER", event.getUser().getName(), event.getFullCommandName());

    }

    @Override
    public String getCommandDescription() {
        return "First set a custom Radio URL for your server with the set-radio command!";
    }

    @Override
    public String getCommandName() {
        return PLAY_RADIO.getCommandName();
    }

    @Autowired
    public void setMusicBot(MusicPlayerManager musicPlayerManager){
        this.musicPlayerManager = musicPlayerManager;
    }

    @Autowired
    public void setDataService(DataService dataService) {
        this.dataService = dataService;
    }

}
