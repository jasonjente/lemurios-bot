package bot.image.processing.builder;

import bot.image.conversion.ImageTypeConverter;
import bot.image.detection.EdgeDetector;
import bot.exceptions.ImageProcessingException;
import bot.image.processing.ImageProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Base Class for Filters and Image Functionalities
 * - Upscale
 * - Downscale
 * - Black and White
 * - Sepia
 * - Gaussian Blur
 * - Noise Reduction
 * - Median Filter
 * - Bilateral Filter
 * - Convert to Black and white (can be used to convert from BnW WnB)
 * - Dilate (BnW) Image
 * - Erode Image
 * - Import File as BufferedImage
 * - Export BufferedImage as File
 */
public class ImageProcessorBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageProcessorBuilder.class);
    private BufferedImage image;


    public ImageProcessorBuilder(BufferedImage image) {
        this.image = image;
    }

    public ImageProcessorBuilder(String filename) throws IOException, ImageProcessingException {
        this.image = ImageProcessor.importImageFile(filename);
    }
/**
 * Singleton Example:
    private static ImageProcessorBuilder instance = null;
    public static ImageProcessorBuilder getInstance(String filename) throws IOException {
        if (instance == null) {
            instance = new ImageProcessorBuilder(filename);
        }
        return instance;
    }
    private ImageProcessorBuilder(String filename) throws IOException {
        this.image = ImageProcessor.importImageFile(filename);
    }*/

    public ImageProcessorBuilder upscale(int scaleFactor) {
         image = ImageProcessor.upscaleImage(image,scaleFactor); 
        LOGGER.info("Process for upscale is completed." );
        return this;
    }

    public ImageProcessorBuilder downscale(int scaleFactor) {
         image = ImageProcessor.downscaleImage(image,scaleFactor); 
        LOGGER.info("Process for downscale is completed." );
        return this;
    }

    public ImageProcessorBuilder convertToGreyscale() {
         image = ImageProcessor.convertToBlackAndWhite(image); 
        LOGGER.info("Process for convertToGreyscale is completed." );
        return this;
    }

    public ImageProcessorBuilder convertToSepia(){
         image = ImageProcessor.convertToSepia(image); 
        LOGGER.info("Process for convertToSepia is completed." );
        return this;
    }

    public ImageProcessorBuilder applyGaussianBlur(){
         image = ImageProcessor.applyGaussianBlur(image); 
        LOGGER.info("Process for applyGaussianBlur is completed." );
        return this;
    }

    public ImageProcessorBuilder applyNoiseReduction(){
         image = ImageProcessor.reduceNoise(image); 
        LOGGER.info("Process for applyNoiseReduction is completed." );
        return this;
    }

    public ImageProcessorBuilder applyMedianFilter(){
         image = ImageProcessor.applyMedianFilter(image); 
        LOGGER.info("Process for applyMedianFilter is completed." );
        return this;
    }

    public ImageProcessorBuilder equalizeImage(){
         image = ImageProcessor.equalizeImage(image); 
        LOGGER.info("Process for equalizeImage is completed." );
        return this;
    }

    public ImageProcessorBuilder applyBilateralFilter(){
         image = ImageProcessor.applyBilateralFilter(image,20, 10); 
        LOGGER.info("Process for applyBilateralFilter is completed." );
        return this;
    }

    public ImageProcessorBuilder normalizeImage() {
         try {
            image = ImageProcessor.normalizeImage(image);
        } catch (InterruptedException e) {
             LOGGER.error("Error ", e);
             Thread.currentThread().interrupt();
         }
        LOGGER.info("Process for currentTimeMillis is completed." );
        return this;
    }

    public ImageProcessorBuilder detectEdges(){
         image = EdgeDetector.detectEdges(image);
        LOGGER.info("Process to detect edges is completed." );
        return this;
    }

    public ImageProcessorBuilder applyImageSegmentation(){
         image = ImageProcessor.segmentImage(image); 
        LOGGER.info("Process to apply Image Segmentation is completed." );
        return this;
    }

    public ImageProcessorBuilder applyErodeFilter(){
         image = ImageProcessor.erode(image); 
        LOGGER.info("Process to apply Image Erosion is completed." );
        return this;
    }

    public ImageProcessorBuilder combineEdges (BufferedImage edges) throws ImageProcessingException {
        try {
            image = ImageProcessor.combineEdges(image, edges);
        } catch (ImageProcessingException e) {
            throw new ImageProcessingException("Error processing " + e.getMessage(), e);
        }
        LOGGER.info("Process to apply Image Edge Combination is completed." );
        return this;
    }

    public ImageProcessorBuilder applyDilateEffect(){
         image = ImageProcessor.dilate(image); 
        LOGGER.info("Process to apply Image dilation effect is completed." );
        return this;
    }

    public ImageProcessorBuilder blendPixels(){
         image = ImageProcessor.blendPixels(image); 
        LOGGER.info("Process to apply pixel blending is completed." );
        return this;
    }

    public ImageProcessorBuilder applyBinaryBlackAndWhite() {
         try {
            image = ImageProcessor.blackAndWhiteAndReverse(image);
        } catch (InterruptedException e) {
            LOGGER.error("error: ", e);
             Thread.currentThread().interrupt();        }
        LOGGER.info("Process to applyBinaryBlackAndWhite is completed." );
        return this;
    }

    /**
     *  I/O Methods
     */

    public ImageProcessorBuilder importImage(String filename) throws IOException, ImageProcessingException {
         this.image = ImageProcessor.importImageFile(filename); 
        LOGGER.info("Process for importImage is completed." );
        return this;
    }

    public ImageProcessorBuilder exportImage(String filename){
         ImageProcessor.exportImageFile(this.image, filename); 
        LOGGER.info("Process for exportImage is completed." );
        return this;
    }

    /**
     * Image Conversion Methods
     */

    public ImageProcessorBuilder convertJpegImageToPNG(String filename) throws IOException {
         image = ImageTypeConverter.convertJpegToPNG(filename);
        LOGGER.info("Process for convertJpegImageToPNG is completed." );
        return this;
    }

    /**
     * Getters/Setters
     */
    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public BufferedImage getImage(){
        return image;
    }

}
