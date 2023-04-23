package bot;

import org.junit.jupiter.api.Test;

import java.io.File;

class LemuriosBOTTest {

    public static void main(String[] args) {
        System.out.println(getAvailableFilename("1.png"));
    }

    private static String getAvailableFilename(String filename){
        File file = new File(new File("C:\\projects\\LemuriosBOT\\images"), filename);
        String trimmedFilename = filename;
        if(file.exists()){
            String suffix = filename.substring(filename.lastIndexOf("."));
            trimmedFilename = filename.replace(suffix,"_1");
            trimmedFilename = trimmedFilename.concat(suffix);
            getAvailableFilename(trimmedFilename);
        }
        return trimmedFilename;
    }
}