package bot.image.processing.task;

import java.awt.image.BufferedImage;

public class EqualizeChunkTask implements Runnable {
    private final BufferedImage chunk;

    public EqualizeChunkTask(BufferedImage chunk) {
        this.chunk = chunk;
    }

    public void run() {
        int[] histogram = getHistogram(chunk);
        equalizeHistogram(chunk, histogram);
    }

    private static int[] getHistogram(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        int[] histogram = new int[256];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;
                int yValue = (int) (0.299 * r + 0.587 * g + 0.114 * b);
                histogram[yValue]++;
            }
        }
        return histogram;
    }

    private static void equalizeHistogram(BufferedImage image, int[] histogram) {
        int width = image.getWidth();
        int height = image.getHeight();

        int numPixels = width * height;
        double[] lut = new double[256];
        for (int i = 0; i < 256; i++) {
            double prob = (double) histogram[i] / numPixels;
            if (i == 0) {
                lut[i] = 255.0 * prob;
            } else {
                lut[i] = lut[i - 1] + 255.0 * prob;
            }
        }
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;
                int yValue = (int) (0.299 * r + 0.587 * g + 0.114 * b);

                double scaleFactor = lut[yValue] / 255.0;
                r = (int) (r * scaleFactor);
                g = (int) (g * scaleFactor);
                b = (int) (b * scaleFactor);
                int newRGB = (r << 16) | (g << 8) | b;
                image.setRGB(x, y, newRGB);
            }
        }
    }

}
