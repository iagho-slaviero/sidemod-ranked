package com.rising.ranked.config;

import com.rising.ranked.models.Arena;

import java.util.List;

public class ArenaConfigWrapper {
    private List<Arena> arenas;

    public List<Arena> getArenas() {
        return arenas;
    }

    public void setArenas(List<Arena> arenas) {
        this.arenas = arenas;
    }
}