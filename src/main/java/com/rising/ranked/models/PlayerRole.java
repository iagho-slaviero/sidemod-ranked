package com.rising.ranked.models;

public class PlayerRole {
    private String nameRole;
    private int points;
    private int matchPoints;
    private int winPoints;
    private int losePoints;

    public PlayerRole(String nameRole, int points, int matchPoints, int winPoints, int losePoints) {
        this.nameRole = nameRole;
        this.points = points;
        this.matchPoints = matchPoints;
        this.winPoints = winPoints;
        this.losePoints = losePoints;
    }

    public String getNameRole() {
        return nameRole;
    }

    public void setNameRole(String nameRole) {
        this.nameRole = nameRole;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getMatchPoints() {
        return matchPoints;
    }

    public void setMatchPoints(int matchPoints) {
        this.matchPoints = matchPoints;
    }

    public int getWinPoints() {
        return winPoints;
    }

    public void setWinPoints(int winPoints) {
        this.winPoints = winPoints;
    }

    public int getLosePoints() {
        return losePoints;
    }

    public void setLosePoints(int losePoints) {
        this.losePoints = losePoints;
    }
}
