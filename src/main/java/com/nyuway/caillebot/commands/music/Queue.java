package com.nyuway.caillebot.commands.music;

import com.nyuway.caillebot.ICommand;
import com.nyuway.caillebot.lavaplayer.GuildMusicManager;
import com.nyuway.caillebot.lavaplayer.PlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.BlockingQueue;

public class Queue implements ICommand {
    @Override
    public String getName() {
        return "queue";
    }

    @Override
    public String getDescription() {
        return "Show the current queue of songs";
    }

    @Override
    public List<OptionData> getOptions() {
        return null;
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        Member selfMember = event.getGuild().getSelfMember();
        GuildMusicManager guildMusicManager = PlayerManager.getInstance().getGuildMusicManager(event.getGuild());

        if(!selfMember.getVoiceState().inAudioChannel()) {
            event.reply("Le bot doit être connecté dans un salon vocal pour exécuter cette commande.").setEphemeral(true).queue();
            return;
        }

        if(selfMember.getVoiceState().getChannel() != member.getVoiceState().getChannel()) {
            event.reply("Vous devez être connecté dans le même salon vocal que le bot pour exécuter cette commande.").setEphemeral(true).queue();
            return;
        }

        if(guildMusicManager.getTrackScheduler().getPlayer().getPlayingTrack() == null) {
            event.reply("Il n'y a pas de musique en cours de lecture.").setEphemeral(true).queue();
            return;
        }

        AudioTrackInfo info = guildMusicManager.getTrackScheduler().getPlayer().getPlayingTrack().getInfo();
        BlockingQueue<AudioTrack> queue = guildMusicManager.getTrackScheduler().getQueue();

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("File d'attente");
        embed.setColor(0x1ED760);
        embed.setDescription("Playlist actuelle:");
        embed.setTimestamp(event.getTimeCreated());
        embed.appendDescription("\n1. " + info.title);
        for (AudioTrack audioTrack : queue) {
            embed.appendDescription("\n" + (queue.size() + 1) + ". " + audioTrack.getInfo().title);
        }

        event.replyEmbeds(embed.build()).queue();

    }
}
