package com.rising.ranked.models;

import java.util.List;

public class DuelRules {
    private int minLevel;
    private int maxLevel;
    private List<String> prohibitedHeldItems;
    private boolean allowLegendary;
    private int maxPokemon;

    // Construtor padrão necessário para o YAML
    public DuelRules() {}

    // Getters e Setters
    public int getMinLevel() {
        return minLevel;
    }

    public void setMinLevel(int minLevel) {
        this.minLevel = minLevel;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public boolean isAllowLegendary() {
        return allowLegendary;
    }

    public void setAllowLegendary(boolean allowLegendary) {
        this.allowLegendary = allowLegendary;
    }

    public List<String> getProhibitedHeldItems() {
        return prohibitedHeldItems;
    }

    public void setProhibitedHeldItems(List<String> prohibitedHeldItems) {
        this.prohibitedHeldItems = prohibitedHeldItems;
    }

    public int getMaxPokemon() {
        return maxPokemon;
    }

    public void setMaxPokemon(int maxPokemon) {
        this.maxPokemon = maxPokemon;
    }
}
