package bot.commands.concrete.music;

import bot.commands.Command;
import bot.application.utils.music.MusicPlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static bot.application.constants.Commands.DISCONNECT_COMMAND;


public class DisconnectCommand extends Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(DisconnectCommand.class);
    private MusicPlayerManager musicPlayerManager;

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        LOGGER.info("{} has requested the Disconnect command - ENTER.", event.getUser().getName());
        EmbedBuilder embedBuilder = new EmbedBuilder();
        try{
            String disconnectedChannel = musicPlayerManager.disconnectFromVoiceChannel(event);
            embedBuilder.addField("Disconnecting..", "Bot stopped playing and disconnected from: " + disconnectedChannel , false);
        }catch (NullPointerException e){
            embedBuilder.addField("Error:", "The bot is not in a voice channel!", false);
        }
        LOGGER.info("{} has requested the Disconnect command - LEAVE.", event.getUser().getName());
        earnPoints(event);
        event.getInteraction().getHook().editOriginalEmbeds(embedBuilder.build()).queue();
    }

    @Override
    public String getCommandDescription() {
        return "Disconnects the bot the voice channel the user is currently in.";
    }

    @Override
    public String getCommandName() {
        return DISCONNECT_COMMAND.getCommandName();
    }

    @Autowired
    private void setMusicBot(MusicPlayerManager musicPlayerManager){
        this.musicPlayerManager = musicPlayerManager;
    }
}
