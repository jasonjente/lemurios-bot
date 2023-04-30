package bot.commands.concrete.music;

import bot.commands.Command;
import bot.music.MusicPlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.Objects;

@Service
public class SkipCommand extends Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(SkipCommand.class);
    private MusicPlayerManager musicPlayerManager;

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        LOGGER.info("{} has requested the Skip command.", event.getUser().getName());
        EmbedBuilder embedBuilder = new EmbedBuilder();
        try {
            String nowPlaying = musicPlayerManager.getSongPlaying(Objects.requireNonNull(event.getGuild())).get(0);
            embedBuilder.setTitle("LEMURIOS BOT - Skipped track :").setColor(Color.ORANGE);
            TextChannel textChannel = event.getChannel().asTextChannel();
            musicPlayerManager.skipTrack(event, textChannel);
            embedBuilder.addField("Skipped track:", nowPlaying, true);
        } catch (NullPointerException e){
            LOGGER.warn("Not playing any songs ?");
        }
        createHistoryEntry(event);
        LOGGER.info("{} has requested the Skip command.", event.getUser().getName());
    }

    @Autowired
    private void setMusicPlayerManager(MusicPlayerManager musicPlayerManager){
        this.musicPlayerManager = musicPlayerManager;
    }
}
