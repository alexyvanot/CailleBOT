package com.nyuway.caillebot.commands.utilities;

import com.nyuway.caillebot.ICommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public class Ping implements ICommand {

    @Override
    public String getName() {
        return "ping";
    }

    @Override
    public String getDescription() {
        return "Get the bot's ping";
    }

    @Override
    public List<OptionData> getOptions() {
        return null;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        int ms = (int) event.getHook().getJDA().getGatewayPing();
        event.reply("Pong! (" + ms + "ms)").setEphemeral(true).queue();
    }
}
