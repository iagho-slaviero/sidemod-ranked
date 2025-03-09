package com.rising.ranked.commands;

import com.rising.ranked.Main;
import com.rising.ranked.models.PlayerRanking;
import com.rising.ranked.models.PlayerRole;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.server.permission.PermissionAPI;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class SetPointsCommand extends CommandBase {

    @Override
    public String getName() {
        return "setpoints";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/setpoints <playerName> <points>";
    }

    @Override
    public List<String> getAliases() {
        return Collections.emptyList();
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(sender instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) sender;
            if(PermissionAPI.hasPermission(player, "risingbattle.command.setpoints")) {
                if (args.length < 2) {
                    sender.sendMessage(new TextComponentString("Uso: " + getUsage(sender)));
                    return;
                }

                String targetName = args[0];
                int newPoints;
                try {
                    newPoints = Integer.parseInt(args[1]);
                } catch (NumberFormatException ex) {
                    sender.sendMessage(new TextComponentString("Pontos inválidos."));
                    return;
                }

                // Procura o ranking do jogador na lista
                Optional<PlayerRanking> rankingOpt = Main.rankingList.stream()
                        .filter(pr -> pr.getName().equalsIgnoreCase(targetName))
                        .findFirst();

                if (!rankingOpt.isPresent()) {
                    sender.sendMessage(new TextComponentString("Jogador " + targetName + " não encontrado."));
                    return;
                }

                PlayerRanking ranking = rankingOpt.get();
                int oldPoints = ranking.getRanking();
                ranking.setRanking(newPoints);

                // Obtém o cargo adequado para o novo valor de pontos
                PlayerRole newRole = Main.configManager.getPlayerRole(newPoints);
                if (newRole == null) {
                    // Caso não haja um cargo específico para esse valor, use o cargo mais baixo
                    newRole = Main.configManager.getLowestRole();
                }
                PlayerRole oldRole = ranking.getRole();

                // Se o cargo mudou, atualiza via comandos LuckPerms
                if (oldRole == null || !oldRole.getNameRole().equalsIgnoreCase(newRole.getNameRole())) {
                    ranking.setRole(newRole);
                    String playerName = targetName; // Usamos o nome do jogador para os comandos LuckPerms

                    // Remove o cargo antigo, se existir
                    if (oldRole != null && !oldRole.getNameRole().isEmpty()) {
                        String removeCmd = String.format("lp user %s parent remove %s", playerName, oldRole.getNameRole());
                        server.getCommandManager().executeCommand(server, removeCmd);
                    }
                    // Adiciona o novo cargo
                    String addCmd = String.format("lp user %s parent add %s", playerName, newRole.getNameRole());
                    server.getCommandManager().executeCommand(server, addCmd);
                }

                sender.sendMessage(new TextComponentString(
                        "Pontos de " + targetName + " atualizados de " + oldPoints + " para " + newPoints));
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
