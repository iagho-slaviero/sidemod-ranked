package com.rising.ranked.commands;

import com.rising.ranked.gui.GuiMenuRanked;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.permission.PermissionAPI;

public class OpenGuiCommand extends CommandBase {
    @Override
    public String getName() {
        return "ranked";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/ranked";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        try{
            if(sender instanceof EntityPlayerMP){
                EntityPlayerMP player = (EntityPlayerMP) sender;
                if(PermissionAPI.hasPermission(player, "risingbattle.command.gui")){
                    GuiMenuRanked.openMenuRankedGUI(player);
                }
                else{
                    throw new CommandException("Você não tem permissão para usar este comando.");
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }
}
