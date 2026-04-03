package me.comunidad.dev.legacy.utils.prompt;

import me.comunidad.dev.legacy.Core;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public abstract class Prompt<T> {

    private final Core main;

    public Prompt(Core main){
        this.main = main;
    }

    // The constant for the default timeout duration (20 seconds)
    private static final long DEFAULT_TIMEOUT = TimeUnit.SECONDS.toMillis(20L);

    /**
     * This method will be called when the prompt begins.
     *
     * @param player the player interacting with the prompt
     */
    public abstract void handleBegin(Player player);

    /**
     * This method will be called when the prompt is canceled.
     *
     * @param player the player whose prompt was canceled
     */
    public abstract void handleCancel(Player player);

    /**
     * This method will be called if the player fails to provide valid input.
     *
     * @param player the player who failed the input
     * @param input  the input provided by the player
     */
    public abstract void handleFailed(Player player, String input);

    /**
     * This method processes the player's input and returns a result.
     *
     * @param player the player providing the input
     * @param input  the input provided by the player
     * @return the result of processing the input
     */
    public abstract T handleInput(Player player, String input);

    /**
     * This method accepts the player's input and determines the next prompt.
     *
     * @param player the player interacting with the prompt
     * @param value  the result of the input processing
     * @return the next prompt to be shown after input is accepted
     */
    public abstract Prompt acceptInput(Player player, T value);

    /**
     * Starts the prompt with the default timeout of 20 seconds.
     *
     * @param player the player to start the prompt for
     */
    public void startPrompt(Player player) {
        startPrompt(player, DEFAULT_TIMEOUT);
    }

    /**
     * Starts the prompt with a custom timeout duration.
     *
     * @param player  the player to start the prompt for
     * @param timeout the duration in milliseconds to wait before timing out
     */
    public void startPrompt(Player player, long timeout) {
        PromptManager promptManager = main.getPromptManager();
        promptManager.startPrompt(player, this, timeout);
    }
}
