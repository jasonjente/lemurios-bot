package bot.commands.concrete.music;

import bot.commands.Command;
import bot.application.utils.music.MusicPlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.*;
import java.util.Objects;

import static bot.application.constants.Commands.SKIP_COMMAND;


public class SkipCommand extends Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(SkipCommand.class);
    private MusicPlayerManager musicPlayerManager;

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        LOGGER.info("{} has requested the Skip command.", event.getUser().getName());
        EmbedBuilder embedBuilder = new EmbedBuilder();
        try {
            String toBeSkipped = musicPlayerManager.getSongPlaying(Objects.requireNonNull(event.getGuild())).get(0);
            embedBuilder.setTitle("LEMURIOS BOT - Skipped track :").setColor(Color.ORANGE);
            TextChannel textChannel = event.getChannel().asTextChannel();
            musicPlayerManager.skipTrack(event, textChannel);
            embedBuilder.addField("Skipped track:", toBeSkipped, true);
            String nowPlaying = musicPlayerManager.getSongPlaying(Objects.requireNonNull(event.getGuild())).get(0);
            embedBuilder.addField("Now Playing track:", nowPlaying, true);
        } catch (NullPointerException e){
            LOGGER.warn("Not playing any songs ?");
            embedBuilder.addField("Lemurios Music BOT has no songs in its queue", "Use /play to add a song to the queue.", true);
        }
        earnPoints(event);
        LOGGER.info("{} has requested the Skip command.", event.getUser().getName());
        event.getInteraction().getHook().editOriginalEmbeds(embedBuilder.build()).queue();

    }

    @Override
    public String getCommandDescription() {
        return "Skips current song playing and goes to the next song in the queue.";
    }

    @Override
    public String getCommandName() {
        return SKIP_COMMAND.getCommandName();
    }

    @Autowired
    private void setMusicPlayerManager(MusicPlayerManager musicPlayerManager){
        this.musicPlayerManager = musicPlayerManager;
    }
}
