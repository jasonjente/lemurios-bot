package bot;

import bot.commands.Command;
import bot.commands.concrete.chat.*;
import bot.commands.concrete.meme.MemeCommand;
import bot.commands.concrete.meme.UploadBatchMemesCommand;
import bot.commands.concrete.meme.UploadMemeCommand;
import bot.commands.concrete.music.*;
import bot.commands.concrete.music.radio.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
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
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static bot.application.constants.Commands.*;

@Component
@RequiredArgsConstructor
public class LemuriosBOTListenerAdapter extends ListenerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(LemuriosBOTListenerAdapter.class);
    private static final String GENRE_OPTION = "genre";
    private final AssemblemursCommand assemblemursCommand;
    private final TakenNamesCommand takenNamesCommand;
    private final CreditsCommand creditsCommand;
    private final HelpCommand helpCommand;
    private final MemeCommand memeCommand;
    private final UploadMemeCommand uploadMemeCommand;
    private final UploadBatchMemesCommand uploadBatchMemesCommand;
    private final PlayCommand playCommand;
    private final StopCommand stopCommand;
    private final PauseCommand pauseCommand;
    private final SkipCommand skipCommand;
    private final ResumeCommand resumeCommand;
    private final NowPlayingCommand nowPlayingCommand;
    private final JoinCommand joinCommand;
    private final DisconnectCommand disconnectCommand;
    private final LeaderboardCommand leaderboardCommand;
    private final PlayCustomRadioCommand playCustomRadioCommand;
    private final SetCustomRadioLinkCommand setCustomRadioLinkCommand;
    private final GetCustomRadioLinkCommand getCustomRadioLinkCommand;
    private final DeleteAllCustomRadioLinkCommand deleteAllCustomRadioLinkCommand;
    private final DeleteGenreCustomRadioLinkCommand deleteGenreCustomRadioLinkCommand;
    private final CreateInviteCommand createInviteCommand;

    @Getter
    private static final Map<String, Command> commands = new HashMap<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private final Set<Future<?>> futuresSet = new HashSet<>();

    //Guild Commands -- Commands get instantly deployed

    /**
     * Deploys the guild commands, these can change anytime during the bot startup.
     *
     * @param event allows us to upload the commands to the discord server
     */
    @Override
    public void onGuildReady(@NonNull GuildReadyEvent event) {
        List<CommandData> commandData = setupCommandOptions();

        //Push the commands to discord
        event.getGuild().updateCommands().addCommands(commandData).queue();
    }

    /**
     * Initialize the commands map.
     * The map has as a key the commands name (which is the same as the slash interaction event) and as a value
     * is passed the CommandXXX bean in order to call using polymorphism the appropriate Command instance.execute(event).
     */
    @PostConstruct
    private void init() {
        commands.put(assemblemursCommand.getCommandName(), assemblemursCommand);
        commands.put(takenNamesCommand.getCommandName(), takenNamesCommand);
        commands.put(creditsCommand.getCommandName(), creditsCommand);
        commands.put(helpCommand.getCommandName(), helpCommand);
        commands.put(memeCommand.getCommandName(), memeCommand);
        commands.put(uploadMemeCommand.getCommandName(), uploadMemeCommand);
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
    private void onDestroy() {
        LOGGER.info("Shutting down listener!");
        for (Future<?> runnableFuture : futuresSet) {
            boolean canceled = runnableFuture.cancel(true);
            LOGGER.info("Command terminated: {}, is done: {} , is canceled: {}.",
                    canceled, runnableFuture.isDone(), runnableFuture.isCancelled());
        }

        executor.shutdownNow();
    }

    /**
     * Entrypoint of the bot for the commands.
     * The correct command is chosen based on its type during the runtime.
     * For example when the user prompts the '/help' command, the map returns
     * the helpCommand bean and then the execute()
     * method is called.
     *
     * @param event contains all the information needed for the command flow.
     */
    @Override
    public void onSlashCommandInteraction(final SlashCommandInteractionEvent event) {
        LOGGER.info("Message received from {} - Content: {} - ENTER",
                event.getInteraction().getUser().getName(), event.getFullCommandName());
        futuresSet.add(executor.submit(getRunnable(event)));
        LOGGER.info("Message received from {} - Content: {} - LEAVE",
                event.getInteraction().getUser().getName(), event.getFullCommandName());
    }

    @NotNull
    private Runnable getRunnable(final SlashCommandInteractionEvent event) {
        return () -> {
            try {
                if (commands.containsKey(event.getFullCommandName())) {
                    event.deferReply().queue();
                    // Inform discord that the bot has received the command, send a thinking... message to the user
                    var command = commands.get(event.getFullCommandName());
                    command.execute(event);
                }
            } catch (RuntimeException runtimeException) {
                var embedBuilder = new EmbedBuilder().setTitle("We encountered an error during command execution :(");
                event.getInteraction().getHook().editOriginalEmbeds(embedBuilder.build()).queue();
                LOGGER.error("ERROR:", runtimeException);
            }
        };
    }

    /**
     * Creates the commands, their input parameters if they have any and their relevant information, that will be published to discord
     *
     * @returns a list of all the Discord CommonData which hold the information about commands.
     */
    private List<CommandData> setupCommandOptions() {
        var commandData = new ArrayList<CommandData>();

        /* Chat Commands */
        // /assemblemurs
        var assemblemursOptions = new OptionData(OptionType.STRING,
                "game", "(optional) Specify what game you want to play!", false);
        commandData.add(Commands.slash(ASSEMLEMURS_COMMAND.getCommandName(),
                "Pings all Lemurioi Role Members -- Can be used if and only if you belong to that group.").addOptions(assemblemursOptions));
        // /taken-names
        commandData.add(Commands.slash(TAKEN_NAMES.getCommandName(),
                "Prints out all taken Lemur names."));
        // /credits
        commandData.add(Commands.slash(CREDITS_COMMAND.getCommandName(),
                "Prints out the application's credits."));
        // /help
        commandData.add(Commands.slash(HELP_COMMAND.getCommandName(),
                "Prints all the available commands."));
        // /leaderboard
        commandData.add(Commands.slash(LEADERBOARD_COMMAND.getCommandName(),
                leaderboardCommand.getCommandDescription()));
        // /invite
        commandData.add(Commands.slash(createInviteCommand.getCommandName(),
                createInviteCommand.getCommandDescription()));

        /* Image Commands */
        // /detect-edges
        var optionDataDetection = new OptionData(OptionType.ATTACHMENT,
                "image", "Upload an image to detect its edges.", true);
        commandData.add(Commands.slash(DETECT_IMAGE_EDGES_COMMAND.getCommandName(),
                "Upload an image and the bot will return the detected edges in that image.").addOptions(optionDataDetection));
        // /meme
        commandData.add(Commands.slash(MEME_COMMAND.getCommandName(),
                "The bot will return with a random meme."));
        // /upload
        var optionDataMeme = new OptionData(OptionType.ATTACHMENT,
                "meme-image", "Upload a meme to the BOT", true);
        commandData.add(Commands.slash(UPLOAD_MEME_COMMAND.getCommandName(),
                "Upload a meme to the Bot.").addOptions(optionDataMeme));
        // /upload-batch-memes
        var optionDataBatchMemes = new OptionData(OptionType.ATTACHMENT,
                "zip-file", "Upload a zip file containing memes!", true);
        commandData.add(Commands.slash(uploadBatchMemesCommand.getCommandName(),
                "Upload a meme to the Bot.").addOptions(optionDataBatchMemes));

        /* Music Commands */
        // /play :url
        var optionDataSongToPlay = new OptionData(OptionType.STRING, "search",
                "Enter the title you are looking for or a URL", true);
        commandData.add(Commands.slash(PLAY_COMMAND.getCommandName(),
                "Search and play a song via YouTube or from a discord CDN link.").addOptions(optionDataSongToPlay));
        // /play-radio :genre
        var playCustomRadioGenreOptionData = new OptionData((OptionType.STRING), GENRE_OPTION,
                "specify the genre you want to add.", true);
        commandData.add(Commands.slash(playCustomRadioCommand.getCommandName(),
                playCustomRadioCommand.getCommandDescription()).addOptions(playCustomRadioGenreOptionData));
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
        var setCustomRadioCommandOptionDataUrl = new OptionData((OptionType.STRING),
                "url", setCustomRadioLinkCommand.getCommandDescription(), true);
        var setCustomRadioCommandOptionDataGenre = new OptionData((OptionType.STRING),
                GENRE_OPTION, "specify the genre you want to add.", true);
        commandData.add(Commands.slash(setCustomRadioLinkCommand.getCommandName(),
                setCustomRadioLinkCommand.getCommandDescription()).addOptions(
                        setCustomRadioCommandOptionDataUrl, setCustomRadioCommandOptionDataGenre));
        // /get-radio-urls
        commandData.add(Commands.slash(getCustomRadioLinkCommand.getCommandName(),
                getCustomRadioLinkCommand.getCommandDescription()));
        // /delete-genre
        var deleteByGenre = new OptionData((OptionType.STRING), GENRE_OPTION,
                "specify the genre you want to delete.", true);
        commandData.add(Commands.slash(deleteGenreCustomRadioLinkCommand.getCommandName(),
                deleteGenreCustomRadioLinkCommand.getCommandDescription()).addOptions(deleteByGenre));
        // /delete-all
        commandData.add(Commands.slash(deleteAllCustomRadioLinkCommand.getCommandName(),
                deleteAllCustomRadioLinkCommand.getCommandDescription()));
        return commandData;
    }

}
