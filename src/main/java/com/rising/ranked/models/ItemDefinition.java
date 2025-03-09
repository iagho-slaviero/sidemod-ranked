package com.rising.ranked.models;

public class ItemDefinition {
    private String itemID;      // Ex: "minecraft:concrete"
    private int meta;       // Meta/dano do item
    private int slotItem;
    private String itemTitle;   // Título do item na GUI
    private String[] itemLore;  // Lore/descrição do item

    public int getSlotItem() {
        return slotItem;
    }

    public void setSlotItem(int slotItem) {
        this.slotItem = slotItem;
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public int getMeta() {
        return meta;
    }

    public void setMeta(int meta) {
        this.meta = meta;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public String[] getItemLore() {
        return itemLore;
    }

    public void setItemLore(String[] itemLore) {
        this.itemLore = itemLore;
    }
}
