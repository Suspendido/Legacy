package me.comunidad.dev.legacy.utils.configs;

import lombok.Getter;
import me.comunidad.dev.legacy.Core;
import me.comunidad.dev.legacy.utils.CC;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Getter
@SuppressWarnings("all")
public class ConfigYML extends YamlConfiguration {

    private final Map<String, Object> map;
    private final File file;
    private final Core instance;

    public ConfigYML(Core instance, String name) {
        this.file = new File(instance.getDataFolder(), name + ".yml");
        this.map = new HashMap<>();
        this.instance = instance;

        instance.getConfigs().add(this);

        if (!file.exists()) {
            instance.saveResource(name + ".yml", false);
        }

        this.reloadCache();
        this.reload();
    }


    /*
    These methods have cached variables stored for better performance.
     */

    @Override
    public double getDouble(String path) {
        Object cache = map.get(path);

        if (cache != null) {
            if (cache instanceof Double d)  return d;
            if (cache instanceof Number n)  return n.doubleValue();
            if (cache instanceof Boolean b) return b ? 1.0 : 0.0;
        }

        if (!super.contains(path)) {
            return 0;
        }

        double toCache = super.getDouble(path);
        map.put(path, toCache);
        return toCache;
    }

    @Override
    public int getInt(String path) {
        Integer cache = (Integer) map.get(path);

        if (cache != null) {
            return cache;

        } else {
            if (!super.contains(path)) {
                return 0;
            }

            Integer toCache = super.getInt(path);
            map.put(path, toCache);
            return toCache;
        }
    }

    @Override
    public long getLong(String path) {
        Long cache = (Long) map.get(path);

        if (cache != null) {
            return cache;

        } else {
            if (!super.contains(path)) {
                return 0;
            }

            Long toCache = super.getLong(path);
            map.put(path, toCache);
            return toCache;
        }
    }

    @Override
    public boolean getBoolean(String path) {
        Boolean cache = (Boolean) map.get(path);

        if (cache != null) {
            return cache;

        } else {
            if (!super.contains(path)) {
                return false;
            }

            Boolean toCache = super.getBoolean(path);
            map.put(path, toCache);
            return toCache;
        }
    }

    @Override
    public String getString(String path) {
        return getString(path, (CommandSender) null);
    }

    public String getString(String path, CommandSender sender) {
        String cache = (String) map.get(path);

        if (cache != null) {
            if (sender == null || !(sender instanceof Player)) return cache;

            return instance.getPlaceholderHook().replace((Player) sender, cache);
        } else {
            if (!super.contains(path)) {
                return CC.t("&c" + file.getName() + " » " + path);
            }

            String toCache = CC.t(super.getString(path));
            map.put(path, toCache);
            return sender != null && sender instanceof Player ? instance.getPlaceholderHook().replace((Player) sender, toCache) : toCache;
        }
    }

    public List<String> getStringList(String path, List<String> def) {
        List<String> cache = (List<String>) map.get(path);

        if (cache != null) {
            return new ArrayList<>(cache);

        } else {
            if (!super.contains(path)) {
                return def;
            }

            List<String> toCache = CC.t(super.getStringList(path));
            map.put(path, new ArrayList<>(toCache));
            return toCache;
        }
    }

    @Override
    public List<String> getStringList(String path) {
        return getStringList(path, Arrays.asList(CC.t("&c" + file.getName() + " » " + path)));
    }

    public String getUntranslatedString(String path) {
        return super.getString(path);
    }

    public List<String> getUntranslatedStringList(String path) {
        return super.getStringList(path);
    }

    public void reloadCache() {
        map.clear(); // we just clear and if it's absent we'll get it from the file again.
    }

    public void reload() {
        try {
            this.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            this.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}