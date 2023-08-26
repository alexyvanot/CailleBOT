package com.nyuway.caillebot.commands.music;

import com.nyuway.caillebot.ICommand;
import com.nyuway.caillebot.lavaplayer.GuildMusicManager;
import com.nyuway.caillebot.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public class Resume implements ICommand {
    @Override
    public String getName() {
        return "resume";
    }

    @Override
    public String getDescription() {
        return "Resume the current song";
    }

    @Override
    public List<OptionData> getOptions() {
        return null;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {

        Member member = event.getMember();
        Member selfMember = event.getGuild().getSelfMember();
        GuildMusicManager guildMusicManagger = PlayerManager.getInstance().getGuildMusicManager(event.getGuild());

        if(!selfMember.getVoiceState().inAudioChannel()) {
            event.reply("Le bot doit être connecté dans un salon vocal pour exécuter cette commande.").setEphemeral(true).queue();
            return;
        }

        if(selfMember.getVoiceState().getChannel() != member.getVoiceState().getChannel()) {
            event.reply("Vous devez être connecté dans le même salon vocal que le bot pour exécuter cette commande.").setEphemeral(true).queue();
            return;
        }

        if(!guildMusicManagger.getTrackScheduler().getPlayer().isPaused()) {
            event.reply("La musique n'est pas en pause.").setEphemeral(true).queue();
            return;
        }

        if(guildMusicManagger.getTrackScheduler().getPlayer().getPlayingTrack() == null) {
            event.reply("Il n'y a pas de musique en cours de lecture.").setEphemeral(true).queue();
            return;
        }

        guildMusicManagger.getTrackScheduler().getPlayer().setPaused(false);
        event.reply("La musique a été reprise.").queue();

    }
}
