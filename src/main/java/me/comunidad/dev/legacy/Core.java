package me.comunidad.dev.legacy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.Setter;
import me.comunidad.dev.legacy.module.combat.ProfileManager;
import me.comunidad.dev.legacy.module.commands.CommandManager;
import me.comunidad.dev.legacy.framework.Manager;
import me.comunidad.dev.legacy.framework.extra.Configs;
import me.comunidad.dev.legacy.framework.menu.MenuManager;
import me.comunidad.dev.legacy.module.lang.LangManager;
import me.comunidad.dev.legacy.module.listener.ListenerManager;
import me.comunidad.dev.legacy.module.placeholder.PlaceholderHook;
import me.comunidad.dev.legacy.utils.Logger;
import me.comunidad.dev.legacy.utils.configs.ConfigYML;
import me.comunidad.dev.legacy.utils.extra.DecimalFormat;
import me.comunidad.dev.legacy.utils.extra.Cooldown;
import me.comunidad.dev.legacy.utils.prompt.PromptManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public final class Core extends JavaPlugin {

    private List<Manager> managers;
    private List<ConfigYML> configs;

    private List<Cooldown> cooldowns;
    private List<DecimalFormat> decimalFormats;

    private Configs configsObject;
    private Gson gson; // used for serialization / deserialization

    // Managers
    private PromptManager promptManager;
    private PlaceholderHook placeholderHook;
    private MenuManager menuManager;
    private ListenerManager listenerManager;
    private LangManager langManager;
    private ProfileManager profileManager;

    private boolean loaded = false;
    public String load;

    public void onEnable() {
        this.managers = new ArrayList<>();
        this.configs = new ArrayList<>();
        this.cooldowns = new ArrayList<>();
        this.decimalFormats = new ArrayList<>();

        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        (this.configsObject = new Configs()).load(this);

        this.placeholderHook = new PlaceholderHook(this);
        this.menuManager = new MenuManager(this);
        this.promptManager = new PromptManager(this);
        this.profileManager = new ProfileManager(this);
        this.listenerManager = new ListenerManager(this);
        this.langManager = new LangManager(this);

        new CommandManager(this);

        this.managers.forEach(Manager::enable);
        this.loaded = true;

        Logger.state("Enabled", managers.size());
    }


    public void onDisable() {
        this.managers.forEach(Manager::disable);
        Logger.state("Disabled", managers.size());
    }
}
