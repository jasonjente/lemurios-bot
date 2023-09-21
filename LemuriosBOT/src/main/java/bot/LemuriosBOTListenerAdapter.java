package bot;

import bot.commands.Command;
import bot.commands.concrete.chat.*;
import bot.commands.concrete.images.DetectImageEdgesCommand;
import bot.commands.concrete.images.MemeCommand;
import bot.commands.concrete.images.UploadBatchMemesCommand;
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
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static bot.constants.Commands.*;

@Component
public class LemuriosBOTListenerAdapter extends ListenerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(LemuriosBOTListenerAdapter.class);
    private static final String GENRE_OPTION = "genre";
    private AssemblemursCommand assemblemursCommand;
    private TakenNamesCommand takenNamesCommand;
    private CreditsCommand creditsCommand;
    private DetectImageEdgesCommand detectImageEdgesCommand;
    private HelpCommand helpCommand;
    private HistoryCommand historyCommand;
    private MemeCommand memeCommand;
    private UploadMemeCommand uploadMemeCommand;
    private UploadBatchMemesCommand uploadBatchMemesCommand;
    private PlayCommand playCommand;
    private StopCommand stopCommand;
    private PauseCommand pauseCommand;
    private SkipCommand skipCommand;
    private ResumeCommand resumeCommand;
    private NowPlayingCommand nowPlayingCommand;
    private JoinCommand joinCommand;
    private DisconnectCommand disconnectCommand;
    private LeaderboardCommand leaderboardCommand;
    private PlayCustomRadioCommand playCustomRadioCommand;
    private SetCustomRadioLinkCommand setCustomRadioLinkCommand;
    private GetCustomRadioLinkCommand getCustomRadioLinkCommand;
    private DeleteAllCustomRadioLinkCommand deleteAllCustomRadioLinkCommand;
    private DeleteGenreCustomRadioLinkCommand deleteGenreCustomRadioLinkCommand;
    private CreateInviteCommand createInviteCommand;
    private static final Map<String, Command> commands = new HashMap<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private final Set<Future<?>> futuresSet = new HashSet<>();

    //Guild Commands -- Commands get instantly deployed

    /**
     * Deploys the guild commands, these can change anytime during the bot startup.
     * @param event allows us to upload the commands to the discord server
     */
    @Override
    public void onGuildReady(@NonNull GuildReadyEvent event){
        List<CommandData> commandData  = setupCommandOptions();

        //Push the commands to discord
        event.getGuild().updateCommands().addCommands(commandData).queue();
    }

    /**
     * Initialize the commands map.
     * The map has as a key the commands name (which is the same as the slash interaction event) and as a value
     * is passed the CommandXXX bean in order to call using polymorphism the appropriate Command instance.execute(event).
     */
    @PostConstruct
    private void init(){
        commands.put(assemblemursCommand.getCommandName(),assemblemursCommand);
        commands.put(takenNamesCommand.getCommandName(), takenNamesCommand);
        commands.put(creditsCommand.getCommandName(),creditsCommand);
        commands.put(detectImageEdgesCommand.getCommandName(),detectImageEdgesCommand);
        commands.put(helpCommand.getCommandName(),helpCommand);
        commands.put(historyCommand.getCommandName(),historyCommand);
        commands.put(memeCommand.getCommandName(),memeCommand);
        commands.put(uploadMemeCommand.getCommandName(),uploadMemeCommand);
        commands.put(playCommand.getCommandName(), playCommand);
        commands.put(pauseCommand.getCommandName(), pauseCommand);
        commands.put(skipCommand.getCommandName(), skipCommand);
        commands.put(stopCommand.getCommandName(), stopCommand);
        commands.put(joinCommand.getCommandName(), joinCommand);
        commands.put(nowPlayingCommand.getCommandName(), nowPlayingCommand);
        commands.put(resumeCommand.getCommandName(), resumeCommand);
        commands.put(disconnectCommand.getCommandName(), disconnectCommand);
        commands.put(leaderboardCommand.getCommandName(), leaderboardCommand);
        commands.put(playCustomRadioCommand.getCommandName(), playCustomRadioCommand);
        commands.put(setCustomRadioLinkCommand.getCommandName(), setCustomRadioLinkCommand);
        commands.put(getCustomRadioLinkCommand.getCommandName(), getCustomRadioLinkCommand);
        commands.put(deleteAllCustomRadioLinkCommand.getCommandName(), deleteAllCustomRadioLinkCommand);
        commands.put(deleteGenreCustomRadioLinkCommand.getCommandName(), deleteGenreCustomRadioLinkCommand);
        commands.put(createInviteCommand.getCommandName(), createInviteCommand);
        commands.put(uploadBatchMemesCommand.getCommandName(), uploadBatchMemesCommand);
    }

    @PreDestroy
    private void onDestroy(){
        LOGGER.info("Shutting down listener!");
        for (Future<?> runnableFuture : futuresSet){
            boolean canceled = runnableFuture.cancel(true);
            LOGGER.info("Command terminated: {}, is done: {} , is canceled: {}.", canceled, runnableFuture.isDone(), runnableFuture.isCancelled());
        }

        executor.shutdownNow();
    }
    /**
     * The correct command is chosen based on its type during the runtime.
     * For example when the user prompts the '/help' command, the map returns the helpCommand bean and then the execute()
     * method is called.
     *
     * @param event contains all the information needed for the command flow.
     */
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event){ 
        LOGGER.info("Message received from {} - Content: {} - ENTER", event.getInteraction().getUser().getName(), event.getFullCommandName());
        futuresSet.add(executor.submit(getRunnable(event)));
        LOGGER.info("Message received from {} - Content: {} - LEAVE", event.getInteraction().getUser().getName(), event.getFullCommandName());
    }

    @NotNull
    private Runnable getRunnable(SlashCommandInteractionEvent event) {
        return () -> {
            try {
                if (commands.containsKey(event.getFullCommandName())) {
                    event.deferReply().queue(); // Tell discord we received the command, send a thinking... message to the user
                    Command command = commands.get(event.getFullCommandName());
                    command.execute(event);
                }
            } catch (RuntimeException runtimeException){
                EmbedBuilder embedBuilder = new EmbedBuilder().setTitle("We encountered an error during command execution :(");
                event.getInteraction().getHook().editOriginalEmbeds(embedBuilder.build()).queue();
                LOGGER.error("ERROR:", runtimeException);
            }
        };
    }

    /**
     * Creates the commands, their input parameters if they have any and their relevant information, that will be published to discord
     * @returns a list of all the Discord CommonData which hold the information about commands.
     */
    private List<CommandData> setupCommandOptions() {
        List<CommandData> commandData = new ArrayList<>();

        /* Chat Commands */
        // /assemblemurs
        OptionData assemblemursOptions = new OptionData(OptionType.STRING, "game","(optional) Specify what game you want to play!",false);
        commandData.add(Commands.slash(ASSEMLEMURS_COMMAND.getCommandName(),"Pings all Lemurioi Role Members -- Can be used if and only if you belong to that group.").addOptions(assemblemursOptions));
        // /taken-names
        commandData.add(Commands.slash(TAKEN_NAMES.getCommandName(),"Prints out all taken Lemur names."));
        // /credits
        commandData.add(Commands.slash(CREDITS_COMMAND.getCommandName(),"Prints out the application's credits."));
        // /help
        commandData.add(Commands.slash(HELP_COMMAND.getCommandName(),"Prints all the available commands."));
        // /history
        OptionData historyOptionData = new OptionData(OptionType.STRING,"command-name", "Optional: narrow down your search by providing a command name, e.g. play");
        commandData.add(Commands.slash(HISTORY_COMMAND.getCommandName(),"Prints the last 25 commands used.").addOptions(historyOptionData));
        // /leaderboard
        commandData.add(Commands.slash(LEADERBOARD_COMMAND.getCommandName(), leaderboardCommand.getCommandDescription()));
        // /invite
        commandData.add(Commands.slash(createInviteCommand.getCommandName(), createInviteCommand.getCommandDescription()));

        /* Image Commands */
        // /detect-edges
        OptionData optionDataDetection = new OptionData(OptionType.ATTACHMENT, "image", "Upload an image to detect its edges.",true);
        commandData.add(Commands.slash(DETECT_IMAGE_EDGES_COMMAND.getCommandName(),"Upload an image and the bot will return the detected edges in that image.").addOptions(optionDataDetection));
        // /meme
        commandData.add(Commands.slash(MEME_COMMAND.getCommandName(),"The bot will return with a random meme."));
        // /upload
        OptionData optionDataMeme = new OptionData(OptionType.ATTACHMENT, "meme-image", "Upload a meme to the BOT",true);
        commandData.add(Commands.slash(UPLOAD_MEME_COMMAND.getCommandName(),"Upload a meme to the Bot.").addOptions(optionDataMeme));
        // /upload-batch-memes
        OptionData optionDataBatchMemes = new OptionData(OptionType.ATTACHMENT, "zip-file", "Upload a zip file containing memes!",true);
        commandData.add(Commands.slash(uploadBatchMemesCommand.getCommandName(),"Upload a meme to the Bot.").addOptions(optionDataBatchMemes));

        /* Music Commands */
        // /play :url
        OptionData optionDataSongToPlay = new OptionData(OptionType.STRING, "search", "Enter the title you are looking for or a URL",true);
        commandData.add(Commands.slash(PLAY_COMMAND.getCommandName(), "Search and play a song via YouTube or from a discord CDN link.").addOptions(optionDataSongToPlay));
        // /play-radio :genre
        OptionData playCustomRadioGenreOptionData = new OptionData((OptionType.STRING), GENRE_OPTION ,"specify the genre you want to add.", true);
        commandData.add(Commands.slash(playCustomRadioCommand.getCommandName(), playCustomRadioCommand.getCommandDescription()).addOptions(playCustomRadioGenreOptionData));
        // /skip
        commandData.add(Commands.slash(SKIP_COMMAND.getCommandName(), "Skips current song from the song list."));
        // /pause
        commandData.add(Commands.slash(PAUSE_COMMAND.getCommandName(), "Pauses current song from the song list."));
        // /resume
        commandData.add(Commands.slash(STOP_COMMAND.getCommandName(), "Stops execution and empties the song list."));
        // /join
        commandData.add(Commands.slash(JOIN_COMMAND.getCommandName(), "Bot joins the voice channel the caller is in."));
        // /now-playing
        commandData.add(Commands.slash(NOW_PLAYING.getCommandName(), "Bot prints the song it is currently playing."));
        // /resume
        commandData.add(Commands.slash(RESUME_COMMAND.getCommandName(), "Bot unpauses the song it paused."));
        // /disconnect
        commandData.add(Commands.slash(DISCONNECT_COMMAND.getCommandName(), "Bot disconnects and empties the queue."));
        // /set-radio-url :url :genre
        OptionData setCustomRadioCommandOptionDataUrl = new OptionData((OptionType.STRING), "url" ,setCustomRadioLinkCommand.getCommandDescription(), true);
        OptionData setCustomRadioCommandOptionDataGenre = new OptionData((OptionType.STRING), GENRE_OPTION ,"specify the genre you want to add.", true);
        commandData.add(Commands.slash(setCustomRadioLinkCommand.getCommandName(), setCustomRadioLinkCommand.getCommandDescription()).addOptions(setCustomRadioCommandOptionDataUrl,setCustomRadioCommandOptionDataGenre));
        // /get-radio-urls
        commandData.add(Commands.slash(getCustomRadioLinkCommand.getCommandName(), getCustomRadioLinkCommand.getCommandDescription()));
        // /delete-genre
        OptionData deleteByGenre = new OptionData((OptionType.STRING), GENRE_OPTION,"specify the genre you want to delete.", true);
        commandData.add(Commands.slash(deleteGenreCustomRadioLinkCommand.getCommandName(), deleteGenreCustomRadioLinkCommand.getCommandDescription()).addOptions(deleteByGenre));
        // /delete-all
        commandData.add(Commands.slash(deleteAllCustomRadioLinkCommand.getCommandName(), deleteAllCustomRadioLinkCommand.getCommandDescription()));
        return commandData;
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

    @Autowired
    public void setCreateInviteCommand(CreateInviteCommand createInviteCommand) {
        this.createInviteCommand = createInviteCommand;
    }

    @Autowired
    public void setUploadBatchMemes(UploadBatchMemesCommand uploadBatchMemesCommand) {
        this.uploadBatchMemesCommand = uploadBatchMemesCommand;
    }

    public static Map<String, Command> getCommands() {
        return commands;
    }
}
