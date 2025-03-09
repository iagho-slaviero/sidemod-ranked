package com.rising.ranked.gui;

import com.rising.ranked.models.ItemDefinition;

public class GuiSectionRanking {
    private String titleGui;
    private ItemDefinition[] decorate;
    private ItemDefinition nextPage;
    private ItemDefinition prevPage;

    public String getTitleGui() {
        return titleGui;
    }

    public void setTitleGui(String titleGui) {
        this.titleGui = titleGui;
    }

    public ItemDefinition[] getDecorate() {
        return decorate;
    }

    public void setDecorate(ItemDefinition[] decorate) {
        this.decorate = decorate;
    }

    public ItemDefinition getNextPage() {
        return nextPage;
    }

    public void setNextPage(ItemDefinition nextPage) {
        this.nextPage = nextPage;
    }

    public ItemDefinition getPrevPage() {
        return prevPage;
    }

    public void setPrevPage(ItemDefinition prevPage) {
        this.prevPage = prevPage;
    }
}
