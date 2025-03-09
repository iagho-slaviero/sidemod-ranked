package com.rising.ranked.manager;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.battles.BattleRegistry;
import com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import com.rising.ranked.Main;
import com.rising.ranked.models.Arena;
import com.rising.ranked.models.PlayerRanking;
import com.rising.ranked.config.ArenaConfigLoader;
import com.rising.ranked.models.QueueEntry;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.rising.ranked.manager.QueueManager.*;

public class DuelManager {
    public static Map<UUID, PlayerRanking> queueDuel = new HashMap<>();
    public static Map<UUID, PlayerRanking> playerDuel = new HashMap<>();
    public static Map<UUID, Arena> busyArena = new HashMap<>();

    // Cria um executor para checar a fila periodicamente
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public static void startQueueChecker() {
        scheduler.scheduleAtFixedRate(() -> checkQueue(), 0, 60, TimeUnit.SECONDS);
    }

    public static void checkQueue() {
        if (!ArenaConfigLoader.arenaList.isEmpty() && queueDuel.size() >= 2) {
            List<PlayerRanking> waiting = new ArrayList<>(queueDuel.values());
            if (waiting.size() >= 2) {
                PlayerRanking pr1 = waiting.get(0);
                PlayerRanking pr2 = waiting.get(1);
                queueDuel.remove(pr1.getUuid());
                queueDuel.remove(pr2.getUuid());
                MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
                server.addScheduledTask(() -> {
                    startDuel(pr1, pr2);
                });
            }
        }
    }


    public static void startDuel(PlayerRanking player1, PlayerRanking player2) {
        EntityPlayerMP playerMP = Main.getPlayerByUUID(player1.getUuid());
        EntityPlayerMP playerMP2 = Main.getPlayerByUUID(player2.getUuid());
        if (playerMP == null || playerMP2 == null) {
            return;
        }
        if (!ArenaConfigLoader.arenaList.isEmpty()) {
            Arena arena = ArenaConfigLoader.arenaList.remove(0);
            busyArena.put(player1.getUuid(), arena);

            MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();

            // Comandos de cura e teleport para o spawn
            server.commandManager.executeCommand(server, "pokeheal " + playerMP.getName());
            server.commandManager.executeCommand(server, "pokeheal " + playerMP2.getName());
            server.commandManager.executeCommand(server, "mv tp " + playerMP.getName() + " spawn");
            server.commandManager.executeCommand(server, "mv tp " + playerMP2.getName() + " spawn");

            // Teleporta os jogadores para as posições da arena
            playerMP.setPositionAndRotation(arena.getPos1().getX(), arena.getPos1().getY(), arena.getPos1().getZ(), arena.getYaw1(), arena.getPitch1());
            playerMP2.setPositionAndRotation(arena.getPos2().getX(), arena.getPos2().getY(), arena.getPos2().getZ(), arena.getYaw2(), arena.getPitch2());

            playerDuel.put(player1.getUuid(), player2);
            playerDuel.put(player2.getUuid(), player1);

            // Cria uma thread separada para esperar alguns segundos antes de iniciar a batalha
            new Thread(() -> {
                try {
                    Thread.sleep(1000); // Aguarda 2 segundos
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // Agendamos a tarefa na thread principal do servidor para iniciar a batalha
                server.addScheduledTask(() -> {
                    BattleParticipant participant1 = DuelManager.prepareParticipant(playerMP, 1);
                    BattleParticipant participant2 = DuelManager.prepareParticipant(playerMP2, 1);
                    BattleRegistry.startBattle(participant1, participant2);
                });
            }).start();
        }
        else {
            String msgArenaOcupada = "\u00a7c[Ranqueada] \u00a77\u00BB A batalha foi encontrada, mas n\u00e3o tem arena dispon\u00edvel, aguarde um pouco que voc\u00ea ser\u00e1 teleportado em breve.";
            playerMP.sendMessage(new TextComponentTranslation(msgArenaOcupada));
            playerMP2.sendMessage(new TextComponentTranslation(msgArenaOcupada));
            putPlayerPair(player1, player2);
            putPlayerPair(player2, player1);
        }
    }


    private static BattleParticipant prepareParticipant(EntityPlayerMP player, int numPokemon) {
        PlayerPartyStorage party = Pixelmon.storageManager.getParty(player);
        List<Pokemon> pokemonList = party.findAll(pk -> pk.getHealth() > 0 && !pk.isEgg());
        EntityPixelmon[] pixelmons = new EntityPixelmon[numPokemon];
        for (int i = 0; i < numPokemon && i < pokemonList.size(); ++i) {
            pixelmons[i] = ((Pokemon) pokemonList.get(i)).getOrSpawnPixelmon(player);
        }
        return new PlayerParticipant(player, pixelmons);
    }

    public static void putPlayerPair(PlayerRanking player1, PlayerRanking player2) {
        queueDuel.put(player2.getUuid(), player1);
        queueDuel.put(player1.getUuid(), player2);
    }

}
