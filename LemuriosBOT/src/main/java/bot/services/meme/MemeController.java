package bot.services.meme;

import bot.services.model.Meme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static bot.services.meme.MemeUtils.createMemesFromFiles;
import static bot.services.meme.MemeUtils.extractZipFile;

@RestController
@RequestMapping("/meme")
public class MemeController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MemeController.class);

    @Autowired
    private MemeService memeService;

    @PostMapping("/post")
    public ResponseEntity<String> uploadMeme(@RequestParam("file") MultipartFile file, HttpServletRequest httpServletRequest) {
        String ipAddress = httpServletRequest.getRemoteAddr();
        LOGGER.info("uploadMeme() - ENTER - Request coming from: {}", ipAddress);

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is required");
        }

        try {
            // Create a temporary folder to extract the ZIP file
            File tempFolder = Files.createTempDirectory("meme-upload").toFile();
            extractZipFile(file, tempFolder);
            List<Meme> memes = createMemesFromFiles(tempFolder.listFiles());

            memeService.storeMemes(memes);

            //I opted for this in order to free up space since they have been persisted in the database.
            tempFolder.deleteOnExit();
            return ResponseEntity.ok("Meme upload started");
        } catch (IOException e) {
            LOGGER.error("Error processing zip file: ", e);
            //for the lols
            return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).body("Apparently I cannot brew coffee ... :/");
        }
    }


}
