package bot.image.conversion;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageTypeConverter {
    private ImageTypeConverter() {
    }

    public static BufferedImage convertJpegToPNG(String filename) throws IOException {
        // Load the JPEG file
        File inputFile = new File(filename);

        if(inputFile.exists()) {

            String filenameAdjusted ="";
            if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")){
                String suffix = filename.endsWith(".jpg") ? ".jpg" : ".jpeg";
                filenameAdjusted = filename.replace(suffix, ".png");
            } else {
                filenameAdjusted = filename;
            }

            BufferedImage inputImage = ImageIO.read(inputFile);
            // Create the output PNG file
            File outputFile = new File(filenameAdjusted);

            // Convert the image to PNG and save to file
            ImageIO.write(inputImage, "png", outputFile);
            return inputImage;
        }else {
            throw new IOException("File not found.");
        }
    }

}
