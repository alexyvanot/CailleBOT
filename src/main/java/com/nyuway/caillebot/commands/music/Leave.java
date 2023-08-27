package com.nyuway.caillebot.commands.music;

import com.nyuway.caillebot.ICommand;
import com.nyuway.caillebot.lavaplayer.GuildMusicManager;
import com.nyuway.caillebot.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Leave implements ICommand {
    @Override
    public String getName() {
        return "leave";
    }

    @Override
    public String getDescription() {
        return "Make the bot leave his current voice channel";
    }

    @Override
    public List<OptionData> getOptions() {
        return null;
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {

        Member member = event.getMember();
        Member selfMember = event.getGuild().getSelfMember();

        if(!selfMember.getVoiceState().inAudioChannel()) {
            event.reply("Le bot doit être connecté dans un salon vocal pour exécuter cette commande.").setEphemeral(true).queue();
            return;
        }

        if(selfMember.getVoiceState().getChannel() != member.getVoiceState().getChannel()) {
            event.reply("Vous devez être connecté dans le même salon vocal que le bot pour exécuter cette commande.").setEphemeral(true).queue();
            return;
        }

        GuildMusicManager guildMusicManager = PlayerManager.getInstance().getGuildMusicManager(event.getGuild());

        if(!guildMusicManager.getTrackScheduler().getQueue().isEmpty()) {
            guildMusicManager.getTrackScheduler().getQueue().clear();
        }

        if(guildMusicManager.getTrackScheduler().getPlayer().getPlayingTrack() != null) {
            guildMusicManager.getTrackScheduler().getPlayer().stopTrack();
        }

        event.getGuild().getAudioManager().closeAudioConnection();
        event.reply("Le bot a quitté le salon vocal.").queue();

    }
}
