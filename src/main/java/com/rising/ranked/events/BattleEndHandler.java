package com.rising.ranked.events;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.events.battles.BattleEndEvent;
import com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant;
import com.pixelmonmod.pixelmon.enums.battle.BattleResults;
import com.rising.ranked.BattleLogger;
import com.rising.ranked.Main;
import com.rising.ranked.config.ArenaConfigLoader;
import com.rising.ranked.manager.DuelManager;
import com.rising.ranked.models.Arena;
import com.rising.ranked.models.PlayerRanking;
import com.rising.ranked.models.PlayerRole;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Map;
import java.util.UUID;

public class BattleEndHandler {

    public BattleEndHandler() {
        Pixelmon.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onBattleEnd(BattleEndEvent event) {
        EntityPlayerMP winner = null;
        EntityPlayerMP loser = null;

        for (Map.Entry<BattleParticipant, BattleResults> entry : event.results.entrySet()) {
            BattleParticipant participant = entry.getKey();
            BattleResults result = entry.getValue();
            if (participant instanceof PlayerParticipant) {
                EntityPlayerMP player = ((PlayerParticipant) participant).player;
                if (result == BattleResults.VICTORY) {
                    winner = player;
                } else if (result == BattleResults.DEFEAT) {
                    loser = player;
                }
            }
        }

        if (winner != null && loser != null) {
            UUID winnerUuid = winner.getUniqueID();
            UUID loserUuid = loser.getUniqueID();
            if (DuelManager.playerDuel.containsKey(winnerUuid) && DuelManager.playerDuel.containsKey(loserUuid)) {

                updateBattlePointsAndRole(winner, true);
                updateBattlePointsAndRole(loser, false);

                DuelManager.playerDuel.remove(winnerUuid);
                DuelManager.playerDuel.remove(loserUuid);

                Arena arena = DuelManager.busyArena.get(winnerUuid);
                if (arena != null) {
                    DuelManager.busyArena.remove(winnerUuid);
                    ArenaConfigLoader.arenaList.add(arena);
                } else {
                    arena = DuelManager.busyArena.get(loserUuid);
                    if (arena != null) {
                        DuelManager.busyArena.remove(loserUuid);
                        ArenaConfigLoader.arenaList.add(arena);
                    }
                }

                // Cria um atraso de 1 segundo antes de teleportar os jogadores para o spawn
                MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
                EntityPlayerMP finalWinner = winner;
                EntityPlayerMP finalLoser = loser;
                new Thread(() -> {
                    try {
                        Thread.sleep(1000); // 1 segundo de atraso
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    server.addScheduledTask(() -> {
                        server.commandManager.executeCommand(server, "spawn " + finalWinner.getName());
                        server.commandManager.executeCommand(server, "spawn " + finalLoser.getName());
                    });
                }).start();

                String logMsg = String.format(
                        "Batalha encerrada: %s (VICTORY) %s (DEFEAT)",
                        winner.getName(), loser.getName()
                );
                BattleLogger.logBattle("[" + java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")) + "] " + logMsg);
            }
        }
    }


    private void updateBattlePointsAndRole(EntityPlayerMP player, boolean isWinner) {
        UUID uuid = player.getUniqueID();
        boolean found = false;
        for (PlayerRanking pr : Main.rankingList) {
            if (pr.getUuid().equals(uuid)) {
                found = true;
                int oldPoints = pr.getRanking();
                int delta;
                if (isWinner) {
                    int currentStreak = pr.getWinStreak();
                    // Incrementa a sequência de vitórias (máximo 10)
                    if (currentStreak < 10) {
                        currentStreak++;
                        pr.setWinStreak(currentStreak);
                    }
                    int bonus = currentStreak * 2;
                    delta = (pr.getRole() != null ? pr.getRole().getWinPoints() : 20) + bonus;
                    int newPoints = oldPoints + delta;
                    pr.setRanking(newPoints);
                    player.sendMessage(new TextComponentTranslation(
                            "\u00a7c[Ranqueada] \u00a77\u00BB \u00a7aVoc\u00ea venceu o seu oponente e ganhou " + delta +
                                    " pontos. Foi para " + newPoints + ", sua Sequ\u00eancia de Vit\u00f3rias: " + currentStreak + "!"));
                } else {
                    delta = pr.getRole() != null ? pr.getRole().getLosePoints() : 10;
                    int looseStreak = pr.getLooseStreak();
                    // Incrementa a sequência de derrotas (máximo 10)
                    if (looseStreak < 10) {
                        looseStreak++;
                        pr.setLooseStreak(looseStreak);
                    }
                    int bonus = looseStreak * 2;
                    int newPoints = Math.max(0, oldPoints - (delta + bonus));
                    pr.setRanking(newPoints);
                    player.sendMessage(new TextComponentTranslation(
                            "\u00a7c[Ranqueada] \u00a77\u00BB \u00a7cVoc\u00ea perdeu contra o seu oponente e perdeu " + (delta + bonus) +
                                    " pontos. Foi para " + newPoints + ", sua Sequ\u00eancia de Derrotas: " + looseStreak + "!"));
                }
                // Verifica se o cargo deve ser alterado
                PlayerRole currentRole = pr.getRole();
                PlayerRole newRole = Main.configManager.getPlayerRole(pr.getRanking());
                if (newRole == null) {
                    newRole = Main.configManager.getLowestRole();
                }
                if (newRole != null && (currentRole == null || !currentRole.getNameRole().equalsIgnoreCase(newRole.getNameRole()))) {
                    pr.setRole(newRole);
                    String playerName = player.getName();
                    MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
                    if (server != null) {
                        // Remove o cargo antigo, se houver
                        if (currentRole != null && !currentRole.getNameRole().isEmpty()) {
                            String removeCmd = String.format("lp user %s parent remove %s", playerName, currentRole.getNameRole());
                            server.getCommandManager().executeCommand(server, removeCmd);
                        }
                        // Adiciona o novo cargo
                        String addCmd = String.format("lp user %s parent add %s", playerName, newRole.getNameRole());
                        server.getCommandManager().executeCommand(server, addCmd);
                    }
                    if (currentRole == null || currentRole.getPoints() < newRole.getPoints()) {
                        player.sendMessage(new TextComponentTranslation(
                                "\u00a7c[Ranqueada] \u00a77\u00BB \u00a7eParab\u00e9ns, voc\u00ea subiu para o rank " + newRole.getNameRole() + "."));
                    } else {
                        player.sendMessage(new TextComponentTranslation(
                                "\u00a7c[Ranqueada] \u00a77\u00BB \u00a74Voc\u00ea acabou caindo de rank para o rank " + newRole.getNameRole() + "."));
                    }
                }
                break;
            }
        }
        if (!found) {
            player.sendMessage(new TextComponentTranslation("\u00a7c[Ranqueada] \u00a77\u00BB \u00a7cSeu ranking não foi encontrado na lista."));
        }
    }

}
