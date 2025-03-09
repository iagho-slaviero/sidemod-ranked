package com.rising.ranked.models;

import net.minecraft.util.math.BlockPos;

public class Arena {
    private String name;
    private SimpleBlockPos pos1;
    private float yaw1;
    private float pitch1;
    private SimpleBlockPos pos2;
    private float yaw2;
    private float pitch2;

    // Construtor padrão para o YAML
    public Arena() {
    }

    public Arena(String name, SimpleBlockPos pos1, float yaw1, float pitch1, SimpleBlockPos pos2, float yaw2, float pitch2) {
        this.name = name;
        this.pos1 = pos1;
        this.yaw1 = yaw1;
        this.pitch1 = pitch1;
        this.pos2 = pos2;
        this.yaw2 = yaw2;
        this.pitch2 = pitch2;
    }

    // Getters e setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SimpleBlockPos getPos1() {
        return pos1;
    }

    public void setPos1(SimpleBlockPos pos1) {
        this.pos1 = pos1;
    }

    public float getYaw1() {
        return yaw1;
    }

    public void setYaw1(float yaw1) {
        this.yaw1 = yaw1;
    }

    public float getPitch1() {
        return pitch1;
    }

    public void setPitch1(float pitch1) {
        this.pitch1 = pitch1;
    }

    public SimpleBlockPos getPos2() {
        return pos2;
    }

    public void setPos2(SimpleBlockPos pos2) {
        this.pos2 = pos2;
    }

    public float getYaw2() {
        return yaw2;
    }

    public void setYaw2(float yaw2) {
        this.yaw2 = yaw2;
    }

    public float getPitch2() {
        return pitch2;
    }

    public void setPitch2(float pitch2) {
        this.pitch2 = pitch2;
    }

    // Métodos auxiliares para converter para BlockPos
    public BlockPos getBlockPos1() {
        return pos1.toBlockPos();
    }

    public BlockPos getBlockPos2() {
        return pos2.toBlockPos();
    }
}
