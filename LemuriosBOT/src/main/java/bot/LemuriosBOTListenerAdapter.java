package bot;

import bot.commands.Command;
import bot.commands.chat.*;
import bot.commands.music.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static bot.application.constants.Commands.*;
import static net.dv8tion.jda.api.interactions.commands.build.Commands.slash;

@Component
@RequiredArgsConstructor
@Slf4j
public class LemuriosBOTListenerAdapter extends ListenerAdapter {
    private final AssemblemursCommand assemblemursCommand;
    private final TakenNamesCommand takenNamesCommand;
    private final CreditsCommand creditsCommand;
    private final HelpCommand helpCommand;
    private final PlayCommand playCommand;
    private final StopCommand stopCommand;
    private final PauseCommand pauseCommand;
    private final SkipCommand skipCommand;
    private final ResumeCommand resumeCommand;
    private final NowPlayingCommand nowPlayingCommand;
    private final JoinCommand joinCommand;
    private final DisconnectCommand disconnectCommand;
    private final CreateInviteCommand createInviteCommand;

    @Getter
    private static final Map<String, Command> commands = new HashMap<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(4);

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

        /*
            Chat commands
         */
        commands.put(assemblemursCommand.getCommandName(), assemblemursCommand);
        commands.put(createInviteCommand.getCommandName(), createInviteCommand);
        commands.put(creditsCommand.getCommandName(), creditsCommand);
        commands.put(helpCommand.getCommandName(), helpCommand);
        commands.put(takenNamesCommand.getCommandName(), takenNamesCommand);

        /*
            Music commands
         */
        commands.put(playCommand.getCommandName(), playCommand);
        commands.put(pauseCommand.getCommandName(), pauseCommand);
        commands.put(skipCommand.getCommandName(), skipCommand);
        commands.put(stopCommand.getCommandName(), stopCommand);
        commands.put(joinCommand.getCommandName(), joinCommand);
        commands.put(nowPlayingCommand.getCommandName(), nowPlayingCommand);
        commands.put(resumeCommand.getCommandName(), resumeCommand);
        commands.put(disconnectCommand.getCommandName(), disconnectCommand);
    }

    /**
     * Invoked on the shutdown of the application. Makes sure that futures that are still pending are getting canceled
     * ASAP to avoid memory leaks and shuts down the executor to avoid the spawning of new threads.
     */
    @PreDestroy
    private void onDestroy() {
        log.info("Shutting down listener!");
        for (Future<?> runnableFuture : futuresSet) {
            boolean canceled = runnableFuture.cancel(true);
            log.info("Command terminated: {}, is done: {} , is canceled: {}.",
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
        log.info("Message received from {} - Content: {} - ENTER",
                event.getInteraction().getUser().getName(), event.getFullCommandName());
        futuresSet.add(executor.submit(getRunnable(event)));
        log.info("Message received from {} - Content: {} - LEAVE",
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
                log.error("ERROR:", runtimeException);
            }
        };
    }

    /**
     * Creates the commands, their input parameters if they have any and their relevant information, that will be published to discord
     *
     * @return a list of all the Discord CommonData which hold the information about commands.
     */
    private List<CommandData> setupCommandOptions() {
        var commandData = new ArrayList<CommandData>();

        /*
            Chat Commands
         */

        // /assemblemurs
        var assemblemursOptions = new OptionData(OptionType.STRING,
                "game", "(optional) Specify what game you want to play!", false);
        commandData.add(slash(ASSEMLEMURS_COMMAND.getCommandName(),
                "Pings all Lemurioi Role Members -- Can be used if and only if you belong to that group.")
                .addOptions(assemblemursOptions));
        // /taken-names
        commandData.add(slash(TAKEN_NAMES.getCommandName(),
                "Prints out all taken Lemur names."));
        // /credits
        commandData.add(slash(CREDITS_COMMAND.getCommandName(),
                "Prints out the application's credits."));
        // /help
        commandData.add(slash(HELP_COMMAND.getCommandName(),
                "Prints all the available commands."));

        /*
            Meme Commands
        */
        // /meme
        commandData.add(slash(MEME_COMMAND.getCommandName(),
                "The bot will return with a random meme."));
        // /upload
        var optionDataMeme = new OptionData(OptionType.ATTACHMENT,
                "meme-image", "Upload a meme to the BOT", true);
        commandData.add(slash(UPLOAD_MEME_COMMAND.getCommandName(),
                "Upload a meme to the Bot.").addOptions(optionDataMeme));

        /*
            Music Commands
         */

        // /play :url
        var optionDataSongToPlay = new OptionData(OptionType.STRING, "search",
                "Enter the title you are looking for or a URL", true);
        commandData.add(slash(PLAY_COMMAND.getCommandName(),
                "Search and play a song via YouTube or from a discord CDN link.")
                .addOptions(optionDataSongToPlay));

        // /skip
        commandData.add(
                slash(SKIP_COMMAND.getCommandName(), "Skips current song from the song list."));
        // /pause
        commandData.add(
                slash(PAUSE_COMMAND.getCommandName(), "Pauses current song from the song list."));
        // /resume
        commandData.add(
                slash(STOP_COMMAND.getCommandName(), "Stops execution and empties the song list."));
        // /join
        commandData.add(
                slash(JOIN_COMMAND.getCommandName(), "Bot joins the voice channel the caller is in."));
        // /now-playing
        commandData.add(slash(NOW_PLAYING.getCommandName(), "Bot prints the song it is currently playing."));
        // /resume
        commandData.add(slash(RESUME_COMMAND.getCommandName(), "Bot unpauses the song it paused."));
        // /disconnect
        commandData.add(slash(DISCONNECT_COMMAND.getCommandName(), "Bot disconnects and empties the queue."));

        return commandData;
    }

}
