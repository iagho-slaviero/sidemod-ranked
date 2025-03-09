package com.rising.ranked.commands;

import com.rising.ranked.Main;
import com.rising.ranked.config.DuelRulesConfigLoader;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.permission.PermissionAPI;

public class ReloadDuelRulesCommand extends CommandBase {
    @Override
    public String getName() {
        return "reloadduelrules";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/reloadduelrules";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (sender instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) sender;
            if(PermissionAPI.hasPermission(player, "risingbattle.command.reloadduelrules")){
                DuelRulesConfigLoader.loadDuelRules(Main.logger);
            }
            else{
                throw new CommandException("Você não tem permissão para usar este comando.");
            }
        }
        else{
            throw new CommandException("Você não tem permissão para usar este comando.");
        }
    }
}
