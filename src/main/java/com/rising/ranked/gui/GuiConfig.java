package com.rising.ranked.gui;

public class GuiConfig {
    private GuiSection gui;
    private GuiSectionRanking guiListPlayers;

    public GuiSection getGui() {
        return gui;
    }

    public GuiSectionRanking getGuiListPlayers() {
        return guiListPlayers;
    }

    public void setGuiListPlayers(GuiSectionRanking guiListPlayers) {
        this.guiListPlayers = guiListPlayers;
    }

    public void setGui(GuiSection gui) {
        this.gui = gui;
    }
}
