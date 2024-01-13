package com.nyuway.caillebot.commands.music;

import com.nyuway.caillebot.ICommand;
import com.nyuway.caillebot.lavaplayer.GuildMusicManager;
import com.nyuway.caillebot.lavaplayer.PlayerManager;
import com.nyuway.caillebot.lavaplayer.TrackScheduler;
import com.nyuway.caillebot.utils.TimeFormatter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackState;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;

public class Play implements ICommand {
    @Override
    public String getName() {
        return "play";
    }

    @Override
    public String getDescription() {
        return "Play a song";
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.STRING, "query", "The name/url of the song to play").setRequired(true));
        return options;
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        GuildVoiceState memberVoiceState = null;
        if (member != null) {
            memberVoiceState = member.getVoiceState();
        }

        if (memberVoiceState == null || !memberVoiceState.inAudioChannel()) {
            event.reply("Vous devez être connecté dans un salon vocal pour exécuter cette commande.").setEphemeral(true).queue();
            return;
        }

        Member selfMember = Objects.requireNonNull(event.getGuild()).getSelfMember();
        GuildVoiceState selfVoiceState = selfMember.getVoiceState();

        if (!Objects.requireNonNull(selfVoiceState).inAudioChannel()) {
            event.getGuild().getAudioManager().openAudioConnection(memberVoiceState.getChannel());
        }

        String query = Objects.requireNonNull(event.getOption("query")).getAsString();

        if (!query.startsWith("http")) {
            try {
                new URI(query);
            } catch (URISyntaxException e) {
                query = "ytsearch:" + query;
            }
        }

        PlayerManager playerManager = PlayerManager.getInstance();
        GuildMusicManager guildMusicManager = playerManager.getGuildMusicManager(event.getGuild());
        playerManager.play(event.getGuild(), query); // added

        AudioTrack track = guildMusicManager.getTrackScheduler().getQueue().isEmpty() ? waitForTrackToBeQueued(guildMusicManager) : TrackScheduler.getLastElement(guildMusicManager.getTrackScheduler().getQueue());

        System.out.println(event.getGuild().getName() + " » Playing: " + track.getInfo().title + " by " + track.getInfo().author + " (" + track.getInfo().uri + ")");

        AudioTrackInfo info = track.getInfo();

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Ajouté à la file d'attente");
        embed.setDescription("[" + info.title + "](" + info.uri + ")");
        embed.appendDescription("\nAuteur : " + info.author);
        embed.appendDescription("\nURL : " + info.uri);
        embed.setThumbnail("https://img.youtube.com/vi/" + info.identifier + "/maxresdefault.jpg");
        embed.setFooter("Durée : " + TimeFormatter.formatMs(info.length));

        event.replyEmbeds(embed.build()).queue();

    }

    @Nullable
    private AudioTrack waitForTrackToBeQueued(GuildMusicManager guildMusicManager) {
        // run that in a separate thread

        int attempts = 0;
        while (attempts < 10) {

            BlockingQueue<AudioTrack> queue = guildMusicManager.getTrackScheduler().getQueue();
            AudioTrack currentTrack = guildMusicManager.getTrackScheduler().getPlayer().getPlayingTrack();

            if (!queue.isEmpty()) {
                return TrackScheduler.getLastElement(queue);
            } else if (currentTrack != null && currentTrack.getState() == AudioTrackState.LOADING) {
                return currentTrack;
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            attempts++;
        }
        return null;
    }


}
