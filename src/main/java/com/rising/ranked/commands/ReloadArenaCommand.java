package com.rising.ranked.commands;

import com.rising.ranked.Main;
import com.rising.ranked.config.ArenaConfigLoader;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.permission.PermissionAPI;

public class ReloadArenaCommand extends CommandBase {
    @Override
    public String getName() {
        return "reloadarena";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/reloadarena";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (sender instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) sender;
            if (PermissionAPI.hasPermission(player, "risingbattle.command.reloadarena")) {
                ArenaConfigLoader.loadArenas(Main.logger);
            }
        }
    }
}
