package com.nyuway.caillebot.listeners;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class EventListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        User user = event.getAuthor();
        if(user.isBot()) return;
        TextChannel channel = event.getChannel().asTextChannel();
        String message = event.getMessage().getContentRaw();
        boolean isImage = event.getMessage().getAttachments().stream().anyMatch(Message.Attachment::isImage);
        boolean isGifFromURL = (message.contains("gif") && message.contains("http")) || (message.contains("http") && message.contains("tenor.com"));
        boolean isPhotoChannel = channel.getId().equals("1142148089365418094") || channel.getId().equals("1142148303841132635");
        Emoji heart = Emoji.fromUnicode("U+2764");

        if((isImage || isGifFromURL) && isPhotoChannel) {
            event.getMessage().addReaction(heart).queue();
        }

    }
}
