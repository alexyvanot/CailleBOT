package com.nyuway.caillebot.commands.music;

import com.nyuway.caillebot.ICommand;
import com.nyuway.caillebot.lavaplayer.GuildMusicManager;
import com.nyuway.caillebot.lavaplayer.PlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public class NowPlaying implements ICommand {
    @Override
    public String getName() {
        return "nowplaying";
    }

    @Override
    public String getDescription() {
        return "Display the current song";
    }

    @Override
    public List<OptionData> getOptions() {
        return null;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        GuildVoiceState memberVoiceState = member.getVoiceState();

        Member selfMember = event.getGuild().getSelfMember();
        GuildVoiceState selfVoiceState = selfMember.getVoiceState();

        if(!selfVoiceState.inAudioChannel()) {
            event.reply("Le bot doit être connecté dans un salon vocal pour exécuter cette commande.").setEphemeral(true).queue();
            return;
        }

        GuildMusicManager guildMusicManager = PlayerManager.getInstance().getGuildMusicManager(event.getGuild());
        if(guildMusicManager.getTrackScheduler().getPlayer().getPlayingTrack() == null) {
            event.reply("Il n'y a pas de musique en cours de lecture.").setEphemeral(true).queue();
            return;
        }

        AudioTrackInfo info = guildMusicManager.getTrackScheduler().getPlayer().getPlayingTrack().getInfo();
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Lecture en cours");
        embed.setDescription("[" + info.title + "](" + info.uri + ")");
        embed.appendDescription("\nAuteur: " + info.author);
        embed.appendDescription("\nURL: " + info.uri);
        embed.setThumbnail("https://img.youtube.com/vi/" + info.identifier + "/maxresdefault.jpg");
        embed.setFooter("Durée: " + guildMusicManager.getTrackScheduler().getPlayer().getPlayingTrack().getDuration());
        event.replyEmbeds(embed.build()).queue();
    }
}
