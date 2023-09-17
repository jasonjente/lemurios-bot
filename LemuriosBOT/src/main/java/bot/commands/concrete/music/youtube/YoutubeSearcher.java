package bot.commands.concrete.music.youtube;

import bot.exceptions.YoutubeSearchException;
import bot.utils.PropertiesUtil;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Service
public class YoutubeSearcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(YoutubeSearcher.class);
    private static final String YOUTUBE_API_KEY_ENTRY = "youtube_dev_key";
    private static final String APPLICATION_NAME_ENTRY = "application_name";

    private PropertiesUtil propertiesUtil;


    /**
     * Call function to create API service object. Define and
     * execute API request. Print API response.
     *
     * @throws GeneralSecurityException, IOException
     */
    public YoutubeResult search(String requestedSong) throws YoutubeSearchException {
        LOGGER.info("search() - ENTER - requesting: {}", requestedSong);
        try {
            YouTube youtubeService = getService();
            YouTube.Search.List request = youtubeService.search().list("snippet");
            SearchListResponse response = sendRequest(requestedSong, request);

            YoutubeResult youtubeResult = createYoutubeResults(response);
            youtubeResult.setRequestedTitle(requestedSong);

            LOGGER.info("search() - LEAVE - Found this: title: {}, video id: {}", youtubeResult.getActualTitle(), youtubeResult.getVideoIdentifier());
            return youtubeResult;

        } catch (GeneralSecurityException | IOException e) {
            throw new YoutubeSearchException(e);
        }
    }

    /**
     * Build and return an authorized API client service.
     *
     * @return an authorized API client service
     * @throws GeneralSecurityException, IOException
     */
    private YouTube getService() throws GeneralSecurityException, IOException {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        String applicationName = propertiesUtil.getValue(APPLICATION_NAME_ENTRY);
        return new YouTube.Builder(httpTransport, JacksonFactory.getDefaultInstance(), null).setApplicationName(applicationName).build();
    }

    private SearchListResponse sendRequest(String requestedSong, YouTube.Search.List request) throws IOException {
        String devKeyValue = propertiesUtil.getValue(YOUTUBE_API_KEY_ENTRY);
        return request.setKey(devKeyValue)
                .setChannelType("any")
                .setMaxResults(3L)
                .setQ(requestedSong)
                .setSafeSearch("none")
                .setVideoDuration("any")
                .setVideoLicense("any")
                .execute();
    }

    private YoutubeResult createYoutubeResults(SearchListResponse response) {
        YoutubeResult ret = new YoutubeResult();
        SearchResult searchResult = response.getItems().get(0);
        ret.setActualTitle(searchResult.getSnippet().getTitle());
        ret.setUploader(searchResult.getSnippet().getChannelTitle());
        if(response.getItems().get(0).getId().getPlaylistId()!= null) {
            String playlistUrl = response.getItems().get(0).getId().getPlaylistId();
            ret.setPlaylistUrl(playlistUrl);
        }

        ret.setVideoIdentifier(searchResult.getId().getVideoId());
        ret.setThumbnailUrl(searchResult.getSnippet().getThumbnails().getDefault().getUrl());
        return ret;
    }

    @Autowired
    public void setPropertiesUtil(PropertiesUtil propertiesUtil) {
        this.propertiesUtil = propertiesUtil;
    }

}