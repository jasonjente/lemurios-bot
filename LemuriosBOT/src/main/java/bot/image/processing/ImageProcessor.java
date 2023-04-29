package bot.image.processing;

import bot.exceptions.ImageProcessingException;
import bot.image.model.BasicColorCodes;
import bot.image.processing.task.AverageBrightnessTask;
import bot.image.processing.task.EqualizeChunkTask;
import bot.image.processing.task.NormalizeChunkTask;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.BitSet;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static bot.constants.Constants.IMAGE_DETECTION_IMAGE_IN_DIR;
import static bot.constants.Constants.IMAGE_DETECTION_IMAGE_OUT_DIR;

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
public class ImageProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageProcessor.class);

    private static final double RADIUS = 0.05;
    private static final int NUMBER_OF_CHUNKS = 256;


    private ImageProcessor(){
        //Utility classes should not be instantiated. All methods are static.
        throw new IllegalStateException("Utility class");
    }

    /**
     *
     * @param upscaleFactor is used to multiply the dimensions of the array, i.e. in a 1024x1024 pixel image,
     * a scale factor of 2 will make the new dimensions 2048x2048.
     * @throws IOException
     */
    public static BufferedImage upscaleImage(BufferedImage image, double upscaleFactor){
        LOGGER.info("upscaleImage - upscale factor: {}, image width: {}, image height: {}.", upscaleFactor, image.getWidth(), image.getHeight());
        int inputWidth = image.getWidth();
        int inputHeight = image.getHeight();
        int outputWidth = (int) (inputWidth * upscaleFactor);
        int outputHeight = (int) (inputHeight * upscaleFactor);

        BufferedImage outputImage = new BufferedImage(outputWidth, outputHeight, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < inputHeight; y++) {
            for (int x = 0; x < inputWidth; x++) {
                int color = image.getRGB(x, y);
                for (int j = 0; j < upscaleFactor; j++) {
                    for (int i = 0; i < upscaleFactor; i++) {
                        int newX = (int) (x * upscaleFactor + i);
                        int newY = (int) (y * upscaleFactor + j);
                        outputImage.setRGB(newX, newY, color);
                    }
                }
            }
        }
        LOGGER.info("upscaleImage - initial width: {}, final width: {}," +
                "\ninitial height: {}, final height: {}.",image.getWidth(), outputImage.getWidth(), image.getHeight(), outputImage.getHeight());
        return outputImage;
    }

    /**
     * Similar fashion with the upscale method above, i.e. in a 1024x1024 pixel image,
     * a scale factor of 2 will make the new dimensions equal to 512x512.
     * @param scaleFactor
     * @throws IOException
     */
    public static BufferedImage downscaleImage(BufferedImage image, int scaleFactor) {
        LOGGER.info("downscaleImage - downscale factor: {}, image width: {}, image height: {}.", scaleFactor, image.getWidth(), image.getHeight());

        int scaledWidth = image.getWidth() / scaleFactor;
        int scaledHeight = image.getHeight() / scaleFactor;
        BufferedImage outputImage = new BufferedImage(scaledWidth, scaledHeight, image.getType());

        for (int y = 0; y < scaledHeight; y++) {
            for (int x = 0; x < scaledWidth; x++) {
                Color outputColor = getMeanColor(x, y, image, scaleFactor);
                outputImage.setRGB(x, y, outputColor.getRGB());
            }
        }
        LOGGER.info("downscaleImage - initial width: {}, final width: {}," +
                "\ninitial height: {}, final height: {}.",image.getWidth(), outputImage.getWidth(), image.getHeight(), outputImage.getHeight());
        return outputImage;
    }

    private static Color getMeanColor(int x, int y, BufferedImage inputImage, int scaleFactor) {
        int startX = x * scaleFactor;
        int startY = y * scaleFactor;
        int endX = startX + scaleFactor;
        int endY = startY + scaleFactor;
        int totalRed = 0;
        int totalGreen = 0;
        int totalBlue = 0;

        for (int innerY = startY; innerY < endY; innerY++) {
            for (int innerX = startX; innerX < endX; innerX++) {
                Color pixelColor = new Color(inputImage.getRGB(innerX, innerY));
                totalRed += pixelColor.getRed();
                totalGreen += pixelColor.getGreen();
                totalBlue += pixelColor.getBlue();
            }
        }

        int numPixels = scaleFactor * scaleFactor;
        int avgRed = totalRed / numPixels;
        int avgGreen = totalGreen / numPixels;
        int avgBlue = totalBlue / numPixels;
        return new Color(avgRed, avgGreen, avgBlue);
    }

    public static BufferedImage applyGaussianBlur(BufferedImage image) {
        LOGGER.info("applyGaussianBlur() - enter.");
        int size = (int) Math.ceil(RADIUS * 3) * 2 + 1;
        float[] kernelValues = calculateGaussianKernel(size, RADIUS);
        Kernel kernel = new Kernel(size, size, kernelValues);
        ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        LOGGER.info("applyGaussianBlur() - leave.");
        return op.filter(image, null);
    }

    /**
     *
     * @param size
     * @param radius -> Sigma, denoted as σ, is a parameter used in the Gaussian distribution to control the width
     *              of the distribution curve. In the context of Gaussian blur, it determines the amount of blur to be
     *              applied to the image. A larger sigma value produces a wider kernel and therefore more blur, while a
     *               smaller sigma value produces a narrower kernel and therefore less blur.
     * @return
     */
    private static float[] calculateGaussianKernel(int size, double radius) {
        LOGGER.info("calculateGaussianKernel() - enter.");
        float[] kernel = new float[size * size];
        double sigma = radius / 3.0;
        double sigmaSquared = sigma * sigma;
        double twoSigmaSquared = 2.0 * sigmaSquared;
        double sqrtTwoPiSigmaSquared = Math.sqrt(2 * Math.PI) * sigmaSquared;
        double total = 0;
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                double distanceSquared = (double) (x - size / 2) * (double) (x - size / 2) + (double) (y - size / 2) * (double) (y - size / 2);
                double value = Math.exp(-distanceSquared / twoSigmaSquared) / sqrtTwoPiSigmaSquared;
                kernel[y * size + x] = (float) value;
                total += value;
            }
        }
        for (int i = 0; i < kernel.length; i++) {
            if(total != 0) {
                kernel[i] /= total;
            }else {
                kernel[i] = 0.1f;
            }
        }
        LOGGER.info("calculateGaussianKernel() - leave.");

        return kernel;
    }
    public static BufferedImage convertToBlackAndWhite(final BufferedImage inputImage) {
        LOGGER.info("convertToBlackAndWhite() - enter.");
        int width = inputImage.getWidth();
        int height = inputImage.getHeight();

        BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = inputImage.getRGB(x, y);
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;
                int gray = (r + g + b) / 3;
                int grayValue = (gray << 16) + (gray << 8) + gray;
                outputImage.setRGB(x, y, grayValue);
            }
        }
        LOGGER.info("convertToBlackAndWhite() - leave.");
        return outputImage;
    }

    public static BufferedImage reduceNoise(final BufferedImage image) {
        LOGGER.info("reduceNoise() - enter.");
        // apply a Gaussian filter to the image
        float[] kernelData = {0.0625f, 0.125f, 0.0625f,
                0.125f, 0.25f, 0.125f,
                0.0625f, 0.125f, 0.0625f};

        Kernel kernel = new Kernel(3, 3, kernelData);
        ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        BufferedImage filteredImage = op.filter(image, null);

        LOGGER.info("reduceNoise() - leave.");
        return filteredImage;
    }

    /**
     * Creates a sepia filter and applies it to the image.
     * @throws IOException
     */
    public static BufferedImage convertToSepia(final BufferedImage image) {
        LOGGER.info("convertToSepia() - enter.");

        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        // Loop through each pixel in the image
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Get the RGB values of the pixel
                int rgb = image.getRGB(x, y);
                int originalRed = (rgb >> 16) & 0xff;
                int originalGreen = (rgb >> 8) & 0xff;
                int originalBlue = rgb & 0xff;

                // Compute the new RGB values using the sepia formula
                int newRed = (int)(0.393 * originalRed + 0.769 * originalGreen + 0.189 * originalBlue);
                int newGreen = (int)(0.349 * originalRed + 0.686 * originalGreen + 0.168 * originalBlue);
                int newBlue = (int)(0.272 * originalRed + 0.534 * originalGreen + 0.131 * originalBlue);

                // Clamp the new RGB values to the range 0-255
                newRed = Math.min(255, Math.max(0, newRed));
                newGreen = Math.min(255, Math.max(0, newGreen));
                newBlue = Math.min(255, Math.max(0, newBlue));

                // Set the RGB values of the pixel to the new values
                int newRgb = (newRed << 16) | (newGreen << 8) | newBlue;
                outputImage.setRGB(x, y, newRgb);
            }
        }
        LOGGER.info("convertToSepia() - leave.");
        return outputImage;
    }

    /**
     * Creates a sample image with lines 4 alternating red lines followed by 4 rows of blue
     */
    public static BufferedImage createImage(int width, int height){
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < image.getHeight(); y++) {
            // Alternate color every 4 rows
            int color = (y / 8) % 2 == 0 ? BasicColorCodes.RED.getCode() : BasicColorCodes.BLUE.getCode();

            // Set row pixels to the alternating color
            for (int x = 0; x < image.getHeight(); x++) {
                image.setRGB(x, y, color);
            }
        }
        return image;
    }


    public static BufferedImage equalizeImage(BufferedImage image) {
        LOGGER.info("equalizeImage() - leave.");

        int chunkHeight = image.getHeight() / NUMBER_OF_CHUNKS; // height of each chunk
        int remainder = image.getHeight() % NUMBER_OF_CHUNKS; // remainder to add to the last chunk

        ExecutorService executor = Executors.newFixedThreadPool(NUMBER_OF_CHUNKS);

        for (int i = 0; i < NUMBER_OF_CHUNKS; i++) {
            int startY = i * chunkHeight;
            int endY = (i == NUMBER_OF_CHUNKS - 1) ? startY + chunkHeight + remainder : startY + chunkHeight;
            BufferedImage chunk = image.getSubimage(0, startY, image.getWidth(), endY - startY);

            executor.execute(new EqualizeChunkTask(chunk));
        }

        executor.shutdown();

        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.error("Error: ",e);
        }
        LOGGER.info("equalizeImage() - leave.");
        return image;
    }

    /**
     * Performs and applies Median filter on the provided image
     * @param image
     * @return
     */
    public static BufferedImage applyMedianFilter(BufferedImage image) {
        LOGGER.info("applyMedianFilter() - enter.");

        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);


        // apply median filter to each pixel and each color channel
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                // get the 3x3 kernel for each color channel
                int[] kernelR = new int[9];
                int[] kernelG = new int[9];
                int[] kernelB = new int[9];
                int index = 0;
                for (int j = -1; j <= 1; j++) {
                    for (int i = -1; i <= 1; i++) {
                        int rgb = image.getRGB(x + i, y + j);
                        kernelR[index] = (rgb >> 16) & 0xFF;
                        kernelG[index] = (rgb >> 8) & 0xFF;
                        kernelB[index] = rgb & 0xFF;
                        index++;
                    }
                }

                // sort the kernel for each color channel
                Arrays.sort(kernelR);
                Arrays.sort(kernelG);
                Arrays.sort(kernelB);

                // get the median value for each color channel
                int medianR = kernelR[4];
                int medianG = kernelG[4];
                int medianB = kernelB[4];

                // set the result pixel to the median value for each color channel
                int rgb = (medianR << 16) | (medianG << 8) | medianB;
                result.setRGB(x,y,rgb);
            }
        }
        LOGGER.info("applyMedianFilter() - leave.");
        return result;
    }

    /**
     * Splits the image into chunks and assigns each chunk to a thread for processing that chunk
     * @param image
     * @return
     */
    public static BufferedImage normalizeImage(final BufferedImage image) throws InterruptedException {
        LOGGER.info("normalizeImage() - enter.");

        double averageImageBrightness = calculateAverageImageBrightness(image);
        //For small images, many threads cause error, the chunks (threads) need to be of size > 1
        int chunks = NUMBER_OF_CHUNKS;
        while(chunks > image.getHeight()+1){
            chunks /= 2;
        }
        int chunkHeight = image.getHeight() / chunks;
        int remainder = image.getHeight() % chunks;



        ExecutorService executor = Executors.newFixedThreadPool(chunks);

        for (int i = 0; i < chunks; i++) {
            int startY = i * chunkHeight;
            int endY = (i == chunks - 1) ? startY + chunkHeight + remainder : startY + chunkHeight;
            BufferedImage chunk = image.getSubimage(0, startY, image.getWidth(), endY - startY);

            executor.execute(new NormalizeChunkTask(chunk, averageImageBrightness));
        }

        executor.shutdown();

        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.error("error", e);
        }

        return image;
    }

    public static double calculateAverageImageBrightness(BufferedImage image) throws InterruptedException {
        int width = image.getWidth();
        int height = image.getHeight();
        int numPixels = width * height;
        int numberOfThreads = NUMBER_OF_CHUNKS/64;
        // Calculate the number of pixels to process per thread
        int pixelsPerThread = numPixels / numberOfThreads;

        // Create an array of threads
        AverageBrightnessTask[] threads = new AverageBrightnessTask[numberOfThreads];

        // Initialize the threads
        int startIndex = 0;
        for (int i = 0; i < numberOfThreads; i++) {
            int endIndex = startIndex + pixelsPerThread;
            if (i == numberOfThreads - 1) {
                // Last thread processes the remaining pixels
                endIndex = numPixels;
            }
            threads[i] = new AverageBrightnessTask(image, startIndex, endIndex);
            startIndex = endIndex;
        }

        // Start the threads
        for (AverageBrightnessTask thread : threads) {
            thread.start();
        }

        // Wait for the threads to finish and collect the results
        double totalBrightness = 0.0;
        for (AverageBrightnessTask thread : threads) {
            thread.join();
            totalBrightness += thread.getThreadBrightness();
        }

        // return the average brightness
        return totalBrightness / numPixels;



    }

    /**
     * creates and applies the bilateral filter. starting values for spatial sigma: 20, intensity sigma: 10
     * @param image
     * @param spatialSigma
     * @param intensitySigma
     * @return
     */
    public static BufferedImage applyBilateralFilter(BufferedImage image, double spatialSigma, double intensitySigma) {
        LOGGER.info("applyBilateralFilter() - enter.");

        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage result = new BufferedImage(width, height, image.getType());

        int chunkSize = 100; // number of rows in each chunk

        IntStream.range(0, height) // create a stream of row indices
                .boxed() // convert primitive int stream to boxed stream
                .collect(Collectors.groupingByConcurrent(row -> row / chunkSize)) // group rows into chunks
                .values().parallelStream() // process chunks in parallel
                .forEach(chunk -> {
                    for (int y : chunk) {
                        for (int x = 0; x < width; x++) {
                            int rgb = applyBilateralFilterPixel(image, x, y, spatialSigma, intensitySigma);
                            result.setRGB(x, y, rgb);
                        }
                    }
                });

        LOGGER.info("applyBilateralFilter() - leave.");
        return result;
    }

    private static int applyBilateralFilterPixel(BufferedImage image, int x, int y, double spatialSigma, double intensitySigma) {
        double sumR = 0;
        int sumG = 0;
        int sumB = 0;
        double sumWeight = 0;

        int centerRgb = image.getRGB(x, y);
        int centerR = (centerRgb >> 16) & 0xFF;
        int centerG = (centerRgb >> 8) & 0xFF;
        int centerB = centerRgb & 0xFF;

        for (int j = -1; j <= 1; j++) {
            for (int i = -1; i <= 1; i++) {
                int neighborX = x + i;
                int neighborY = y + j;

                if (neighborX >= 0 && neighborX < image.getWidth() && neighborY >= 0 && neighborY < image.getHeight()) {
                    int neighborRgb = image.getRGB(neighborX, neighborY);
                    int neighborR = (neighborRgb >> 16) & 0xFF;
                    int neighborG = (neighborRgb >> 8) & 0xFF;
                    int neighborB = neighborRgb & 0xFF;

                    double spatialDistance = Math.sqrt((double) (i * i) + j * j);
                    double intensityDistance = Math.sqrt((centerR - neighborR) * (centerR - neighborR) +
                            (centerG - neighborG) * (centerG - neighborG) +
                            (centerB - neighborB) * ((double)(centerB - neighborB)));

                    double weight = Math.exp(-spatialDistance / (2 * spatialSigma * spatialSigma)) *
                            Math.exp(-intensityDistance / (2 * intensitySigma * intensitySigma));

                    sumR += neighborR * weight;
                    sumG += neighborG * weight;
                    sumB += neighborB * weight;
                    sumWeight += weight;
                }
            }
        }
        if(sumWeight == 0) {
            sumWeight = 0.01;
        }
        int filteredR = (int) Math.round(sumR / sumWeight);
        int filteredG = (int) Math.round(sumG / sumWeight);
        int filteredB = (int) Math.round(sumB / sumWeight);

        return (filteredR << 16) | (filteredG << 8) | filteredB | 0xFF000000;

    }

    public static BufferedImage segmentImage(BufferedImage image) {
        LOGGER.info("segmentImage() - enter.");
        int threshold = 64;
        // Convert image to grayscale
        BufferedImage grayscale = ImageProcessor.convertToBlackAndWhite(image);

        // Apply threshold filter
        short[] thresholdTable = new short[256];
        for (int i = 0; i < thresholdTable.length; i++) {
            if (i < threshold) {
                thresholdTable[i] = 0;
            } else {
                thresholdTable[i] = 255;
            }
        }
        BufferedImageOp thresholdFilter = new LookupOp(new ShortLookupTable(0, thresholdTable), null);
        BufferedImage thresholded = thresholdFilter.filter(grayscale, null);
        // Convert image back to RGB
        BufferedImage segmented = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        ColorConvertOp convert = new ColorConvertOp(null);
        segmented = convert.filter(thresholded, segmented);

        LOGGER.info("segmentImage() - leave");
        return segmented;
    }

    public static BufferedImage blackAndWhiteAndReverse(BufferedImage image) throws InterruptedException {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage bwImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        double threshold = ImageProcessor.calculateAverageImageBrightness(image); // The threshold value for deciding whether a pixel is black or white

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                int gray = (r + g + b) / 3; // Calculate the grayscale value of the pixel
                if (gray < threshold) {
                    bwImage.setRGB(x, y, Color.WHITE.getRGB());
                } else {
                    bwImage.setRGB(x, y, Color.BLACK.getRGB());
                }
            }
        }

        return bwImage;
    }

    public static BufferedImage combineEdges(final BufferedImage source, BufferedImage edges) throws ImageProcessingException {
        if ((source.getWidth() != edges.getWidth()) || source.getHeight() != edges.getHeight()) {
            throw new ImageProcessingException();
        }
        BufferedImage result = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < source.getHeight(); y++) {
            for (int x = 0; x < source.getWidth(); x++) {
                // Get the color of the pixel in the source image
                Color srcColor = new Color(source.getRGB(x, y), true);
                Color edgeColor = new Color(edges.getRGB(x, y), true);
                //Overlays black pixels over source. (edge detected image with white background)
                if (edgeColor.getRed() == 0 && edgeColor.getGreen() == 0 && edgeColor.getBlue() == 0) {
                    result.setRGB(x, y, Color.BLACK.getRGB());
                } else {
                    result.setRGB(x,y, srcColor.getRGB());
                }
            }
        }
        return result;
    }

    public static BufferedImage erode(BufferedImage image) {
        LOGGER.info("erode() - ENTER");
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);

        int[][] structuringElement = {{0, 0, 1, 0, 0},{0, 1, 2, 1, 0}, {1, 1, 1, 1, 1}, {0, 1, 2, 1, 0}, {0, 0, 1, 0, 0}}; // 5x5 structuring element

        // perform erosion on each pixel
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                boolean allOnes = true;
                for (int j = -1; j <= 1; j++) {
                    for (int i = -1; i <= 1; i++) {
                        if (y + j < 0 || y + j >= height || x + i < 0 || x + i >= width) {
                            // skip pixels outside the image boundary
                            continue;
                        }
                        int pixel = image.getRGB(x + i, y + j);
                        if (structuringElement[j + 1][i + 1] == 1 && pixel != 0xFFFFFFFF) {
                            // if structuring element has a 1 and corresponding pixel is not white, set center pixel to 0
                            allOnes = false;
                            break;
                        }
                    }
                    if (!allOnes) {
                        break;
                    }
                }
                result.setRGB(x, y, allOnes ? 0xFFFFFFFF : 0xFF000000);
            }
        }

        return result;
    }


