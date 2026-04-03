package me.comunidad.dev.legacy.framework.commands;

import lombok.Getter;
import lombok.Setter;
import me.comunidad.dev.legacy.module.commands.CommandManager;
import me.comunidad.dev.legacy.framework.Config;
import me.comunidad.dev.legacy.framework.Module;
import me.comunidad.dev.legacy.framework.commands.extra.TabCompletion;
import me.comunidad.dev.legacy.utils.CC;
import me.comunidad.dev.legacy.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@SuppressWarnings("unchecked")
public abstract class Command extends Module<CommandManager> {

    protected String name;
    protected String permissible;
    protected BukkitCommand bukkitCommand;
    protected boolean async;

    protected Map<String, Argument> arguments;
    protected List<TabCompletion> completions;
    protected List<String> usage;

    public Command(CommandManager manager, String name) {
        super(manager);
        this.name = name;

        this.permissible = null;
        this.async = false;

        this.arguments = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        this.completions = new ArrayList<>();
    }

    public abstract List<String> aliases();

    public abstract List<String> usage();

    public BukkitCommand asBukkitCommand() {
        if (bukkitCommand != null) return bukkitCommand;

        BukkitCommand command = new BukkitCommand(name) {
            @Override
            public boolean execute(@NotNull CommandSender sender, @NotNull String s, String[] args) {
                if (permissible != null && !permissible.isEmpty() && !sender.hasPermission(permissible)) {
                    sendMessage(sender, Config.INSUFFICIENT_PERM);
                    return true;
                }

                if (async) {
                    Tasks.executeAsync(getManager(), () -> Command.this.execute(sender, args));
                    return true;
                }

                Command.this.execute(sender, args);
                return true;
            }

            @Override
            public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, String[] args) throws IllegalArgumentException {
                List<String> tabComplete = Command.this.tabComplete(sender, args);
                if (tabComplete != null) return tabComplete;
                return super.tabComplete(sender, alias, args);
            }
        };

        if (!aliases().isEmpty()) command.setAliases(aliases());
        if (permissible != null) command.setPermission(permissible);

        this.bukkitCommand = command; // Cache bukkit command
        return command;
    }

    public void unregister() {
        if (name.equalsIgnoreCase("mineffects")) return;
        if (bukkitCommand == null) return;

        HandlerList.unregisterAll(this);

        for (Argument argument : arguments.values()) {
            HandlerList.unregisterAll(argument);
        }

        try {
            Object server = Bukkit.getServer();
            Method getCommandMapMethod = server.getClass().getMethod("getCommandMap");
            CommandMap commandMap = (CommandMap) getCommandMapMethod.invoke(server);

            // Remove the command from the Bukkit command map
            bukkitCommand.unregister(commandMap);

            Field knownCommandsField = commandMap.getClass().getDeclaredField("knownCommands");
            knownCommandsField.setAccessible(true);

            @SuppressWarnings("unchecked")
            Map<String, org.bukkit.command.Command> knownCommands = (Map<String, org.bukkit.command.Command>) knownCommandsField.get(commandMap);

            knownCommands.values().removeIf(cmd -> cmd.getName().equalsIgnoreCase(bukkitCommand.getName()));

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to unregister command: " + name, e);
        }
    }


    public void handleArguments(Argument... arguments) {
        for (Argument argument : arguments) {
            argument.getNames().forEach(s -> this.arguments.put(s, argument));
        }
    }

    public void handleArguments(List<Argument> arguments) {
        for (Argument argument : arguments) {
            argument.getNames().forEach(s -> this.arguments.put(s, argument));
        }
    }

    public void sendMessage(CommandSender sender, String... s) {
        for (String msg : s) {
            sender.sendMessage(CC.t(msg));
        }
    }

    public void sendUsage(CommandSender sender) {
        if (usage == null) usage = usage();

        for (String string : usage) {
            sender.sendMessage(CC.t(string));
        }
    }

    public Integer getInt(String string) {
        try {

            return Integer.parseInt(string);

        } catch (NumberFormatException e) {
            return null;
        }
    }

    public Double getDouble(String string) {
        try {

            return Double.parseDouble(string);

        } catch (NumberFormatException e) {
            return null;
        }
    }

    public Float getFloat(String string) {
        try {

            return Float.parseFloat(string);

        } catch (NumberFormatException e) {
            return null;
        }
    }

    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendUsage(sender);
            return;
        }

        String[] array = Arrays.copyOfRange(args, 1, args.length);

        if (arguments.containsKey(args[0])) {
            Argument arg = arguments.get(args[0]);

            if (permissible != null && !permissible.isEmpty() && !sender.hasPermission(permissible)) {
                sendMessage(sender, Config.INSUFFICIENT_PERM);
                return;
            }

            if (arg.permissible != null && !arg.permissible.isEmpty() && !sender.hasPermission(arg.permissible)) {
                sendMessage(sender, Config.INSUFFICIENT_PERM);
                return;
            }

            if (arg.isAsync()) {
                Tasks.executeAsync(getManager(), () -> arg.execute(sender, array));
                return;
            }

            arg.execute(sender, array);
            return;
        }

        sendUsage(sender);
    }

    public List<String> tabComplete(CommandSender sender, String[] args) {
        String string = args[args.length - 1];

        if (args.length == 1 && !arguments.isEmpty()) {
            List<String> toComplete = new ArrayList<>();

            for (Argument arg : arguments.values()) {
                if (hasPerm(sender, arg)) {
                    toComplete.addAll(arg.getNames());
                }
            }

            return toComplete
                    .stream()
                    .filter(s -> s.regionMatches(true, 0, string, 0, string.length()))
                    .collect(Collectors.toList());
        }

        if (!arguments.isEmpty()) {
            String[] array = Arrays.copyOfRange(args, 1, args.length);
            Argument arg = arguments.get(args[0]);

            if (arg == null) return null;

            List<String> tabComplete = arg.tabComplete(sender, array);

            if (hasPerm(sender, arg) && tabComplete != null && !tabComplete.isEmpty()) {
                return tabComplete;
            }
        }

        if (!completions.isEmpty()) {
            // Check permission first
            if (permissible != null && !sender.hasPermission(permissible)) return null;

            List<String> toComplete = new ArrayList<>();

            for (TabCompletion completion : completions) {
                if (completion.getArg() != args.length - 1) continue;
                if (completion.getPermission() != null && !sender.hasPermission(completion.getPermission())) continue;

                toComplete.addAll(completion.getNames());
            }

            if (!toComplete.isEmpty())
                return toComplete
                        .stream()
                        .filter(s -> s.regionMatches(true, 0, string, 0, string.length()))
                        .collect(Collectors.toList());
        }

        return null;
    }

    private boolean hasPerm(CommandSender sender, Argument arg) {
        if (arg.getPermissible() == null && permissible == null) return true; // no perm set
        if (arg.getPermissible() != null && sender.hasPermission(arg.getPermissible())) return true;
        if (permissible != null && sender.hasPermission(permissible) && arg.getPermissible() != null &&
                !sender.hasPermission(arg.getPermissible())) return false; // don't override argument perms

        return permissible != null && sender.hasPermission(permissible);
    }
}