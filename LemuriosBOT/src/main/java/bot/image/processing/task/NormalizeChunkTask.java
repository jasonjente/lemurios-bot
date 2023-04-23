package bot.image.processing.task;

import bot.image.processing.ImageProcessor;

import java.awt.image.BufferedImage;

public class NormalizeChunkTask implements Runnable {
    private final BufferedImage chunk;
    private double brightness;

    public NormalizeChunkTask(BufferedImage chunk, double brightness) {
        this.chunk = chunk;
        this.brightness = brightness;
    }

    public void run() {
        double[] avgRGB = getAvgRGB(chunk);
        try {
            normalizePixels(chunk, avgRGB, brightness);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void normalizePixels(BufferedImage image, double[] avgRGB, double brightness) throws InterruptedException {
        doNormalization(image, avgRGB, brightness);
    }

    private static void doNormalization(BufferedImage image, double[] avgRGB, double brightness) throws InterruptedException {
        int width = image.getWidth();
        int height = image.getHeight();

        double chunkAverageBrightness = ImageProcessor.calculateAverageImageBrightness(image);
        double rScale = chunkAverageBrightness / avgRGB[0];
        double gScale = chunkAverageBrightness / avgRGB[1];
        double bScale = chunkAverageBrightness / avgRGB[2];
        //If chunks average brightness is greater than the images average, choose it.
        //Helps with areas that are at the sides of the bell curve in terms of avg brightness and avoid jarred lines
        brightness =  chunkAverageBrightness>brightness ? chunkAverageBrightness : brightness ;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);

                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;

                r = (int) Math.min(r * rScale, brightness);
                g = (int) Math.min(g * gScale, brightness);
                b = (int) Math.min(b * bScale, brightness);

                int normalizedRGB = (r << 16) | (g << 8) | b;
                image.setRGB(x, y, normalizedRGB);
            }
        }

    }

    private static double[] getAvgRGB(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        double rSum = 0;
        double gSum = 0;
        double bSum = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                rSum += (rgb >> 16) & 0xff;
                gSum += (rgb >> 8) & 0xff;
                bSum += rgb & 0xff;
            }
        }

        int numPixels = width * height;
        double rAvg = rSum / numPixels;
        double gAvg = gSum / numPixels;
        double bAvg = bSum / numPixels;

        double[] avgRGB = new double[3];
        avgRGB[0] = rAvg;
        avgRGB[1] = gAvg;
        avgRGB[2] = bAvg;
        return avgRGB;
    }
}