//    public static BufferedImage dilateNonOptimized(BufferedImage image) {
//        int width = image.getWidth();
//        int height = image.getHeight();
//        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
//
//        int[][] structuringElement = {{0, 0, 1, 0, 0},{0, 1, 2, 1, 0}, {1, 1, 1, 1, 1}, {0, 1, 2, 1, 0}, {0, 0, 1, 0, 0}}; // 5x5 structuring element
//
//        // perform dilation on each pixel
//        for (int y = 0; y < height; y++) {
//            for (int x = 0; x < width; x++) {
//                boolean allZeros = true;
//                for (int j = -1; j <= 1; j++) {
//                    for (int i = -1; i <= 1; i++) {
//                        if (y + j < 0 || y + j >= height || x + i < 0 || x + i >= width) {
//                            // skip pixels outside the image boundary
//                            continue;
//                        }
//                        int pixel = image.getRGB(x + i, y + j);
//                        if (structuringElement[j + 1][i + 1] == 1 && pixel == 0xFFFFFFFF) {
//                            // if structuring element has a 1 and corresponding pixel is white,
//                            // set the current pixel to white and stop checking other pixels in the structuring element
//                            result.setRGB(x, y, 0xFFFFFFFF);
//                            allZeros = false;
//                            break;
//                        }
//                    }
//                    if (!allZeros) {
//                        break;
//                    }
//                }
//                if (allZeros) {
//                    result.setRGB(x, y, 0xFF000000);
//                }
//            }
//        }
//        return result;
//    }

    public static BufferedImage dilate(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        BitSet structuringElement = new BitSet(25);
        structuringElement.set(2);
        structuringElement.set(7);
        structuringElement.set(8);
        structuringElement.set(9);
        structuringElement.set(12);

        int[][] bounds = new int[height][2];
        for (int y = 0; y < height; y++) {
            bounds[y][0] = Math.max(0, y - 2);
            bounds[y][1] = Math.min(height - 1, y + 2);
        }

        AtomicInteger count = new AtomicInteger(0);
        IntStream.range(0, height * width).parallel().forEach(n -> {
            int y = n / width;
            int x = n % width;
            boolean allZeros = true;
            for (int j = -2; j <= 2; j++) {
                int y0 = y + j;
                if (y0 < bounds[y][0] || y0 > bounds[y][1]) {
                    continue;
                }
                for (int i = -2; i <= 2; i++) {
                    int x0 = x + i;
                    if (x0 < 0 || x0 >= width) {
                        continue;
                    }
                    if (structuringElement.get((j + 2) * 5 + (i + 2))) {
                        int pixel = image.getRGB(x0, y0);
                        if (pixel == 0xFFFFFFFF) {
                            result.setRGB(x, y, 0xFFFFFFFF);
                            allZeros = false;
                            break;
                        }
                    }
                }
                if (!allZeros) {
                    break;
                }
            }
            if (allZeros) {
                result.setRGB(x, y, 0xFF000000);
            }
            int c = count.incrementAndGet();
            if (c % 100000 == 0) {
               LOGGER.info("{} pixels processed", c);
            }
        });

        return result;
    }

    public static BufferedImage blendPixels(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        // Create a new BufferedImage to store the blended image
        BufferedImage blendedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // Loop over each pixel in the original image
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // Get the RGB values of the current pixel
                int rgb = image.getRGB(x, y);
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;

                // Calculate the blended RGB values of the current pixel and the pixel to the bottom right
                int blendRed = red * 8 / 10;
                int blendGreen = green * 8 / 10;
                int blendBlue = blue * 8 / 10;

                int blendRed2 = 0;
                int blendGreen2 = 0;
                int blendBlue2 = 0;

                // Check if there is a pixel to the bottom right
                if (x + 5 < width && y + 5 < height) {
                    // Get the RGB values of the pixel to the bottom right
                    int rgb2 = image.getRGB(x + 1, y + 1);
                    blendRed2 = (rgb2 >> 16) & 0xFF;
                    blendGreen2 = (rgb2 >> 8) & 0xFF;
                    blendBlue2 = rgb2 & 0xFF;

                    // Calculate the blended RGB values of the current pixel and the pixel to the bottom right
                    blendRed += blendRed2 * 0.25;
                    blendGreen += blendGreen2 * 0.25;
                    blendBlue += blendBlue2 * 0.25;
                }

                // Combine the blended RGB values into a single pixel value and set it in the blended image
                int blendedRgb = (blendRed << 16) | (blendGreen << 8) | blendBlue;
                blendedImage.setRGB(x, y, blendedRgb);
            }
        }

        return blendedImage;
    }



    // I/O related Methods

    /**
     * Creates a .png file on the file system with the contents of the image.
     * @param image
     * @param filename
     */
    public static void exportImageFile(final BufferedImage image, String filename){
        LOGGER.info("exportImageFile() - enter.");

        // Write image to file
        try {
            File directory = new File(IMAGE_DETECTION_IMAGE_OUT_DIR.getValue());
            if (!directory.exists()){
                directory.mkdir();
            }

            File file = new File(directory, filename + ".png");
            ImageIO.write(image, "png", file);
            LOGGER.info("exportImageFile() - {}.png exported successfully. in {} - leave.", filename, file.getCanonicalPath());
        } catch (Exception e) {
            LOGGER.error("exportImageFile() - error:", e);
        }
    }

    /**
     * Imports a file into a BufferedImage object
     * @param filename
     * @return
     * @throws IOException
     */
    public static BufferedImage importImageFile(String filename) throws IOException, ImageProcessingException {
        LOGGER.info("importImageFile()- importing file {} - enter", filename);
        File directory = new File(IMAGE_DETECTION_IMAGE_IN_DIR.getValue());
        if(!directory.exists()){
            throw new ImageProcessingException("Directory " + "data/pictures/samples doesn't exist");
        }
        File inputFile = new File(directory, filename);

        if(!inputFile.exists()){
            throw new ImageProcessingException("Input file " + filename + " doesn't exist.");
        }
            BufferedImage ret = ImageIO.read(inputFile);
        LOGGER.info("importImageFile() import of {} was successful.", filename);
        return ret;
    }

}
