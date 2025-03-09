package com.rising.ranked.commands;

import com.rising.ranked.Main;
import com.rising.ranked.config.GuiConfigLoader;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.permission.PermissionAPI;

public class ReloadGuiCommand extends CommandBase {
    @Override
    public String getName() {
        return "reloadGuiRanked";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/reloadGuiRanked";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (sender != null){
            if(sender instanceof EntityPlayer){
                EntityPlayer player = (EntityPlayer) sender;
                if(PermissionAPI.hasPermission(player, "risingbattle.command.reloadgui")){
                    Main.guiConfig = GuiConfigLoader.loadGuiConfig(Main.logger);
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
}
