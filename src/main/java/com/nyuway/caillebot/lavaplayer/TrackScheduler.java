package com.nyuway.caillebot.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AudioEventAdapter {

    private final AudioPlayer player;
    private BlockingQueue<AudioTrack> queue = new LinkedBlockingQueue<>();
    private  AudioTrack lastAddedTrack;

    private boolean repeatMode = false;

    public TrackScheduler(AudioPlayer player) {
        this.player = player;
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if(repeatMode) {
            player.startTrack(track.makeClone(), false);
        } else {
            player.startTrack(queue.poll(), false);
        }
    }

    public void queue(AudioTrack track) {
        if(!player.startTrack(track, true)) {
            queue.offer(track);
        }
        this.setLastAddedTrack(track);
    }

    public AudioPlayer getPlayer() {
        return player;
    }

    public BlockingQueue<AudioTrack> getQueue() {
        return queue;
    }

    public boolean isRepeatModeEnabled() {
        return repeatMode;
    }

    public void setRepeatMode(boolean b) {
        repeatMode = b;
    }

    public static <T> T getLastElement(BlockingQueue<T> queue) {
        if (queue == null || queue.isEmpty()) {
            throw new IllegalArgumentException("La queue est vide");
        }

        T lastElement = null;
        for (T element : queue) {
            lastElement = element;
        }

        return lastElement;
    }

    public AudioTrack getLastAddedTrack() {
        return lastAddedTrack;
    }

    public AudioTrack getLastTrack() {
        return queue.peek();
    }

    public void setLastAddedTrack(AudioTrack lastAddedTrack) {
        this.lastAddedTrack = lastAddedTrack;
    }
}
