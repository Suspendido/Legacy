package me.comunidad.dev.legacy.module.commands.type.knockback;

import me.comunidad.dev.legacy.framework.commands.Command;
import me.comunidad.dev.legacy.module.commands.CommandManager;
import me.comunidad.dev.legacy.module.commands.type.knockback.arg.*;
import me.comunidad.dev.legacy.module.lang.Lang;
import org.bukkit.command.CommandSender;

import java.util.List;

/*
 * Copyright (c) 2026. @Comunidad, made since 17/4/2026
 * Use or redistribution of this source file is only permitted
 * if explicit permission is given by the author.
 */
public class KnockbackCommand extends Command {

    public KnockbackCommand(CommandManager manager) {
        super(manager, "knockback");
        this.setPermissible("legacy.knockback");
        
        handleArguments(
            new CreateProfileArgument(manager),
            new DeleteProfileArgument(manager),
            new ListProfilesArgument(manager),
            new SetActiveProfileArgument(manager),
            new ViewProfileArgument(manager),
            new EditProfileArgument(manager)
        );
    }

    @Override
    public List<String> aliases() {
        return List.of("kb", "kbprofiles");
    }

    @Override
    public List<String> usage() {
        return List.of(getInstance().getLangManager().of(Lang.KB_COMMAND_USAGE));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        super.execute(sender, args);
    }
}
