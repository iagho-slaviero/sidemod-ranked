package com.rising.ranked.models;

public class QueueEntry {
    private final PlayerRanking playerRanking;
    private final long joinTime;

    public QueueEntry(PlayerRanking playerRanking) {
        this.playerRanking = playerRanking;
        this.joinTime = System.currentTimeMillis();
    }

    public PlayerRanking getPlayerRanking() {
        return playerRanking;
    }

    public long getJoinTime() {
        return joinTime;
    }
}
