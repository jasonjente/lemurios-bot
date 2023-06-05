package bot.commands.concrete.music.youtube;

public class YoutubeResult {
    private String videoIdentifier;
    private String requestedTitle;
    private String actualTitle;
    private String uploader;


    public String getVideoIdentifier() {
        return videoIdentifier;
    }

    public String getVideoURL(){
        return "https://youtube.com/watch?v=" + this.getVideoIdentifier();
    }

    public void setVideoIdentifier(String videoIdentifier) {
        this.videoIdentifier = videoIdentifier;
    }

    public String getRequestedTitle() {
        return requestedTitle;
    }

    public void setRequestedTitle(String requestedTitle) {
        this.requestedTitle = requestedTitle;
    }

    public String getActualTitle() {
        return actualTitle;
    }

    public void setActualTitle(String actualTitle) {
        this.actualTitle = actualTitle;
    }

    public String getUploader() {
        return uploader;
    }

    public void setUploader(String uploader) {
        this.uploader = uploader;
    }

}
