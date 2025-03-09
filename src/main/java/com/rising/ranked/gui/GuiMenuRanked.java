package com.rising.ranked.gui;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import ca.landonjw.gooeylibs2.api.page.LinkedPage;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;
import com.rising.ranked.Main;
import com.rising.ranked.manager.QueueManager;
import com.rising.ranked.models.PlayerRanking;
import com.rising.ranked.util.ItemStackHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;

public class GuiMenuRanked {

    public static void openMenuRankedGUI(EntityPlayerMP player){
        GuiConfig config = Main.guiConfig;
        openGUI(player, config);
    }

    private static void openGUI(EntityPlayerMP player, GuiConfig config){
        ChestTemplate template = ChestTemplate.builder(3).build();

        template.set(config.getGui().getSearchMatch().getSlotItem(), GooeyButton.builder()
                .display(ItemStackHelper.fromItemDefinition(config.getGui().getSearchMatch()))
                .title(config.getGui().getSearchMatch().getItemTitle())
                .lore(Arrays.asList(config.getGui().getSearchMatch().getItemLore()))
                .onClick(action -> {
                    Optional<PlayerRanking> rankingOpt = Main.rankingList.stream()
                            .filter(pr -> pr.getUuid().equals(player.getUniqueID()))
                            .findFirst();
                    if (rankingOpt.isPresent()) {
                        PlayerRanking ranking = rankingOpt.get();
                        // Tenta entrar na fila somente se ainda não estiver em nenhum estado de espera
                        QueueManager.tryEnterQueue(ranking, player);
                    } else {
                        player.sendMessage(new TextComponentTranslation(
                                "\u00a7c[Ranqueada] \u00a77\u00BB \u00a74Seu ranking n\u00e3o foi encontrado."));
                    }
                    UIManager.closeUI(player);
                })
                .build());

        template.set(config.getGui().getCancelMatch().getSlotItem(), GooeyButton.builder()
                .display(ItemStackHelper.fromItemDefinition(config.getGui().getCancelMatch()))
                .title(config.getGui().getCancelMatch().getItemTitle())
                .lore(Arrays.asList(config.getGui().getCancelMatch().getItemLore()))
                .onClick(action -> {
                    Optional<PlayerRanking> rankingOpt = Main.rankingList.stream()
                            .filter(pr -> pr.getUuid().equals(player.getUniqueID()))
                            .findFirst();
                    QueueManager.removePlayerRanking(rankingOpt.get());
                    UIManager.closeUI(player);
                }).build());

        template.set(config.getGui().getTierList().getSlotItem(), GooeyButton.builder()
                .display(ItemStackHelper.fromItemDefinition(config.getGui().getTierList()))
                .title(config.getGui().getTierList().getItemTitle())
                .lore(Arrays.asList(config.getGui().getTierList().getItemLore()))
                .onClick((buttonAction) -> {
                    GuiRankList.openGuiRankList(buttonAction.getPlayer(), 0);
                })
                .build());

        LinkedPage page = LinkedPage.builder()
                .title(config.getGui().getTitleGui())
                .template(template)
                .build();

        UIManager.openUIForcefully(player, page);
    }
}
