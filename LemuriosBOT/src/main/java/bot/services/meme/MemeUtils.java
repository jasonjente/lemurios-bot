package bot.services.meme;

import bot.services.model.Meme;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class MemeUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(MemeUtils.class);

    private MemeUtils(){}
    public static void extractZipFile(MultipartFile file, File destinationFolder) throws IOException {
        LOGGER.info("extractZipFile() - ENTER");
        try (ZipInputStream zipInputStream = new ZipInputStream(file.getInputStream())) {
            doExtract(destinationFolder, zipInputStream);
        }
        LOGGER.info("extractZipFile() - LEAVE");
    }

    public static void extractZipFile(File file, File destinationFolder) throws IOException {
        LOGGER.info("extractZipFile() - ENTER");
        try (ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(file.toPath()))) {
            doExtract(destinationFolder, zipInputStream);
        }
        LOGGER.info("extractZipFile() - LEAVE");
    }

    private static void doExtract(File destinationFolder, ZipInputStream zipInputStream) throws IOException {
        byte[] buffer = new byte[1024];
        ZipEntry zipEntry;
        while ((zipEntry = zipInputStream.getNextEntry()) != null) {
            if (!zipEntry.isDirectory() && isSupportedImageFile(zipEntry.getName())) {
                File outputFile = new File(destinationFolder, zipEntry.getName());
                try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                    int length;
                    while ((length = zipInputStream.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, length);
                    }
                }
            }
            zipInputStream.closeEntry();
        }
    }

    public static List<Meme> createMemesFromFiles(File[] files) {
        LOGGER.info("createMemesFromFiles() - ENTER");
        List<Meme> memes = new ArrayList<>();
        for (File file : files) {
            if (file.isFile() && isSupportedImageFile(file.getName())) {
                Meme meme = new Meme();
                meme.setFilename(file.getName());
                try {
                    meme.setImageData(Files.readAllBytes(file.toPath()));
                } catch (IOException e) {
                    LOGGER.error("Error with file {} ", file.getName(), e);
                }
                meme.setCreatedOn(Timestamp.valueOf(LocalDateTime.now()));
                memes.add(meme);
            }
        }
        LOGGER.info("createMemesFromFiles() - LEAVE");
        return memes;
    }

    public static boolean isSupportedImageFile(String fileName) {
        String extension = FilenameUtils.getExtension(fileName).toLowerCase();
        return extension.equals("jpg") || extension.equals("jpeg") || extension.equals("png");
    }

}
