package bot;

import bot.application.utils.PropertiesUtil;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class Application {
	private PropertiesUtil propertiesUtil;
	private static final String API_TOKEN_KEY = "discord_dev_key";

	public static void main(final String[] args) {
		SpringApplication app = new SpringApplication(Application.class);
		ApplicationContext context = app.run(args);

		Application application = context.getBean(Application.class);
		application.init(context);
	}

	private void init(ApplicationContext context) {
		final String API_KEY = propertiesUtil.getValue(API_TOKEN_KEY);
		LemuriosBOTListenerAdapter lemuriosBOTListenerAdapter = context.getBean(LemuriosBOTListenerAdapter.class);

		JDABuilder builder = JDABuilder.createDefault(API_KEY);
		builder.enableIntents(GatewayIntent.GUILD_MESSAGES,
				GatewayIntent.GUILD_MEMBERS,
				GatewayIntent.GUILD_MESSAGE_REACTIONS,
				GatewayIntent.DIRECT_MESSAGES,
				GatewayIntent.DIRECT_MESSAGE_REACTIONS,
				GatewayIntent.MESSAGE_CONTENT);

		builder.addEventListeners(lemuriosBOTListenerAdapter).build();
	}
	@Autowired
	public void setPropertiesUtil(PropertiesUtil propertiesUtil) {
		this.propertiesUtil = propertiesUtil;
	}
}
