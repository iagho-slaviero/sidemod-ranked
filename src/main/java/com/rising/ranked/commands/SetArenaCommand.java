package com.rising.ranked.commands;

import com.rising.ranked.Main;
import com.rising.ranked.config.ArenaConfigLoader;
import com.rising.ranked.models.Arena;
import com.rising.ranked.models.SimpleBlockPos;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.server.permission.PermissionAPI;

import java.util.Collections;
import java.util.List;

public class SetArenaCommand extends CommandBase {

    @Override
    public String getName() {
        return "setarena";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/setarena <nomeArena> <pos1|pos2>";
    }

    @Override
    public List<String> getAliases() {
        return Collections.emptyList();
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if (!(sender instanceof EntityPlayerMP)) {
            sender.sendMessage(new TextComponentString("Apenas jogadores podem usar este comando."));
            return;
        }
        if (args.length < 2) {
            sender.sendMessage(new TextComponentString("Uso: " + getUsage(sender)));
            return;
        }
        EntityPlayerMP player = (EntityPlayerMP) sender;

        if(!PermissionAPI.hasPermission(player, "risingbattle.command.setarena")){
            sender.sendMessage(new TextComponentString("Você não tem permissão para usar este comando."));
            return;
        }
        String arenaName = args[0];
        String posIdentifier = args[1].toLowerCase();

        int x = (int) player.posX;
        int y = (int) player.posY;
        int z = (int) player.posZ;
        float yaw = player.rotationYaw;
        float pitch = player.rotationPitch;

        // Carrega as arenas do arquivo para ter os dados atualizados.
        List<Arena> arenaList = ArenaConfigLoader.loadArenas(Main.logger);
        Arena arena = null;
        // Procura uma arena com o nome informado, ignorando entradas com nome nulo ou vazio.
        for (Arena a : arenaList) {
            if (a.getName() != null && !a.getName().isEmpty() && a.getName().equalsIgnoreCase(arenaName)) {
                arena = a;
                break;
            }
        }
        if (arena == null) {
            // Se a arena não existir, cria uma nova
            arena = new Arena();
            arena.setName(arenaName);
            // Define ambas as posições com a posição atual como padrão
            SimpleBlockPos pos = new SimpleBlockPos(x, y, z);
            arena.setPos1(pos);
            arena.setYaw1(yaw);
            arena.setPitch1(pitch);
            arena.setPos2(pos);
            arena.setYaw2(yaw);
            arena.setPitch2(pitch);
            arenaList.add(arena);
            player.sendMessage(new TextComponentString("Arena criada com sucesso!"));
        }
        // Atualiza a posição de acordo com o argumento informado
        if (posIdentifier.equals("pos1")) {
            SimpleBlockPos pos = new SimpleBlockPos(x, y, z);
            arena.setPos1(pos);
            arena.setYaw1(yaw);
            arena.setPitch1(pitch);
            player.sendMessage(new TextComponentString("Posição 1 da arena atualizada!"));
        } else if (posIdentifier.equals("pos2")) {
            SimpleBlockPos pos = new SimpleBlockPos(x, y, z);
            arena.setPos2(pos);
            arena.setYaw2(yaw);
            arena.setPitch2(pitch);
            player.sendMessage(new TextComponentString("Posição 2 da arena atualizada!"));
        } else {
            player.sendMessage(new TextComponentString("Argumento inválido. Use pos1 ou pos2."));
            return;
        }
        // Salva as arenas atualizadas no arquivo usando o logger de Main
        ArenaConfigLoader.saveArenas(Main.logger);
    }
}
