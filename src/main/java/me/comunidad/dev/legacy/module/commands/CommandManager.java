package me.comunidad.dev.legacy.module.commands;

import me.comunidad.dev.legacy.Core;
import me.comunidad.dev.legacy.framework.Manager;
import me.comunidad.dev.legacy.framework.commands.Command;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class CommandManager extends Manager {

    private final List<Command> commands;

    public CommandManager(Core instance) {
        super(instance);
        this.commands = new ArrayList<>();

        this.load();
        this.checkCommands();

        getCommandMap().registerAll("legacy",
                commands.stream()
                        .map(Command::asBukkitCommand)
                        .collect(Collectors.toList()));
    }

    private void load() {
        Reflections reflections = new Reflections("me.comunidad.dev.legacy");
        Set<Class<? extends Command>> classes = reflections.getSubTypesOf(Command.class);

        for (Class<? extends Command> clazz : classes) {
            try {
                Command feat = clazz.getDeclaredConstructor(this.getClass()).newInstance(this);
                commands.add(feat);
            } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException exception) {
                getInstance().getLogger().warning("Failed to load the command of the class " + clazz.getName());
            }
        }
    }

    private void checkCommands() {
        List<String> disabled = getConfig().getStringList("DISABLED_COMMANDS.MAIN_COMMANDS");
        Iterator<Command> iterator = commands.iterator();

        while (iterator.hasNext()) {
            Command command = iterator.next();

            if (disabled.contains(command.getName().toLowerCase())) {
                iterator.remove();
                continue;
            }

            for (String alias : command.aliases()) {
                if (disabled.contains(alias.toLowerCase())) {
                    iterator.remove();
                    break;
                }
            }
        }
    }

    @Override
    public void reload() {
        CommandMap map = getCommandMap();

        commands.clear();
        this.load();
        this.checkCommands();

        map.registerAll("legacy",
                commands.stream()
                        .map(Command::asBukkitCommand)
                        .collect(Collectors.toList()));
    }

    private CommandMap getCommandMap() {
        try {
            Method method = Bukkit.getServer().getClass().getMethod("getCommandMap");
            return (CommandMap) method.invoke(Bukkit.getServer());
        } catch (Exception e) {
            throw new RuntimeException("Failed to access CommandMap for command registration.", e);
        }
    }
}
