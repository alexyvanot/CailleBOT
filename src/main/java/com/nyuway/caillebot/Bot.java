package com.nyuway.caillebot;

import com.nyuway.caillebot.commands.CommandManager;
import com.nyuway.caillebot.commands.utilities.Ping;
import com.nyuway.caillebot.commands.music.*;
import com.nyuway.caillebot.listeners.EventListener;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;
import java.util.List;

public class Bot {

    private final Dotenv config;
    private final ShardManager shardManager;

    public Bot() throws LoginException {
        config = Dotenv.configure().load();
        String token = config.get("TOKEN");

        List<GatewayIntent> intents = List.of(
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_MESSAGE_REACTIONS,
                GatewayIntent.MESSAGE_CONTENT,
                GatewayIntent.GUILD_PRESENCES
        );

        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(token);
        builder.setStatus(OnlineStatus.ONLINE);
        builder.setActivity(Activity.streaming("vive la caillé", "https://www.twitch.tv/monstercat"));
        builder.enableIntents(intents);
        builder.setMemberCachePolicy(MemberCachePolicy.ALL);
        builder.setChunkingFilter(ChunkingFilter.ALL);
        builder.enableCache(CacheFlag.ONLINE_STATUS);

        // build
        shardManager = builder.build();

        // Listeners
        shardManager.addEventListener(new EventListener());

        // Commands
        CommandManager commandManager = new CommandManager();
        commandManager.add(new Ping());
        commandManager.add(new Play());
        commandManager.add(new Stop());
        commandManager.add(new NowPlaying());
        commandManager.add(new Join());
        commandManager.add(new Leave());
        commandManager.add(new Skip());
        commandManager.add(new Pause());
        commandManager.add(new Resume());
        commandManager.add(new Repeat());

        shardManager.addEventListener(commandManager);

    }

    public Dotenv getConfig() {
        return config;
    }

    public ShardManager getShardManager() {
        return shardManager;
    }

    public static void main(String[] args) {
        try {
            Bot bot = new Bot();
        } catch (LoginException e) {
            System.err.println("Erreur de connexion: le token est invalide");
            e.printStackTrace();
        }

    }

}
