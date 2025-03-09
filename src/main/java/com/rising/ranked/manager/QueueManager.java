package com.rising.ranked.manager;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import com.rising.ranked.Main;
import com.rising.ranked.config.DuelRulesConfigLoader;
import com.rising.ranked.models.DuelRules;
import com.rising.ranked.models.PlayerRanking;
import com.rising.ranked.models.QueueEntry;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class QueueManager {
    public static final List<QueueEntry> playersQueue = new ArrayList<>();
    public static final Map<UUID, Long> penaltyMap = new ConcurrentHashMap<>();

    public static List<QueueEntry> getPlayersQueue() {
        return playersQueue;
    }

    public static boolean putPlayerRanking(PlayerRanking playerRanking) {
        // Tenta parear o jogador imediatamente
        if (pairPlayerInQueue(playerRanking)) {
            // Se já encontrou par, não adiciona à fila
            return false;
        }
        // Se não encontrou par, adiciona à fila
        playersQueue.add(new QueueEntry(playerRanking));
        return true;
    }


    public static void removePlayerRanking(PlayerRanking playerRanking) {
        EntityPlayerMP playerMP = Main.getPlayerByUUID(playerRanking.getUuid());
        boolean removed = false;
        Iterator<QueueEntry> it = playersQueue.iterator();
        if(DuelManager.queueDuel.containsKey(playerRanking.getUuid())) {
            PlayerRanking playerRanking2 = DuelManager.queueDuel.get(playerRanking.getUuid());
            EntityPlayerMP playerMP2 = Main.getPlayerByUUID(playerRanking2.getUuid());
            DuelManager.queueDuel.remove(playerRanking.getUuid());
            DuelManager.queueDuel.remove(playerRanking2.getUuid());

            playerMP.sendMessage(new TextComponentTranslation("\u00a7c[Ranqueada] \u00a77\u00bb Voc\u00ea saiu da fila e o duelo foi cancelado."));
            playerMP2.sendMessage(new TextComponentTranslation("\u00a7c[Ranqueada] \u00a77\u00bb Seu advers\u00e1rio desistiu da fila e a partida foi cancelada."));
        }
        else {
            while (it.hasNext()) {
                QueueEntry entry = it.next();
                // Compara os UUIDs para garantir que seja o mesmo jogador
                if (entry.getPlayerRanking().getUuid().equals(playerRanking.getUuid())) {
                    it.remove();
                    removed = true;
                    if (playerMP != null) {
                        playerMP.sendMessage(new TextComponentTranslation("\u00a7c[Ranqueada] \u00a77\u00bb \u00a7cVoc\u00ea cancelou a fila."));
                    }
                    break;
                }
            }
            if (!removed && playerMP != null) {
                playerMP.sendMessage(new TextComponentTranslation("\u00a7c[Ranqueada] \u00a77\u00bb \u00a7cVoc\u00ea n\u00e3o est\u00e1 em nenhuma fila."));
            }
        }
    }

    /**
     * Procura por um par na fila para o playerRanking.
     * Se encontrar um candidato cuja diferença de ranking esteja dentro
     * do permitido por ambos, remove-o da fila, inicia o duelo e retorna true.
     * Caso contrário, retorna false.
     */
    private static boolean pairPlayerInQueue(PlayerRanking playerRanking) {
        // Se o cargo for nulo, nem tenta
        if (playerRanking.getRole() == null) {
            return false;
        }
        // Cria uma cópia para iteração segura
        List<QueueEntry> snapshot = new ArrayList<>(playersQueue);
        for (QueueEntry candidate : snapshot) {
            if (!candidate.getPlayerRanking().getUuid().equals(playerRanking.getUuid())
                    && candidate.getPlayerRanking().getRole() != null) {

                int diff = Math.abs(playerRanking.getRanking() - candidate.getPlayerRanking().getRanking());
                int allowedRangeP1 = playerRanking.getRole().getMatchPoints();
                int allowedRangeP2 = candidate.getPlayerRanking().getRole().getMatchPoints();

                if (diff <= allowedRangeP1 && diff <= allowedRangeP2) {
                    // Remove o candidato
                    playersQueue.remove(candidate);
                    // Notifica e inicia duelo
                    EntityPlayerMP p1 = Main.getPlayerByUUID(candidate.getPlayerRanking().getUuid());
                    EntityPlayerMP p2 = Main.getPlayerByUUID(playerRanking.getUuid());
                    if (p1 != null && p2 != null) {
                        p1.sendMessage(new TextComponentTranslation(
                                "\u00a7c[Ranqueada] \u00a77\u00BB \u00a7aPartida encontrada! Voc\u00ea caiu contra o " + p2.getName() + "."));
                        p2.sendMessage(new TextComponentTranslation(
                                "\u00a7c[Ranqueada] \u00a77\u00BB \u00a7aPartida encontrada! Voc\u00ea caiu contra o " + p1.getName() + "."));
                    }
                    DuelManager.startDuel(playerRanking, candidate.getPlayerRanking());
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Percorre a fila e remove os jogadores que estiverem esperando por 5 minutos ou mais.
     */
    public static void checkQueueTimeouts() {
        long now = System.currentTimeMillis();
        Iterator<QueueEntry> it = playersQueue.iterator();
        while (it.hasNext()) {
            QueueEntry entry = it.next();
            // 300.000 ms = 5 minutos
            if (now - entry.getJoinTime() >= 300_000) {
                it.remove();
                EntityPlayerMP playerMP = Main.getPlayerByUUID(entry.getPlayerRanking().getUuid());
                if (playerMP != null) {
                    playerMP.sendMessage(new TextComponentTranslation("\u00a7c[Ranqueada] \u00a77\u00bb \u00a7cVoc\u00ea foi removido da fila por tempo de espera excedido."));
                }
            }
        }
    }

    public static boolean validatePlayerForDuel(EntityPlayerMP player) {
        // Obtém as regras de duelo (supondo que Main.duelRules já tenha sido carregado)
        DuelRules rules = DuelRulesConfigLoader.duelRules;
        if (rules == null) {
            return true;
        }
        // Obtém o party do jogador (usando a API do Pixelmon)
        PlayerPartyStorage party = com.pixelmonmod.pixelmon.Pixelmon.storageManager.getParty(player);
        // Filtra apenas os Pokémon vivos e não-ovos
        List<Pokemon> pokemonList = party.findAll(pk -> pk.getHealth() > 0 && !pk.isEgg());

        // Verifica se o número de Pokémon excede o máximo permitido
        if (pokemonList.size() > rules.getMaxPokemon()) {
            player.sendMessage(new TextComponentTranslation(
                    "\u00a7c[Ranqueada] \u00a77\u00BB \u00a7cVoc\u00ea possui mais Pokemon (" + pokemonList.size() +
                            ") do que o permitido (" + rules.getMaxPokemon() + ") para duelos."));
            return false;
        }

        // Verifica o nível de cada Pokémon e se lend\u00e1rio \u00e9 permitido
        for (Pokemon p : pokemonList) {
            int level = p.getLevel();
            if (level < rules.getMinLevel() || level > rules.getMaxLevel()) {
                player.sendMessage(new TextComponentTranslation(
                        "\u00a7c[Ranqueada] \u00a77\u00BB \u00a7cO Pokemon " + p.getSpecies().name() +
                                " n\u00e3o est\u00e1 dentro do intervalo permitido (" +
                                rules.getMinLevel() + " - " + rules.getMaxLevel() + ")."));
                return false;
            }
            if (!rules.isAllowLegendary() && p.isLegendary()) {
                player.sendMessage(new TextComponentTranslation(
                        "\u00a7c[Ranqueada] \u00a77\u00BB \u00a7cPokemon lend\u00e1rio n\u00e3o \u00e9 permitido para duelos."));
                return false;
            }
            // Verifica se o Pokémon possui algum item proibido equipado
            if (p.getHeldItem() != null) {
                String heldItemID = p.getHeldItem().getItem().getRegistryName().toString();
                // Supondo que rules.getProhibitedHeldItems() retorne uma lista de Strings com os IDs proibidos
                if (rules.getProhibitedHeldItems() != null && rules.getProhibitedHeldItems().contains(heldItemID)) {
                    player.sendMessage(new TextComponentTranslation(
                            "\u00a7c[Ranqueada] \u00a77\u00BB \u00a7cO item que " + p.getSpecies().name() + " est\u00e1 segurando n\u00e3o \u00e9 permitido para duelos."));
                    return false;
                }
            }
        }
        return true;
    }

    // Verifica se o UUID está penalizado
    public static boolean isPlayerPenalized(UUID uuid) {
        Long penaltyEnd = penaltyMap.get(uuid);
        return penaltyEnd != null && System.currentTimeMillis() < penaltyEnd;
    }

    // Retorna os segundos restantes de penalidade para o UUID
    public static long getPenaltyRemaining(UUID uuid) {
        Long penaltyEnd = penaltyMap.get(uuid);
        if (penaltyEnd != null && System.currentTimeMillis() < penaltyEnd) {
            return (penaltyEnd - System.currentTimeMillis()) / 1000;
        }
        return 0;
    }

    public static boolean isAlreadyInQueue(PlayerRanking ranking) {
        return playersQueue.stream().anyMatch(qr -> qr.getPlayerRanking().getUuid().equals(ranking.getUuid()))
                || DuelManager.queueDuel.containsKey(ranking.getUuid())
                || DuelManager.playerDuel.containsKey(ranking.getUuid());
    }

    /**
     * Tenta colocar o jogador na fila.
     * Se o jogador já estiver penalizado, já estiver na fila ou não atender às regras para duelo,
     * envia as mensagens apropriadas e retorna false; caso contrário, adiciona o jogador à fila e retorna true.
     */
    public static boolean tryEnterQueue(PlayerRanking ranking, EntityPlayerMP player) {
        // 1) Verifica se o jogador está penalizado
        if (isPlayerPenalized(player.getUniqueID())) {
            long remaining = getPenaltyRemaining(player.getUniqueID());
            player.sendMessage(new TextComponentTranslation(
                    "\u00a7c[Ranqueada] \u00a77\u00BB \u00a7cVoc\u00ea est\u00e1 penalizado, aguarde " + remaining + " segundos para entrar na fila."));
            return false;
        }
        // 2) Verifica se o jogador já está na fila ou em duelo
        if (isAlreadyInQueue(ranking)) {
            player.sendMessage(new TextComponentTranslation(
                    "\u00a7c[Ranqueada] \u00a77\u00BB \u00a74Voc\u00ea j\u00e1 est\u00e1 na fila ou em duelo."));
            return false;
        }
        // 3) Verifica se o jogador atende às regras para duelos
        if (!validatePlayerForDuel(player)) {
            // A mensagem de erro já é enviada dentro de validatePlayerForDuel(...)
            return false;
        }
        // 4) Tenta colocar o jogador na fila.
        // Se for pareado imediatamente, 'putPlayerRanking' retornará false.
        boolean joinedQueue = putPlayerRanking(ranking);
        if (joinedQueue) {
            // Só exibe a mensagem se o jogador NÃO foi imediatamente pareado
            player.sendMessage(new TextComponentTranslation(
                    "\u00a7c[Ranqueada] \u00a77\u00BB \u00a7fVoc\u00ea entrou na fila ranqueada, aguarde um oponente."));
        }
        return joinedQueue;
    }


}
