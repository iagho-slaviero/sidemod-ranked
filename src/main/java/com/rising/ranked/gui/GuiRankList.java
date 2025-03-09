package com.rising.ranked.gui;

import ca.landonjw.gooeylibs2.GooeyLibs;
import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import ca.landonjw.gooeylibs2.api.page.LinkedPage;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;
import com.pixelmonmod.pixelmon.storage.playerData.PlayerData;
import com.rising.ranked.Main;
import com.rising.ranked.database.RankingDAO;
import com.rising.ranked.models.ItemDefinition;
import com.rising.ranked.models.PlayerRanking;
import com.rising.ranked.util.ItemStackHelper;
import com.rising.ranked.util.PlayerProfileCache;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import java.sql.SQLException;
import java.util.*;

public class GuiRankList {
    private static int[] playersRankinSlots = {10, 12, 14, 16, 28, 30, 32, 34};

    public static void openGuiRankList(EntityPlayerMP player, int page) {
        GuiConfig config = Main.guiConfig;
        openGui(player, config, page);
    }

    private static void openGui(EntityPlayerMP player, GuiConfig config, int page) {
        ChestTemplate template = ChestTemplate.builder(6).build();
        List<PlayerRanking> players = Main.rankingList;
        int playersPerPage = playersRankinSlots.length; // 8 jogadores por página
        int startIndex = page * playersPerPage;

        for (int i = 0; i < playersPerPage; i++) {
            int slot = playersRankinSlots[i];
            int index = startIndex + i;
            if (index < players.size()) {
                PlayerRanking pd = players.get(index);
                int position = index + 1;
                template.set(slot, GooeyButton.builder()
                        .display(ItemStackHelper.createPlayerHead(PlayerProfileCache.getProfile(pd.getName())))
                        .title(pd.getName())
                        .lore(Arrays.asList(
                                "\u00a7eRanking: " + pd.getRanking(),
                                "\u00a7aPosi\u00e7\u00e3o: " + position
                        ))
                        .build());
            } else {
                template.set(slot, GooeyButton.builder()
                        .display(new ItemStack(Blocks.AIR))
                        .title("")
                        .build());
            }
        }

        template.set(49, GooeyButton.builder()
                .display(ItemStackHelper.createPlayerHead(PlayerProfileCache.getProfile(player.getName())))
                .title(player.getName())
                .lore(Arrays.asList("Posição atual no ranking: " + getPlayerRankingPosition(player.getUniqueID())))
                .build());

        if (config.getGuiListPlayers().getDecorate() != null) {
            for (ItemDefinition deco : config.getGuiListPlayers().getDecorate()) {
                int slot = deco.getSlotItem();
                template.set(slot, GooeyButton.builder()
                        .display(ItemStackHelper.fromItemDefinition(deco))
                        .title(deco.getItemTitle())
                        .lore(Arrays.asList(deco.getItemLore()))
                        .onClick(action -> {})
                        .build());
            }
        }

        if(config.getGuiListPlayers().getNextPage() != null){
            template.set(config.getGuiListPlayers().getNextPage().getSlotItem(), GooeyButton.builder()
                    .display(ItemStackHelper.fromItemDefinition(config.getGuiListPlayers().getNextPage()))
                    .title(config.getGuiListPlayers().getNextPage().getItemTitle())
                    .lore(Arrays.asList(config.getGuiListPlayers().getNextPage().getItemLore()))
                    .onClick(action -> {
                        if(startIndex + playersPerPage < players.size()){
                            int newPage = page + 1;
                            openGuiRankList(player, newPage);
                        }
                    })
                    .build()
            );
        }

        if(config.getGuiListPlayers().getPrevPage() != null){
            template.set(config.getGuiListPlayers().getPrevPage().getSlotItem(), GooeyButton.builder()
                    .display(ItemStackHelper.fromItemDefinition(config.getGuiListPlayers().getPrevPage()))
                    .title(config.getGuiListPlayers().getPrevPage().getItemTitle())
                    .lore(Arrays.asList(config.getGuiListPlayers().getPrevPage().getItemLore()))
                    .onClick(action -> {
                        if(page > 0){
                            int newPage = page - 1;
                            openGuiRankList(player, newPage);
                        }
                    })
                    .build()
            );
        }


        LinkedPage pageg = LinkedPage.builder()
                .title(config.getGuiListPlayers().getTitleGui())
                .template(template)
                .build();

        UIManager.openUIForcefully(player, pageg);
    }

    public static int getPlayerRankingPosition(UUID uuid) {
        for (int i = 0; i < Main.rankingList.size(); i++) {
            if (Main.rankingList.get(i).getUuid().equals(uuid)) {
                return i + 1; // posição: índice + 1 (ranking começa em 1)
            }
        }
        return -1; // não encontrado
    }
}
