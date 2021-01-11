package me.TechsCode.TechDiscordBot.module;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.objects.Requirement;
import me.TechsCode.TechDiscordBot.util.ConsoleColor;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class Module {

    protected TechDiscordBot bot;

    private boolean enabled;

    public Module(TechDiscordBot bot) {
        this.bot = bot;
    }

    public void enable() {
        Set<Requirement> failedRequirements = Arrays.stream(getRequirements()).filter(requirement -> !requirement.check()).collect(Collectors.toSet());
        if(failedRequirements.isEmpty()) {
            TechDiscordBot.log("Enabling Module " + getName() + "..");
            onEnable();
            enabled = true;
        } else {
            TechDiscordBot.log(ConsoleColor.YELLOW_BRIGHT + "Failed Enabling Module " + ConsoleColor.YELLOW_BOLD_BRIGHT + getName() + ConsoleColor.YELLOW_BRIGHT + " because:");
            failedRequirements.forEach(requirement -> TechDiscordBot.log(ConsoleColor.WHITE + "- " + requirement.getUnmatchMessage()));
        }
    }

    public abstract void onEnable();

    public abstract void onDisable();

    public abstract String getName();

    public abstract Requirement[] getRequirements();

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isDisabled() {
        return !enabled;
    }
}
