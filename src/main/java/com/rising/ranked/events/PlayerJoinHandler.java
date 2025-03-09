package com.rising.ranked.events;

import com.rising.ranked.Main;
import com.rising.ranked.database.RankingDAO;
import com.rising.ranked.manager.DuelManager;
import com.rising.ranked.manager.QueueManager;
import com.rising.ranked.models.PlayerRanking;
import com.rising.ranked.models.PlayerRole;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

import java.util.UUID;

public class PlayerJoinHandler {

    public PlayerJoinHandler(){
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerLoggedInEvent event) {
        if (event.player instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) event.player;
            UUID uuid = player.getUniqueID();

            if (Main.rankingList.stream().anyMatch(pr -> pr.getUuid().equals(uuid))) {
                return;
            }
                try {
                    // Obtém o cargo com o menor valor de pontos (ou seja, o cargo mais baixo)
                    PlayerRole lowestRole = Main.configManager.getLowestRole();
                    if (lowestRole == null) {
                        // Opcional: se não houver cargos configurados, cria um cargo padrão
                        lowestRole = new PlayerRole("novato", 0, 500, 10, 10);
                    }
                    PlayerRanking ranking = new PlayerRanking(uuid, 0, player.getName(), lowestRole, 0, 0);
                    MinecraftServer server = event.player.getServer();
                    String setCmd = String.format("lp user %s parent add %s", player.getName(), lowestRole.getNameRole());
                    server.getCommandManager().executeCommand(server, setCmd);
                    Main.rankingList.add(ranking);
                    Main.fetchPlayerJoin(player.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (!(event.player instanceof EntityPlayerMP)) {
            return;
        }
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        UUID quitterUuid = player.getUniqueID();

        // Verifica se o jogador estava em um duelo (usando o mapa de duelos)
        if (DuelManager.playerDuel.containsKey(quitterUuid)) {
            // Obtém o ranking do oponente a partir do mapa de duelos
            PlayerRanking opponentRanking = DuelManager.playerDuel.get(quitterUuid);
            EntityPlayerMP opponent = Main.getPlayerByUUID(opponentRanking.getUuid());

            // Mensagens:
            if (opponent != null) {
                opponent.sendMessage(new TextComponentTranslation(
                        "\u00a7c[Ranqueada] \u00a77\u00BB \u00a7cSeu inimigo desistiu e voc\u00ea n\u00e3o ganhou pontos."));
            }
            player.sendMessage(new TextComponentTranslation(
                    "\u00a7c[Ranqueada] \u00a77\u00BB \u00a7cVoc\u00ea desistiu e perdeu 10 pontos, al\u00e9m de uma penalidade."));

            // Atualiza os pontos do jogador que saiu (desistiu)
            Main.rankingList.stream()
                    .filter(pr -> pr.getUuid().equals(quitterUuid))
                    .findFirst()
                    .ifPresent(pr -> pr.setRanking(pr.getRanking() - 10));

            // Registra a penalidade (30 minutos = 30 * 60 * 1000 ms)
            long penaltyUntil = System.currentTimeMillis() + (30 * 60 * 1000);
            QueueManager.penaltyMap.put(quitterUuid, penaltyUntil);

            // Remove os jogadores dos mapas de duelo e arena
            DuelManager.playerDuel.remove(quitterUuid);
            if (opponent != null) {
                DuelManager.playerDuel.remove(opponent.getUniqueID());
                DuelManager.busyArena.remove(opponent.getUniqueID());
            }
            DuelManager.busyArena.remove(quitterUuid);
        }
    }
}
