package com.nyuway.caillebot.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import org.jetbrains.annotations.NotNull;

public class GuildMusicManager {

    private TrackScheduler trackScheduler;
    private AudioForwarder audioForwarder;

    public GuildMusicManager(@NotNull AudioPlayerManager playerManager) {
        AudioPlayer player = playerManager.createPlayer();
        trackScheduler = new TrackScheduler(player);
        player.addListener(trackScheduler);
        audioForwarder = new AudioForwarder(player);
    }

    public TrackScheduler getTrackScheduler() {
        return trackScheduler;
    }

    public AudioForwarder getAudioForwarder() {
        return audioForwarder;
    }

}