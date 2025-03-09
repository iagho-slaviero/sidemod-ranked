package com.rising.ranked.commands;

import com.rising.ranked.Main;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.server.permission.PermissionAPI;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class DeleteTableCommand extends CommandBase {

    @Override
    public String getName() {
        return "resetranking";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/resetranking";
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("rr");
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        Connection connection = Main.databaseConnection;
        if (connection == null) {
            sender.sendMessage(new TextComponentString("Conexão com o banco de dados não estabelecida."));
            return;
        }
        try {
            if(sender instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) sender;
                if(PermissionAPI.hasPermission(player, "risingbattle.command.deletetable")){
                    String dropSQL = "DROP TABLE IF EXISTS player_ranking";
                    try (PreparedStatement dropStmt = connection.prepareStatement(dropSQL)) {
                        dropStmt.executeUpdate();
                    }
                    String createSQL = "CREATE TABLE player_ranking (" +
                            "id VARCHAR(36) PRIMARY KEY, " +
                            "name VARCHAR(255) NOT NULL, " +
                            "ranking INT NOT NULL, " +
                            "winstreak INT NOT NULL DEFAULT 0" +
                            "loose INT NOT NULL DEFAULT 0" +
                            ")";
                    try (PreparedStatement createStmt = connection.prepareStatement(createSQL)) {
                        createStmt.executeUpdate();
                    }
                    sender.sendMessage(new TextComponentString("Tabela player_ranking reiniciada com sucesso."));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            sender.sendMessage(new TextComponentString("Erro ao reiniciar a tabela: " + e.getMessage()));
        }
    }
}
