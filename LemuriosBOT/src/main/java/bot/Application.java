package bot;

import bot.application.utils.ConfigurationFileAccessor;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class Application {
	private ConfigurationFileAccessor configurationFileAccessor;
	private static final String API_TOKEN_KEY = "discord_dev_key";

	public static void main(final String[] args) {
		var app = new SpringApplication(Application.class);
		var context = app.run(args);
		var application = context.getBean(Application.class);
		application.init(context);
	}

	private void init(ApplicationContext context) {
		// Get API key from configuration
		final String API_KEY = configurationFileAccessor.getValue(API_TOKEN_KEY);

		// Read shard configuration from environment variables (with defaults)
		var shardCount = Integer.parseInt(System.getenv().getOrDefault("SHARD_COUNT", "1"));
		var shardId = Integer.parseInt(System.getenv().getOrDefault("SHARD_ID", "0"));

		LemuriosBOTListenerAdapter listener = context.getBean(LemuriosBOTListenerAdapter.class);

		JDABuilder builder = JDABuilder.createDefault(API_KEY);
		builder.enableIntents(
				GatewayIntent.GUILD_MESSAGES,
				GatewayIntent.GUILD_MEMBERS,
				GatewayIntent.GUILD_MESSAGE_REACTIONS,
				GatewayIntent.DIRECT_MESSAGES,
				GatewayIntent.DIRECT_MESSAGE_REACTIONS,
				GatewayIntent.MESSAGE_CONTENT
		);

		// Set shard information if using multiple shards
		builder.useSharding(shardId, shardCount);

		builder.addEventListeners(listener).build();
	}

	@Autowired
	public void setPropertiesUtil(ConfigurationFileAccessor configurationFileAccessor) {
		this.configurationFileAccessor = configurationFileAccessor;
	}
}
