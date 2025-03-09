package com.rising.ranked.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class MojangAPIHelper {

    public static GameProfile fetchProfile(String username) {
        try {
            // Consulta a API para obter o UUID do jogador (o ID retornado vem sem traços)
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + username);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                return null;
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                responseBuilder.append(line);
            }
            in.close();
            JsonObject json = new JsonParser().parse(responseBuilder.toString()).getAsJsonObject();
            String id = json.get("id").getAsString();
            String name = json.get("name").getAsString();

            String formattedUUID = formatUUID(id);
            UUID uuid = UUID.fromString(formattedUUID);
            GameProfile profile = new GameProfile(uuid, name);

            URL sessionUrl = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + id );
            HttpURLConnection sessionConn = (HttpURLConnection) sessionUrl.openConnection();
            sessionConn.setRequestMethod("GET");
            int sessionResponseCode = sessionConn.getResponseCode();
            if (sessionResponseCode != 200) {
                return profile;
            }
            BufferedReader sessionIn = new BufferedReader(new InputStreamReader(sessionConn.getInputStream()));
            StringBuilder sessionResponse = new StringBuilder();
            while ((line = sessionIn.readLine()) != null) {
                sessionResponse.append(line);
            }
            sessionIn.close();
            JsonObject sessionJson = new JsonParser().parse(sessionResponse.toString()).getAsJsonObject();
            if (sessionJson.has("properties")) {
                JsonArray properties = sessionJson.getAsJsonArray("properties");
                PropertyMap propertyMap = profile.getProperties();
                for (int i = 0; i < properties.size(); i++) {
                    JsonObject propertyObj = properties.get(i).getAsJsonObject();
                    String propName = propertyObj.get("name").getAsString();
                    String propValue = propertyObj.get("value").getAsString();
                    String propSignature = propertyObj.has("signature") ? propertyObj.get("signature").getAsString() : null;
                    propertyMap.put(propName, new Property(propName, propValue, propSignature));
                }
            }
            return profile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String formatUUID(String id) {
        if (id.length() != 32) {
            throw new IllegalArgumentException("UUID string must have 32 hex digits, got: " + id);
        }
        return id.substring(0, 8) + "-" +
                id.substring(8, 12) + "-" +
                id.substring(12, 16) + "-" +
                id.substring(16, 20) + "-" +
                id.substring(20);
    }
}
