## Lemurios Discord BOT Guide

---

### To Add to your server you can use this link:
 - [Bot Invite Link](https://discord.com/oauth2/authorize?client_id=1096774404526063687&permissions=2184226816&redirect_uri=https%3A%2F%2Fdiscordapp.com%2Foauth2%2Fauthorize%3F%26client_id%3D1096774404526063687%26scope%3Dbot&response_type=code&scope=voice%20connections%20bot)
 - Approve the requested rights and accesses.

### Build & Run:

- Build:
```shell
mvn clean package
```

- Run:
```shell
mvn spring-boot:run
```

- Run & Debug:

  - Add your dev. keys on the lemurs.properties file under these names: 
    - **youtube_dev_key**, this is optional, but if ommitted then the youtube functionality will not work.
    - **discord_dev_key**, this is mandatory for the startup of the bot.

```shell
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"
```

---
### Overview:
The commands currently available:
#### Chat commands:
 - /assemblemurs => calls all members of the LEMURIOI role
 - /credits     => shows the credits
 - /help        => shows all available commands
 - /invite      => generates an invite link
 - /history     => shows the last 25 commands executed
 - /taken-names => shows the taken names of Lemurioi
 - /leaderboard => shows the leaderboard for the guild
 ---

#### Image commands:
- /meme                => bot returns a random meme that the server hasn't seen.
- /upload-batch        => upload a zip file containing memes to the bot
- /upload              => lets the user upload a meme to the bot so it can be served later
- /detect-edges        => the user uploads an image and the bot will return a bnw image with the detected edges
---
#### Music commands:  
 - /play + URL or YouTube query => The bot will join the channel that the caller is in and then plays the audio of the provided URL, works with most CDNs (like discords or facebook's) and youtube
 - /set-radio-url + URL         => The bot will save a URL and a genre for later retrieval, works with the /get-radio :genre command.
 - /get-radio-urls              => The bot will join the channel that the caller is in and then plays the audio of the provided URL, works with most CDNs (like discords or facebook's) and youtube
 - /delete-all                  => The bot will delete all urls for the server
 - /delete-genre                => The bot will delete the urls for a specific genre server
 - /pause                       => Pauses the bot
 - /resume                      => Unpauses the bot
 - /skip                        => Skips current track
 - /stop                        => Completely stops current track and removes following tracks from the queue
 - /join                        => The bot will join the voice channel the caller is in
 - /now-playing                 => Shows information about the current track playing (artist and time remaining)
 - /disconnect                  => Disconnects the bot from the voice channel

---
   
 

### Technical Overview:

To improve code re-usability and tidiness we opted to use a java abstract class called Command. Also, we made these commands
act as beans. 

During startup all beans get instantiated and are added to a map where the key is the command name. The command name is taken by an enum which is shared both by the map and the commands that are identified by the bot during the startup.

Please find below a list of the most important classes/beans used in the application:
 * **LemuriosBOT.java - @Component**
   * Responsible for registering the slash interaction commands and map them to java beans/Commands.
     When a Slash Interaction event arrives is picked up by the BOT, the bot will initially reply. During the initial reply, the bot will appear with the Bot is thinking:
   ![img.png](img.png)
 * **LemuriosBotSpringBootApplication.java @SpringBootApplication**,
   * add your own Discord developer key
 * **YoutubeSearcher.java - @Service**
   * add your own YouTube Api developer key
 * **Command.java - @Service**
   * Abstract class which holds all basic functionality of the commands.
 * **MusicPlayerManager.java - @Service**
   * Bean that manages the MusicPlayer Instances

**Finally, each command is responsible for editing the initial reply and adding any information needed by the bot.**

```java
//For example here we edit the initial reply, adding our embed inside.
EmberdBuilder embedBuilder = new EmbedBuilder();
embedBuilder.addField("My field", "Has value of a string that I can choose!", true);
event.getInteraction().getHook().editOriginalEmbeds(embedBuilder.build()).queue();
```
---

#### Example for extending the Command.java

```java
package bot.commands.concrete.placeholder;

import bot.commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;

@Service
public class PlaceHolderCommand extends Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlaceHolderCommand.class);
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        LOGGER.info("{} has requested the placeholder command - ENTER.", event.getUser().getName());
        //Embed builder can hold information such as title, description, color etc. 
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("LEMURIOS BOT - placeholder Command:")
                .setColor(Color.ORANGE)
                .setDescription("Lemurios bot placeholder description")
                .addField("Fields", "you can add fields with information", true)
                .setFooter("p.s. this is a foot");
        //DO other stuff here
        
        //Then grab the interaction hook and edit it by providing our newly created embed.
        event.getInteraction().getHook().editOriginalEmbeds(embedBuilder.build()).queue();
    }

    @Autowired
    public void setMusicPlayerManager(MusicPlayerManager musicPlayerManager) {
        this.musicPlayerManager = musicPlayerManager;
    }
    
   @Override
   public String getCommandDescription() {
      return "Enter a description here about the command.";
   }

   @Override
   public String getCommandName() {
      return PLACEHOLDER_COMMAND.getCommandName();
   }
}
```

Add the following to the Constants.java enum
```java
   PLACEHOLDER_COMMAND("placeholder-command"),
```

Then add the following on the LemuriosBOT.java:
```java
    //Add the bean declaration:
    private PlaceHolderCommand placeHolderCommand;
    @Autowired
    public void setPlaceHolderCommand(PlaceHolderCommand placeHolderCommand){
        this.placeHolderCommand = placeHolderCommand;
    }
    //define the JDA Command under the onGuildReady() method.
        commandData.add(Commands.slash(PLACEHOLDER_COMMAND.getValue(), "Placeholder command"));
    //add on the init() method the following:
        commands.put(PLACEHOLDER_COMMAND.getValue(), placeHolderCommand);

```

The command should be now available on the next startup:
![img_1.png](img_1.png)
---
#### More on Commands:
 - Guild Commands: these commands get instantly deployed. These are the commands under the onGuildReady() method.
 - Global Commands: for production use and takes up to 1 hour to get deployed on the discord's backend. These commands are defined in the onReady() method. {
 ---

### Music Player Functionality Overview:
#### Youtube search:
It should be obvious that entering a URL to play is not the most optimal use case for the user. Most users don't even 
want to type commands and want to do everything with as few keystrokes and clicks as possible. So we created a dev key for YouTube's 
Data API (v3) SaaS, which allows the search for videos by query and the results contain metadata like the video title, the user who 
uploaded the video, thumbnails etc. However, this requires some attention as the use has quota and exceeding the quota can result in termination of the
free tier and the user might have to pay.

#### Music player Manager:
The music player is based on the lava player, developed by sdmelluq. To allow the BOT to join multiple guilds at the same time, a mapping was created
with the MusicPlayerManager bean. This bean, is responsible for handling the 1-to-1 association of music players to guilds. It is responsible for:
- creating Music Players on demand in a synchronous fashion,
- connecting and disconnecting them to and from voice channels

#### Music Player
- The current design allows for one music player being connected to a guild at each time. The player accepts a URL from the user,
or from the search result from YouTube. It can accept, videos, playlists and even some more CDNs for audio like discords. For example
you can upload an mp3 on a chat and then get the URL of that mp3 and play it with the BOT
- Each music player holds an AudioPlayerManager instance and a map called MusicManagers. The AudioManagerPlayer acts as the entry point of the LavaPlayer 
that creates the Audio Track from the URL provided.
- Music Player is responsible for responding back to discord with the thumbnail, the duration, etc.

### Known errors:
- sometimes lava player crashes when loading a youtube links, raising a "FriendlyException". The issue has been mitigated by adding a retransmission 
mechanism that after 2 unsuccessful attempts, it will stop trying to connect.
### Guides:
 * [Lave player](https://github.com/sedmelluq/LavaPlayer#jda-integration) [Has been replaced by LavaPlayerFork]
 * [Lava player-fork](https://github.com/Walkyst/lavaplayer-fork)
 * [JDA Wiki Tutorial](https://jda.wiki/using-jda/making-a-music-bot/)
 

