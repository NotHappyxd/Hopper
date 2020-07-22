package me.happy.license.server.discord;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;

public class DiscordBot {

    public DiscordBot() {
        try {
            JDABuilder jdaBuilder = JDABuilder.createDefault("token");

            jdaBuilder.addEventListeners(new CommandListener());
            jdaBuilder.setActivity(Activity.playing("Hopper License System"));

            jdaBuilder.build();
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }
}
