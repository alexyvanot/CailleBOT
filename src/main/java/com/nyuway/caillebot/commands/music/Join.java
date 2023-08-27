package com.nyuway.caillebot.commands.music;

import com.nyuway.caillebot.ICommand;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Join implements ICommand {
    @Override
    public String getName() {
        return "join";
    }

    @Override
    public String getDescription() {
        return "Make the bot join your voice channel";
    }

    @Override
    public List<OptionData> getOptions() {
        return null;
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {

        AudioChannel channel = event.getMember().getVoiceState().getChannel();

        if(channel == null) {
            event.reply("Vous devez être connecté dans un salon vocal pour exécuter cette commande.").setEphemeral(true).queue();
            return;
        }

        event.getGuild().getAudioManager().openAudioConnection(channel);
        event.reply("Le bot a rejoint le salon vocal.").queue();
    }
}
