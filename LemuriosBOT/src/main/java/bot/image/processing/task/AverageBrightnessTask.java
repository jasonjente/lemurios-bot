package bot.image.processing.task;

import java.awt.image.BufferedImage;

public class AverageBrightnessTask extends Thread{

    private final BufferedImage image;
    private final int startIndex;
    private final int endIndex;
    private double threadBrightness;

    public AverageBrightnessTask(BufferedImage image, int startIndex, int endIndex) {
        this.image = image;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    public double getThreadBrightness() {
        return threadBrightness;
    }

    @Override
    public void run() {
        int sum = 0;
        for (int i = startIndex; i < endIndex; i++) {
            int x = i % image.getWidth();
            int y = i / image.getWidth();
            int pixel = image.getRGB(x, y);
            int red = (pixel >> 16) & 0xff;
            int green = (pixel >> 8) & 0xff;
            int blue = pixel & 0xff;
            int brightness = (int) (0.2126 * red + 0.7152 * green + 0.0722 * blue);
            sum += brightness;
        }
        threadBrightness = sum;
    }
}
