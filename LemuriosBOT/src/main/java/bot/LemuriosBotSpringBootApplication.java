package bot;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class LemuriosBotSpringBootApplication {

	private static final String API_TOKEN = "mytoken-1234-5123412345";

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(LemuriosBotSpringBootApplication.class);
		ApplicationContext context = app.run(args);
		LemuriosBOT lemuriosBOT = context.getBean(LemuriosBOT.class);

		JDABuilder builder = JDABuilder.createDefault(API_TOKEN);
		builder.enableIntents(GatewayIntent.GUILD_MESSAGES,
				GatewayIntent.GUILD_MEMBERS,
				GatewayIntent.GUILD_MESSAGE_REACTIONS,
				GatewayIntent.DIRECT_MESSAGES,
				GatewayIntent.DIRECT_MESSAGE_REACTIONS,
				GatewayIntent.MESSAGE_CONTENT);

		builder.addEventListeners(lemuriosBOT).build();
	}

}
