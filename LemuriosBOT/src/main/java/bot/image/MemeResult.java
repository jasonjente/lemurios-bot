package bot.image;

import java.io.File;

/**
 * Wrapper class for Files. used for the boolean flag pretty much
 */
public class MemeResult {
    private boolean isSent;
    private File file;
    private String filename;


    public MemeResult(boolean isSent, File file, String filename) {
        this.isSent = isSent;
        this.file = file;
        this.filename = filename;
    }

    public boolean isSent() {
        return isSent;
    }

    public void setSent(boolean sent) {
        isSent = sent;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
