package bot.commands.concrete.images;

import bot.commands.Command;
import bot.exceptions.ImageProcessingException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.FileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static bot.constants.Constants.DATA_IN_DIR;
import static bot.constants.Constants.SORRY_MSG;


/**
 * Gets a random meme in File format, then it converted to a buffered image, passed into a buffered stream and
 * then converted into an Input Stream.
 * The event parameter contains relevant information like the author and the chat endpoints.
 */
@Service
public class MemeCommand extends Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(MemeCommand.class);
    private static final Set<String> unusedMemeMap = new HashSet<>();
    private static final Set<String> memeMap = new HashSet<>();
    private final Random random = new Random();  // SecureRandom is preferred to Random

    @PostConstruct
    private void init(){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            File directory = new File(DATA_IN_DIR.getValue());
            File[] files = directory.listFiles();
            LOGGER.info("Initializing meme map!");
            assert files != null;
            for(File meme : files) {
                LOGGER.info("Indexed file {}!", meme.getName());
                unusedMemeMap.add(meme.getName());
                memeMap.add(meme.getName());
            }
            if (memeMap.isEmpty()) {
                LOGGER.warn("EMPTY Meme MAP!");
            } else {
                LOGGER.info("Total memes available: {}", memeMap.size());
            }
        });
        executor.shutdown();
    }


    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String sender = event.getUser().getAsTag();
        createHistoryEntry(event);
        LOGGER.info("{} has requested the meme command.", sender);
        EmbedBuilder embedBuilder = new EmbedBuilder().setImage("attachment://meme.png") ;// we specify this in sendFile as "meme.png"

        MemeResult meme;
        try {
            meme = getRandomMeme();
            if(meme!=null) {
                LOGGER.info("{} will have the {} meme.", sender, meme.getFile().getName());
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                BufferedImage bufferedImageMeme = ImageIO.read(meme.getFile());

                ImageIO.write(bufferedImageMeme, "png", os);
                InputStream ret = new ByteArrayInputStream(os.toByteArray());
                event.getChannel().sendFiles(FileUpload.fromData(ret, "meme.png")).setEmbeds(embedBuilder.build()).queue();
                if(meme.isSent()){
                    embedBuilder.addField("Looks like I've sent you this meme already!","This meme is not new :( ", true);
                }
            }else {
                embedBuilder.addField(SORRY_MSG.getValue(), "Looks like there was an error while processing your request :(", true);
            }
        } catch (IOException e) {
            LOGGER.error("Error: ", e);
            embedBuilder.addField(SORRY_MSG.getValue(), "Looks like there was an error while processing your request :(", true);
        } catch (ImageProcessingException e) {
            LOGGER.error("Error: ", e);
            embedBuilder.addField(SORRY_MSG.getValue(), "Looks like we have run out of new memes :(", true);
        }
        LOGGER.info("Total memes available: {}", memeMap.size());
        LOGGER.info("Total unusedMemeMap size : {}", unusedMemeMap.size());

        embedBuilder.setDescription("Your meme kind of person.");
        event.getInteraction().getHook().editOriginalEmbeds(embedBuilder.build()).queue();
    }

    /**
     * During the start of the BOTs runtime, a set of filenames is created which contains the filenames of the images dir.
     * Then checks the /images directory in classpath. If it finds it then gets the random file,
     * removing its file name from the unused set. If the unused set is emptied, the image is changed to one already used.
     * if no image exists an exception is thrown.
     * @returns the File Wrapper with extra information such as if this is the first occurrence of the meme.
     */
    public MemeResult getRandomMeme() throws ImageProcessingException {
        File directory = new File(DATA_IN_DIR.getValue());
        if(!directory.exists()){
            throw new ImageProcessingException("Directory " + DATA_IN_DIR + " doesn't exist");
        }
        if(!unusedMemeMap.isEmpty()) {
            Optional<String> filenameOpt = unusedMemeMap.stream().skip(random.nextInt(unusedMemeMap.size())).findFirst();
            if (filenameOpt.isPresent()) {
                String filename = filenameOpt.get();
                File meme = new File(directory, filename);
                if (!meme.exists()) {
                    throw new ImageProcessingException("Input file " + filename + " doesn't exist.");
                }
                unusedMemeMap.remove(filename);
                return new MemeResult(false, meme, filename);
            }
        }else if(!memeMap.isEmpty()){
            LOGGER.warn("Unused meme map is empty.");
            Optional<String> filenameOpt = memeMap.stream().skip(random.nextInt(memeMap.size())).findFirst();

            if(!filenameOpt.isPresent()){
                throw new ImageProcessingException("Error in Meme Map size: " + memeMap.size() + " unused meme map size: " + unusedMemeMap);

            }
            return new MemeResult(true, new File(directory, filenameOpt.get()), filenameOpt.get());
        }
        throw new ImageProcessingException("It appears that the size of the meme map is empty: " + memeMap.size());
    }
}
