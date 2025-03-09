package com.rising.ranked.models;

import net.minecraft.util.math.BlockPos;

public class SimpleBlockPos {
    private int x;
    private int y;
    private int z;

    // Construtor padrão necessário para o YAML
    public SimpleBlockPos() {
    }

    public SimpleBlockPos(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    // Getters e setters
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    // Converte para BlockPos
    public BlockPos toBlockPos() {
        return new BlockPos(x, y, z);
    }
}
