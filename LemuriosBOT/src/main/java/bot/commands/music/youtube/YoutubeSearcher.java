package bot.commands.music.youtube;

import bot.application.exceptions.YoutubeSearchException;
import bot.application.utils.ConfigurationFileAccessor;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static java.lang.String.format;

@Service
@Slf4j
public class YoutubeSearcher {
    
    private static final String YOUTUBE_API_KEY_ENTRY = "youtube_dev_key";
    private static final String APPLICATION_NAME_ENTRY = "application_name";

    private ConfigurationFileAccessor configurationFileAccessor;


    /**
     * Call function to create API service object. Define and
     * execute API request. Print API response.
     *
     * @throws GeneralSecurityException, IOException in case of errors during the track execution.
     */
    public YoutubeResult search(String requestedSong) throws YoutubeSearchException {
        log.info("search() - ENTER - requesting: {}", requestedSong);
        try {
            var youtubeService = getService();
            YouTube.Search.List request = youtubeService.search().list("snippet");
            var response = sendRequest(requestedSong, request);

            var youtubeResult = createYoutubeResults(response);
            youtubeResult.setRequestedTitle(requestedSong);

            log.info("search() - LEAVE - Found this: title: {}, video id: {}",
                    youtubeResult.getActualTitle(), youtubeResult.getVideoIdentifier());
            return youtubeResult;

        } catch (GeneralSecurityException | IOException e) {
            throw new YoutubeSearchException(format("Error during the search of the track: %s", requestedSong), e);
        }
    }

    /**
     * Build and return an authorized API client service.
     *
     * @return an authorized API client service
     * @throws GeneralSecurityException, IOException
     */
    private YouTube getService() throws GeneralSecurityException, IOException {
        final var httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        var applicationName = configurationFileAccessor.getValue(APPLICATION_NAME_ENTRY);
        return new YouTube.Builder(httpTransport, JacksonFactory.getDefaultInstance(), null)
                .setApplicationName(applicationName).build();
    }

    private SearchListResponse sendRequest(final String requestedSong, final YouTube.Search.List request)
            throws IOException {
        var devKeyValue = configurationFileAccessor.getValue(YOUTUBE_API_KEY_ENTRY);
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
        if (response.getItems().get(0).getId().getPlaylistId() != null) {
            String playlistUrl = response.getItems().get(0).getId().getPlaylistId();
            ret.setPlaylistUrl(playlistUrl);
        }

        ret.setVideoIdentifier(searchResult.getId().getVideoId());
        ret.setThumbnailUrl(searchResult.getSnippet().getThumbnails().getDefault().getUrl());
        return ret;
    }

    @Autowired
    public void setPropertiesUtil(ConfigurationFileAccessor configurationFileAccessor) {
        this.configurationFileAccessor = configurationFileAccessor;
    }

}