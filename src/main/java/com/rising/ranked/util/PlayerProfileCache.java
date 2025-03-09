package com.rising.ranked.util;

import com.mojang.authlib.GameProfile;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerProfileCache {
    private static final Map<String, GameProfile> cache = new ConcurrentHashMap<>();

    public static GameProfile getProfile(String name) {
        return cache.get(name);
    }

    public static void putProfile(GameProfile profile) {
        if (profile != null) {
            cache.put(profile.getName(), profile);
        }
    }
}
