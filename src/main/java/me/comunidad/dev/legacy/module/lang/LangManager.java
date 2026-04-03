package me.comunidad.dev.legacy.module.lang;

import lombok.Getter;
import lombok.Setter;
import me.comunidad.dev.legacy.Core;
import me.comunidad.dev.legacy.framework.Manager;
import me.comunidad.dev.legacy.utils.CC;
import me.comunidad.dev.legacy.utils.configs.ConfigYML;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (c) 2026. @Comunidad, made since 2/4/2026
 * Use or redistribution of this source file is only permitted
 * if explicit permission is given by the author.
 */
@Getter @Setter
public class LangManager extends Manager {

    public LangManager(Core instance) {
        super(instance);
        // Boot with whatever is set in config.yml → LANGUAGE.ACTIVE, default "en_lang"
        String initial = resolveActiveId();
        this.activeConfig = loadConfig(initial);
    }

    /**
     * The currently loaded language file.
     * Swapped out by reload(langId) when the admin changes language.
     */
    private ConfigYML activeConfig;

    /**
     * Resolves a Lang key with zero or more positional placeholders.
     *
     * Placeholders in the YAML value use {0}, {1}, {2} … notation.
     * E.g. "Set {0} to {1}" with args ("Horizontal", "0.35") → "Set Horizontal to 0.35"
     *
     * Color codes (&x) are translated automatically.
     */
    public String of(Lang key, Object... args) {
        String raw = activeConfig.contains(key.path)
                ? activeConfig.getUntranslatedString(key.path)
                : key.fallback;

        raw = applyArgs(raw, args);
        return CC.t(raw);
    }

    /**
     * Same as {@link #of(Lang, Object...)} but splits the result on literal "\n"
     * so multi-line lore values work without YAML list syntax.
     */
    public List<String> loreOf(Lang key, Object... args) {
        String raw = activeConfig.contains(key.path)
                ? activeConfig.getUntranslatedString(key.path)
                : key.fallback;

        raw = applyArgs(raw, args);

        List<String> lines = new ArrayList<>();
        for (String line : raw.split("\\\\n|\\n")) {
            lines.add(CC.t(line));
        }
        return lines;
    }

    /**
     * Switches the active language file and persists the choice in config.yml.
     * Calling code (LanguagesMenu) passes the langId, e.g. "es_lang".
     */
    public void reload(String langId) {
        this.activeConfig = loadConfig(langId);
        getConfig().set("LANGUAGE.ACTIVE", langId);
        getConfig().reloadCache();
        getConfig().save();
    }

    /** Returns the id of the currently active language file. */
    public String getActiveId() {
        return resolveActiveId();
    }

    // ── Internal ───────────────────────────────────────────────────────────

    /**
     * Loads (or hot-reloads) the ConfigYML for the given language id.
     * Falls back to "en_lang" if the requested file doesn't exist.
     */
    private ConfigYML loadConfig(String langId) {
        try {
            ConfigYML cfg = new ConfigYML(getInstance(), "langs" + java.io.File.separator + langId);
            cfg.reload();
            return cfg;
        } catch (Exception e) {
            // Fallback: try en_lang, then use the existing language config as last resort
            if (!"en_lang".equals(langId)) {
                return loadConfig("en_lang");
            }
            return new ConfigYML(getInstance(), "langs" + File.separator + "en_lang");
        }
    }

    private String resolveActiveId() {
        String id = getConfig().getUntranslatedString("LANGUAGE.ACTIVE");
        return (id == null || id.isBlank()) ? "en_lang" : id;
    }

    /**
     * Replaces {0}, {1}, … with the provided args.
     * Args are converted to String via toString().
     */
    private String applyArgs(String template, Object[] args) {
        if (args == null || args.length == 0) return template;
        for (int i = 0; i < args.length; i++) {
            template = template.replace("{" + i + "}", args[i] == null ? "" : args[i].toString());
        }
        return template;
    }
}