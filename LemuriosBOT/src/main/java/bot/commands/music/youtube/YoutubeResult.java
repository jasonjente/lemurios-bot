package bot.commands.music.youtube;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class YoutubeResult {
    private String videoIdentifier;
    private String requestedTitle;
    private String actualTitle;
    private String uploader;
    private String thumbnailUrl;
    private String playlistUrl;


    public String getVideoURL(){
        return "https://youtube.com/watch?v=" + this.getVideoIdentifier();
    }

}
