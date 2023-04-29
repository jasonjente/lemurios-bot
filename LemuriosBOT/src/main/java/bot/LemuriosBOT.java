package bot;

import bot.commands.Command;
import bot.commands.concrete.*;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static bot.constants.Constants.*;

@Component
public class LemuriosBOT extends ListenerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(LemuriosBOT.class);
    private AssemblemursCommand assemblemursCommand;
    private TakenNamesCommand takenNamesCommand;
    private CreditsCommand creditsCommand;
    private DetectImageEdgesCommand detectImageEdgesCommand;
    private HelpCommand helpCommand;
    private HistoryCommand historyCommand;
    private MemeCommand memeCommand;
    private UploadMemeCommand uploadMemeCommand;
    private final Map<String, Command> commands = new HashMap<>();


    //Guild Commands -- Commands get instantly deployed

    /**
     * Deploys the guild commands, these can change anytime during the bot startup.
     * @param event
     */
    @Override
    public void onGuildReady(@NonNull GuildReadyEvent event){
        List<CommandData> commandData = new ArrayList<>();
        commandData.add(Commands.slash(ASSEMLEMURS_COMMAND.getValue(),"Pings all Lemurioi Role Members -- Can be used if and only if you belong to that group."));
        commandData.add(Commands.slash(TAKEN_NAMES.getValue(),"Prints out all taken Lemur names."));
        commandData.add(Commands.slash(CREDITS_COMMAND.getValue(),"Prints out the application's credits."));
        OptionData optionDataDetection = new OptionData(OptionType.ATTACHMENT, "image", "Upload an image to detect its edges.",true);
        commandData.add(Commands.slash(DETECT_IMAGE_EDGES.getValue(),"Upload an image and the bot will return the detected edges in that image.").addOptions(optionDataDetection));
        commandData.add(Commands.slash(HELP_COMMAND.getValue(),"Prints all the available commands."));
        commandData.add(Commands.slash(HISTORY_COMMAND.getValue(),"Prints the last 25 commands used."));
        commandData.add(Commands.slash(MEME_COMMAND.getValue(),"The bot will return with a random meme."));
        OptionData optionDataMeme = new OptionData(OptionType.ATTACHMENT, "meme-image", "Upload a meme to the BOT",true);
        commandData.add(Commands.slash(UPLOAD_MEME_COMMAND.getValue(),"Upload a meme to the Bot.").addOptions(optionDataMeme));

        event.getGuild().updateCommands().addCommands(commandData).queue();
    }

    /**
     * Initialize the commands map.
     * The map has as a key the commands name (which is the same as the slash interaction event) and as a value
     * is passed the CommandXXX bean in order to call using encapsulation the appropriate Command instance.execute(event).
     */
    @PostConstruct
    private void init(){
        commands.put(ASSEMLEMURS_COMMAND.getValue(),assemblemursCommand);
        commands.put(TAKEN_NAMES.getValue(), takenNamesCommand);
        commands.put(CREDITS_COMMAND.getValue(),creditsCommand);
        commands.put(DETECT_IMAGE_EDGES.getValue(),detectImageEdgesCommand);
        commands.put(HELP_COMMAND.getValue(),helpCommand);
        commands.put(HISTORY_COMMAND.getValue(),historyCommand);
        commands.put(MEME_COMMAND.getValue(),memeCommand);
        commands.put(UPLOAD_MEME_COMMAND.getValue(),uploadMemeCommand);
    }
    //Global command for production -- takes up to 1 hour to get deployed
   /** @Override
    public void onReady(ReadyEvent event) {
    List<CommandData> commandData = new ArrayList<>();
        commandData.add(Commands.slash(ASSEMLEMURS_COMMAND.getValue(),"Pings all Lemurioi Role Members -- Can be used if and only if you belong to that group."));
        commandData.add(Commands.slash(AVAILABLE_NAMES.getValue(),"Prints out all taken Lemurioi names."));
        commandData.add(Commands.slash(CREDITS_COMMAND.getValue(),"Prints out the application's credits."));
        commandData.add(Commands.slash(DETECT_IMAGE_EDGES.getValue(),"Upload an image and the bot will return the detected edges in that image."));
        commandData.add(Commands.slash(HELP_COMMAND.getValue(),"Prints all the available commands."));
        commandData.add(Commands.slash(HISTORY_COMMAND.getValue(),"Prints the last 25 commands used."));
        commandData.add(Commands.slash(MEME_COMMAND.getValue(),"The bot will return with a random meme."));
        OptionData optionData = new OptionData(OptionType.ATTACHMENT, "meme-image", "Upload a meme to the BOT",true);
        commandData.add(Commands.slash(UPLOAD_MEME_COMMAND.getValue(),"Upload a meme to the Bot.").addOptions(optionData));

        event.getGuild().updateCommands().addCommands(commandData).queue();
    }*/

    /**
     * The correct command is chosen based on its type during the runtime.
     * For example when the user prompts the '/help' command, the map returns the helpCommand bean and then the execute()
     * method is called.
     *
     * The idea is to avoid the if/else statement hell and the Play/Pause/Skip/Stop commands will become classes in the
     * @param event contains all the information needed for the command flow.
     */
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event){ 
        LOGGER.info("Message received from {} - Content: {} - ENTER", event.getInteraction().getUser().getAsTag(), event.getFullCommandName());
        if (commands.containsKey(event.getFullCommandName())){
            event.deferReply().queue(); // Tell discord we received the command, send a thinking... message to the user
            commands.get(event.getFullCommandName()).execute(event);
        }
        LOGGER.info("Message received from {} - Content: {} - LEAVE", event.getInteraction().getUser().getAsTag(), event.getFullCommandName());
    }

    /**
     * Dependency Injection Setters
     */
    @Autowired
    public void setAssemblemursCommand(AssemblemursCommand assemblemursCommand) {
        this.assemblemursCommand = assemblemursCommand;
    }

    @Autowired
    public void setAvailableNamesCommand(TakenNamesCommand takenNamesCommand) {
        this.takenNamesCommand = takenNamesCommand;
    }

    @Autowired
    public void setCreditsCommand(CreditsCommand creditsCommand) {
        this.creditsCommand = creditsCommand;
    }

    @Autowired
    public void setDetectImageEdgesCommand(DetectImageEdgesCommand detectImageEdgesCommand) {
        this.detectImageEdgesCommand = detectImageEdgesCommand;
    }

    @Autowired
    public void setHelpCommand(HelpCommand helpCommand) {
        this.helpCommand = helpCommand;
    }

    @Autowired
    public void setHistoryCommand(HistoryCommand historyCommand) {
        this.historyCommand = historyCommand;
    }

    @Autowired
    public void setMemeCommand(MemeCommand memeCommand) {
        this.memeCommand = memeCommand;
    }

    @Autowired
    public void setUploadMemeCommand(UploadMemeCommand uploadMemeCommand) {
        this.uploadMemeCommand = uploadMemeCommand;
    }

}
