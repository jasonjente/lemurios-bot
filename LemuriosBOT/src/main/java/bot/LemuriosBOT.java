package bot;

import bot.commands.Command;
import bot.commands.concrete.chat.*;
import bot.commands.concrete.images.DetectImageEdgesCommand;
import bot.commands.concrete.images.MemeCommand;
import bot.commands.concrete.images.UploadMemeCommand;
import bot.commands.concrete.music.*;
import bot.commands.concrete.music.radio.*;
import net.dv8tion.jda.api.EmbedBuilder;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static bot.constants.Commands.*;

@Component
public class LemuriosBOT extends ListenerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(LemuriosBOT.class);
    private static final String GENRE_OPTION = "genre";
    private AssemblemursCommand assemblemursCommand;
    private TakenNamesCommand takenNamesCommand;
    private CreditsCommand creditsCommand;
    private DetectImageEdgesCommand detectImageEdgesCommand;
    private HelpCommand helpCommand;
    private HistoryCommand historyCommand;
    private MemeCommand memeCommand;
    private UploadMemeCommand uploadMemeCommand;
    private PlayCommand playCommand;
    private StopCommand stopCommand;
    private PauseCommand pauseCommand;
    private SkipCommand skipCommand;
    private ResumeCommand resumeCommand;
    private NowPlayingCommand nowPlayingCommand;
    private JoinCommand joinCommand;
    private static final Map<String, Command> commands = new HashMap<>();
    private DisconnectCommand disconnectCommand;
    private LeaderboardCommand leaderboardCommand;
    private PlayCustomRadioCommand playCustomRadioCommand;
    private SetCustomRadioLinkCommand setCustomRadioLinkCommand;
    private GetCustomRadioLinkCommand getCustomRadioLinkCommand;
    private DeleteAllCustomRadioLinkCommand deleteAllCustomRadioLinkCommand;
    private DeleteGenreCustomRadioLinkCommand deleteGenreCustomRadioLinkCommand;

    //Guild Commands -- Commands get instantly deployed

    /**
     * Deploys the guild commands, these can change anytime during the bot startup.
     * @param event allows us to upload the commands to the discord server
     */
    @Override
    public void onGuildReady(@NonNull GuildReadyEvent event){
        List<CommandData> commandData = new ArrayList<>();
        commandData.add(Commands.slash(ASSEMLEMURS_COMMAND.getCommandName(),"Pings all Lemurioi Role Members -- Can be used if and only if you belong to that group."));

        commandData.add(Commands.slash(TAKEN_NAMES.getCommandName(),"Prints out all taken Lemur names."));

        commandData.add(Commands.slash(CREDITS_COMMAND.getCommandName(),"Prints out the application's credits."));

        OptionData optionDataDetection = new OptionData(OptionType.ATTACHMENT, "image", "Upload an image to detect its edges.",true);
        commandData.add(Commands.slash(DETECT_IMAGE_EDGES_COMMAND.getCommandName(),"Upload an image and the bot will return the detected edges in that image.").addOptions(optionDataDetection));

        commandData.add(Commands.slash(HELP_COMMAND.getCommandName(),"Prints all the available commands."));
        OptionData historyOptionData = new OptionData(OptionType.STRING,"command-name", "Optional: narrow down your search by providing a command name, e.g. play");
        commandData.add(Commands.slash(HISTORY_COMMAND.getCommandName(),"Prints the last 25 commands used.").addOptions(historyOptionData));

        commandData.add(Commands.slash(MEME_COMMAND.getCommandName(),"The bot will return with a random meme."));

        OptionData optionDataMeme = new OptionData(OptionType.ATTACHMENT, "meme-image", "Upload a meme to the BOT",true);
        commandData.add(Commands.slash(UPLOAD_MEME_COMMAND.getCommandName(),"Upload a meme to the Bot.").addOptions(optionDataMeme));

        OptionData optionDataSongToPlay = new OptionData(OptionType.STRING, "url", "Used to add a song to the queue",true);
        commandData.add(Commands.slash(PLAY_COMMAND.getCommandName(), "play a song via soundcloud, YouTube or from a discord CDN link.").addOptions(optionDataSongToPlay));

        commandData.add(Commands.slash(SKIP_COMMAND.getCommandName(), "Skips current song from the song list."));
        commandData.add(Commands.slash(PAUSE_COMMAND.getCommandName(), "Pauses current song from the song list."));
        commandData.add(Commands.slash(STOP_COMMAND.getCommandName(), "Stops execution and empties the song list."));
        commandData.add(Commands.slash(JOIN_COMMAND.getCommandName(), "Bot joins the voice channel the caller is in."));
        commandData.add(Commands.slash(NOW_PLAYING.getCommandName(), "Bot prints the song it is currently playing."));
        commandData.add(Commands.slash(RESUME_COMMAND.getCommandName(), "Bot unpauses the song it paused."));
        commandData.add(Commands.slash(DISCONNECT_COMMAND.getCommandName(), "Bot disconnects and empties the queue."));
        commandData.add(Commands.slash(LEADERBOARD_COMMAND.getCommandName(), leaderboardCommand.getCommandDescription()));

        OptionData setCustomRadioCommandOptionDataUrl = new OptionData((OptionType.STRING), "url" ,setCustomRadioLinkCommand.getCommandDescription(), true);
        OptionData setCustomRadioCommandOptionDataGenre = new OptionData((OptionType.STRING), GENRE_OPTION ,"specify the genre you want to add.", true);
        commandData.add(Commands.slash(setCustomRadioLinkCommand.getCommandName(), setCustomRadioLinkCommand.getCommandDescription()).addOptions(setCustomRadioCommandOptionDataUrl,setCustomRadioCommandOptionDataGenre));
        commandData.add(Commands.slash(getCustomRadioLinkCommand.getCommandName(), getCustomRadioLinkCommand.getCommandDescription()));

        OptionData playCustomRadioGenreOptionData = new OptionData((OptionType.STRING), GENRE_OPTION ,"specify the genre you want to add.", true);
        commandData.add(Commands.slash(playCustomRadioCommand.getCommandName(), playCustomRadioCommand.getCommandDescription()).addOptions(playCustomRadioGenreOptionData));

        OptionData deleteByGenre = new OptionData((OptionType.STRING), GENRE_OPTION,"specify the genre you want to delete.", true);
        commandData.add(Commands.slash(deleteGenreCustomRadioLinkCommand.getCommandName(), deleteGenreCustomRadioLinkCommand.getCommandDescription()).addOptions(deleteByGenre));
        commandData.add(Commands.slash(deleteAllCustomRadioLinkCommand.getCommandName(), deleteAllCustomRadioLinkCommand.getCommandDescription()));


        event.getGuild().updateCommands().addCommands(commandData).queue();
    }

    /**
     * Initialize the commands map.
     * The map has as a key the commands name (which is the same as the slash interaction event) and as a value
     * is passed the CommandXXX bean in order to call using polymorphism the appropriate Command instance.execute(event).
     */
    @PostConstruct
    private void init(){
        commands.put(ASSEMLEMURS_COMMAND.getCommandName(),assemblemursCommand);
        commands.put(TAKEN_NAMES.getCommandName(), takenNamesCommand);
        commands.put(CREDITS_COMMAND.getCommandName(),creditsCommand);
        commands.put(DETECT_IMAGE_EDGES_COMMAND.getCommandName(),detectImageEdgesCommand);
        commands.put(HELP_COMMAND.getCommandName(),helpCommand);
        commands.put(HISTORY_COMMAND.getCommandName(),historyCommand);
        commands.put(MEME_COMMAND.getCommandName(),memeCommand);
        commands.put(UPLOAD_MEME_COMMAND.getCommandName(),uploadMemeCommand);
        commands.put(PLAY_COMMAND.getCommandName(), playCommand);
        commands.put(PAUSE_COMMAND.getCommandName(), pauseCommand);
        commands.put(SKIP_COMMAND.getCommandName(), skipCommand);
        commands.put(STOP_COMMAND.getCommandName(), stopCommand);
        commands.put(JOIN_COMMAND.getCommandName(), joinCommand);
        commands.put(NOW_PLAYING.getCommandName(), nowPlayingCommand);
        commands.put(RESUME_COMMAND.getCommandName(), resumeCommand);
        commands.put(DISCONNECT_COMMAND.getCommandName(), disconnectCommand);
        commands.put(LEADERBOARD_COMMAND.getCommandName(), leaderboardCommand);
        commands.put(playCustomRadioCommand.getCommandName(), playCustomRadioCommand);
        commands.put(setCustomRadioLinkCommand.getCommandName(), setCustomRadioLinkCommand);
        commands.put(getCustomRadioLinkCommand.getCommandName(), getCustomRadioLinkCommand);
        commands.put(deleteAllCustomRadioLinkCommand.getCommandName(), deleteAllCustomRadioLinkCommand);
        commands.put(deleteGenreCustomRadioLinkCommand.getCommandName(), deleteGenreCustomRadioLinkCommand);
    }
    //Global command for production -- takes up to 1 hour to get deployed
   /** @Override
    public void onReady(ReadyEvent event) {
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

   OptionData optionDataSongToPlay = new OptionData(OptionType.STRING, "url", "Used to add a song to the queue",true);
   commandData.add(Commands.slash(PLAY_COMMAND.getValue(), "play a song via soundcloud, YouTube or from a discord CDN link.").addOptions(optionDataSongToPlay));


   event.getGuild().updateCommands().addCommands(commandData).queue();
    }*/

    /**
     * The correct command is chosen based on its type during the runtime.
     * For example when the user prompts the '/help' command, the map returns the helpCommand bean and then the execute()
     * method is called.
     *
     * @param event contains all the information needed for the command flow.
     */
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event){ 
        LOGGER.info("Message received from {} - Content: {} - ENTER", event.getInteraction().getUser().getAsTag(), event.getFullCommandName());
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
                    try {
                        if (commands.containsKey(event.getFullCommandName())) {
                            event.deferReply().queue(); // Tell discord we received the command, send a thinking... message to the user
                            Command command = commands.get(event.getFullCommandName());
                            command.execute(event);
                            command.earnPoints(event);
                        }
                    } catch (NullPointerException npe){
                        EmbedBuilder embedBuilder = new EmbedBuilder().setTitle("We encountered an error during command execution :(");
                        embedBuilder.setDescription("Please connect to a voice channel first before calling the bot!");
                        event.getInteraction().getHook().editOriginalEmbeds(embedBuilder.build()).queue();
                        LOGGER.error("ERROR:", npe);
                    } catch (RuntimeException e) {
                        EmbedBuilder embedBuilder = new EmbedBuilder().setTitle("We encountered an error during command execution :(");
                        embedBuilder.setDescription(e.getCause().getMessage());
                        event.getInteraction().getHook().editOriginalEmbeds(embedBuilder.build()).queue();
                        LOGGER.error("ERROR:", e);
                    }
                });
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
    public void setTakenNamesCommand(TakenNamesCommand takenNamesCommand) {
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
    @Autowired
    public void setPlayCommand(PlayCommand playCommand){
        this.playCommand = playCommand;
    }

    @Autowired
    public void setStopCommand(StopCommand stopCommand) {
        this.stopCommand = stopCommand;
    }

    @Autowired
    public void setPauseCommand(PauseCommand pauseCommand) {
        this.pauseCommand = pauseCommand;
    }
    @Autowired
    public void setSkipCommand(SkipCommand skipCommand) {
        this.skipCommand = skipCommand;
    }

    @Autowired
    public void setJoinCommand(JoinCommand joinCommand) {
        this.joinCommand = joinCommand;
    }

    @Autowired
    public void setResumeCommand(ResumeCommand resumeCommand) {
        this.resumeCommand = resumeCommand;
    }

    @Autowired
    public void setNowPlayingCommand(NowPlayingCommand nowPlayingCommand) {
        this.nowPlayingCommand = nowPlayingCommand;
    }

    @Autowired
    public void setDisconnectCommand(DisconnectCommand disconnectCommand) {
        this.disconnectCommand = disconnectCommand;
    }

    @Autowired
    public void setLeaderboardCommand(LeaderboardCommand leaderboardCommand) {
        this.leaderboardCommand = leaderboardCommand;
    }

    @Autowired
    public void setPlayCustomRadioCommand(PlayCustomRadioCommand playCustomRadioCommand) {
        this.playCustomRadioCommand = playCustomRadioCommand;
    }

    @Autowired
    public void setSetCustomRadioLinkCommand(SetCustomRadioLinkCommand setCustomRadioLinkCommand) {
        this.setCustomRadioLinkCommand = setCustomRadioLinkCommand;
    }
    @Autowired
    public void setGetCustomRadioLinkCommand(GetCustomRadioLinkCommand getCustomRadioLinkCommand) {
        this.getCustomRadioLinkCommand = getCustomRadioLinkCommand;
    }

    @Autowired
    public void setDeleteAllCustomRadioLinkCommand(DeleteAllCustomRadioLinkCommand deleteAllCustomRadioLinkCommand) {
        this.deleteAllCustomRadioLinkCommand = deleteAllCustomRadioLinkCommand;
    }

    @Autowired
    public void setDeleteGenreCustomRadioLinkCommand(DeleteGenreCustomRadioLinkCommand deleteGenreCustomRadioLinkCommand) {
        this.deleteGenreCustomRadioLinkCommand = deleteGenreCustomRadioLinkCommand;
    }

    public static Map<String, Command> getCommands() {
        return commands;
    }
}
