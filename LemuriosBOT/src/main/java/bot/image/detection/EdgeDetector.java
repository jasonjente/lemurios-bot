package bot.image.detection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class EdgeDetector {
    private static final Logger LOGGER = LoggerFactory.getLogger(EdgeDetector.class);
    private static final int COLOR_RED = 0;
    private static final int COLOR_GREEN = 1;
    private static final int COLOR_BLUE = 2;
    private static final int NUM_THREADS = 1024;

    //I found that values between -1/12 to -5/12 work pretty well in detecting edges when I am not using any other
    // filters like a gaussian blur.
    // e.g. for low values (close to 0) the detector will be sensitive to edges, otherwise for lower values
    // (close to -0,5) it will create a brighter image with fewer edges.
    private static final double THRESHOLD = -22.0 / 255.0;

    private EdgeDetector(){
        //should not be initialized
    }

    public static BufferedImage detectEdges(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        //5x5 Filter used to detect edges. The kernel values are based on the Laplacian of Gaussian (LoG) filter,
        // which is a commonly used edge detection filter in image processing.
        double[][] kernel = {{0, 0, 1, 0, 0}, {0, 1, 2, 1, 0}, {1, 2, -16, 2, 1}, {0, 1, 2, 1, 0}, {0, 0, 1, 0, 0}};

        int chunkSize = height / 8;

        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);

        for (int chunk = 0; chunk < 8; chunk++) {
            final int yStart = chunk * chunkSize;
            final int yEnd = (chunk == 7) ? height : (chunk + 1) * chunkSize;

            executor.execute(() -> {
                for (int y = yStart; y < yEnd; y++) {
                    for (int x = 0; x < width; x++) {
                        double intensity = applyKernel(image, kernel, x, y);
                        //reverse condition to make white background with black lines
                        if (intensity > THRESHOLD) {
                            result.setRGB(x, y, Color.BLACK.getRGB());
                        } else {
                            result.setRGB(x, y, Color.WHITE.getRGB());
                        }
                    }
                }
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            LOGGER.error("Error in concurrency: ", e);
        }

        return result;
    }

    /**
     *      For each pixel of the output image, the kernel is centered on that pixel and the values of the surrounding pixels
     * are multiplied by the corresponding values in the kernel. This results in a weighted sum of the pixel intensities
     * in the neighborhood of the current pixel, with the weights given by the kernel values.
     *      The nested loops iterate over the kernel matrix, and for each kernel element, it calculates the corresponding
     * pixel coordinates in the input image. If the pixel coordinates are out of bounds of the image,
     * the loop skips that iteration.
     *      For each valid pixel coordinate, the function getColorIntensity() retrieves the intensity value of the
     * corresponding color channel (red, green, or blue) from the input image. The intensity value is multiplied by the
     * corresponding kernel value and added to a running sum sum.
     *      After iterating over all the kernel elements, the final value of sum is returned. This value represents the
     * weighted sum of the pixel intensities in the neighborhood of the current pixel, as defined by the kernel.
     */
    private static double applyKernel(BufferedImage image, double[][] kernel, int x, int y) {
        int kernelSize = kernel.length;
        int radius = kernelSize / 2;

        double sum = 0.0;

        for (int ky = 0; ky < kernelSize; ky++) {
            for (int kx = 0; kx < kernelSize; kx++) {
                int pixelX = x + kx - radius;
                int pixelY = y + ky - radius;

                if (pixelX < 0 || pixelY < 0 || pixelX >= image.getWidth() || pixelY >= image.getHeight()) {
                    continue;
                }
                //    By averaging the intensities across all color channels, it is likely to give more accurate
                // edge detection results, as it takes into account information from all color
                // channels rather than just one.
                double intensity = getColorIntensity(image.getRGB(pixelX, pixelY), COLOR_BLUE);
                intensity += getColorIntensity(image.getRGB(pixelX, pixelY), COLOR_RED);
                intensity += getColorIntensity(image.getRGB(pixelX, pixelY), COLOR_GREEN);

                intensity /=3;

                sum += intensity * kernel[ky][kx];
            }
        }

        return sum;
    }

    /**
     *
     *      If the color component is red, the method shifts the bits of the pixel argument 16 bits to the right
     * ((pixel >> 16)) to get the value of the red color component, applies a bitwise AND with 0xff to mask
     * off the lower 8 bits, and divides by 255.0 to get a value between 0.0 and 1.0.
     *
     * If the color component is green, the method shifts the bits of the pixel argument 8 bits to the right
     * ((pixel >> 8)) to get the value of the green color component, applies a bitwise AND with 0xff to mask off the
     * lower 8 bits, and divides by 255.0 to get a value between 0.0 and 1.0.
     *
     * If the color component is blue, the method masks off the lower 8 bits of the pixel argument with 0xff to get
     * the value of the blue color component, and divides by 255.0 to get a value between 0.0 and 1.0.
     *
     */
    private static double getColorIntensity(int pixel, int color) {
        switch (color) {
            case COLOR_RED:
                return ((pixel >> 16) & 0xff) / 255.0;
            case COLOR_GREEN:
                return ((pixel >> 8) & 0xff) / 255.0;
            case COLOR_BLUE:
                return (pixel & 0xff) / 255.0;
            default:
                return 0.0;
        }
    }
}
