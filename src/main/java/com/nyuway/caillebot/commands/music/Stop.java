package com.nyuway.caillebot.commands.music;

import com.nyuway.caillebot.ICommand;
import com.nyuway.caillebot.lavaplayer.GuildMusicManager;
import com.nyuway.caillebot.lavaplayer.PlayerManager;
import com.nyuway.caillebot.lavaplayer.TrackScheduler;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public class Stop implements ICommand {
    @Override
    public String getName() {
        return "stop";
    }

    @Override
    public String getDescription() {
        return "Stop the current song";
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

        if(memberVoiceState == null || !memberVoiceState.inAudioChannel()) {
            event.reply("Vous devez être connecté dans un salon vocal pour exécuter cette commande.").setEphemeral(true).queue();
            return;
        }

        if(selfVoiceState.getChannel() != memberVoiceState.getChannel()) {
            event.reply("Vous devez être connecté dans le même salon vocal que le bot pour exécuter cette commande.").setEphemeral(true).queue();
            return;
        }

        GuildMusicManager guildMusicManager = PlayerManager.getInstance().getGuildMusicManager(event.getGuild());
        TrackScheduler trackScheduler = guildMusicManager.getTrackScheduler();
        trackScheduler.getQueue().clear();
        trackScheduler.getPlayer().stopTrack();
        event.reply("La musique a été arrêtée.").queue();


    }
}
