package com.rising.ranked.models;

import java.util.UUID;

public class PlayerRanking {
    private UUID uuid;
    private int ranking;
    private String name;
    private PlayerRole role;
    private int winStreak;
    private int looseStreak;

    public PlayerRanking(UUID uuid, int ranking, String name, PlayerRole role, int winStreak, int looseStreak) {
        this.uuid = uuid;
        this.ranking = ranking;
        this.name = name;
        this.role = role;
        this.winStreak = winStreak;
        this.looseStreak = looseStreak;
    }

    public PlayerRole getRole() {
        return role;
    }

    public void setRole(PlayerRole role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public int getWinStreak() {
        return winStreak;
    }

    public void setWinStreak(int winStreak) {
        this.winStreak = winStreak;
    }

    public int getLooseStreak() {
        return looseStreak;
    }

    public void setLooseStreak(int looseStreak) {
        this.looseStreak = looseStreak;
    }
}
