package com.rising.ranked.gui;

import com.rising.ranked.models.ItemDefinition;

public class GuiSection {
    private String titleGui;
    private ItemDefinition searchMatch;
    private ItemDefinition cancelMatch;
    private ItemDefinition tierList;

    public String getTitleGui() {
        return titleGui;
    }

    public void setTitleGui(String title) {
        this.titleGui = title;
    }

    public ItemDefinition getSearchMatch() {
        return searchMatch;
    }
    public void setSearchMatch(ItemDefinition searchMatch) {
        this.searchMatch = searchMatch;
    }
    public ItemDefinition getCancelMatch() {
        return cancelMatch;
    }
    public void setCancelMatch(ItemDefinition cancelMatch) {
        this.cancelMatch = cancelMatch;
    }
    public ItemDefinition getTierList() {
        return tierList;
    }
    public void setTierList(ItemDefinition tierList) {
        this.tierList = tierList;
    }
}