package bot.application.utils;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.springframework.util.ResourceUtils.toURL;

@Service
public class HTTPConnectionService {

    @NotNull
    private HttpURLConnection getHttpURLConnection(final String attachmentUrl) throws IOException {
        URL url = toURL(attachmentUrl);
        var connection = (HttpURLConnection) url.openConnection();

        // Set the headers to match the ones sent by a browser to trick the CDN and avoid error 403
        connection.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
        connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        connection.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
        connection.setRequestProperty("Referer", "https://discord.com/channels/");
        connection.setRequestProperty("Cookie", "cookies_here");
        return connection;
    }


}
