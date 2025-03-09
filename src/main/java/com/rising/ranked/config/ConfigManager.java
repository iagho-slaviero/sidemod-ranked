package com.rising.ranked.config;


import com.rising.ranked.models.PlayerRole;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class ConfigManager {
    private static final String CONFIG_FILE_PATH = "config/RisingBattle/config.yml";
    private static Map<String, Object> configData;
    private static List<PlayerRole> playersRole;

    public static void loadConfig() {
        File configFile = new File(CONFIG_FILE_PATH);
        if (!configFile.exists()) {
            createDefaultConfig(configFile);
        }
        try (InputStream inputStream = new FileInputStream(configFile)) {
            Yaml yaml = new Yaml();
            configData = yaml.load(inputStream);
            playersRole = getPlayersRole();
            System.out.println("[Ranqueada] Configurcao carregada com sucesso!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createDefaultConfig(File configFile) {
        try {
            configFile.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(configFile)) {
                String defaultConfig =
                        "database:\n" +
                                "  host: 'localhost'\n" +
                                "  port: 3306\n" +
                                "  name: 'ranking_rising'\n" +
                                "  user: 'root'\n" +
                                "  password: 'password'\n" +
                        "ranks:\n"+
                                "  - rankName: \"\"\n" +
                                "    points: 0\n" +
                                "    matchPoints: 0\n" +
                                "    winPoints: 0\n" +
                                "    losePoints: 0\n" +
                                "  - rankName: \"\"\n" +
                                "    points: 0\n" +
                                "    matchPoints: 0\n" +
                                "    winPoints: 0\n" +
                                "    losePoints: 0\n";
                writer.write(defaultConfig);
                System.out.println("[Ranqueada] Arquivo de configuração padrão criado em: " + configFile.getAbsolutePath());
            }
        } catch (IOException e) {
            System.out.println("[Ranqueada] Erro ao criar o arquivo de configuração padrão: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static List<PlayerRole> getPlayersRole() {
        List<Map<String, Object>> roleList = (List<Map<String, Object>>) configData.get("ranks");
        List<PlayerRole> roles = new ArrayList<>();
        if (roleList != null) {
            for (Map<String, Object> roleConfig : roleList) {
                PlayerRole role = new PlayerRole(
                        roleConfig.get("rankName").toString(),
                        ((Number) roleConfig.get("points")).intValue(),
                        ((Number) roleConfig.get("matchPoints")).intValue(),
                        ((Number) roleConfig.get("winPoints")).intValue(),
                        ((Number) roleConfig.get("losePoints")).intValue());
                roles.add(role);
                System.out.println("[Ranqueada] " + role);
            }
            return roles;
        }
        return null;
    }

    public Connection getConnection() throws SQLException {
        // Obtém os parâmetros do config
        Map<String, Object> dbConfig = (Map<String, Object>) configData.get("database");
        String host = dbConfig.get("host").toString();
        int port = ((Number) dbConfig.get("port")).intValue();
        String dbName = dbConfig.get("name").toString();
        String user = dbConfig.get("user").toString();
        String password = dbConfig.get("password").toString();

        // URL base sem especificar o nome do banco
        // Adicione &allowPublicKeyRetrieval=true
        String baseUrl = "jdbc:mysql://" + host + ":" + port
                + "/?useSSL=false&autoReconnect=true&allowPublicKeyRetrieval=true";

        // Conecta à instância MySQL para criar o banco, se necessário
        try (Connection conn = DriverManager.getConnection(baseUrl, user, password);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName);
        }

        // Agora conecta utilizando o banco de dados especificado
        // Novamente adicionamos &allowPublicKeyRetrieval=true
        String url = "jdbc:mysql://" + host + ":" + port + "/" + dbName
                + "?useSSL=false&autoReconnect=true&allowPublicKeyRetrieval=true";

        return DriverManager.getConnection(url, user, password);
    }

    public PlayerRole getPlayerRole(int points) {
        if (playersRole == null || playersRole.isEmpty()) {
            System.out.println("Ta vaziu");
            return null;
        }
        playersRole.sort(Comparator.comparingInt(PlayerRole::getPoints));

        PlayerRole result = null;
        for (PlayerRole role : playersRole) {
            if (points >= role.getPoints()) {
                result = role;
            } else {
                break;
            }
        }
        return result;
    }

    public PlayerRole getLowestRole() {
        if (playersRole == null || playersRole.isEmpty()) {
            return null;
        }
        PlayerRole lowest = playersRole.get(0);
        for (PlayerRole role : playersRole) {
            if (role.getPoints() < lowest.getPoints()) {
                lowest = role;
            }
        }
        return lowest;
    }

}
