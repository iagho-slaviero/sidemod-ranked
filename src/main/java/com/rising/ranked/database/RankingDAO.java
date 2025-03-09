package com.rising.ranked.database;

import com.rising.ranked.Main;
import com.rising.ranked.models.PlayerRanking;

import java.sql.*;
import java.util.*;

public class RankingDAO {

    public static List<PlayerRanking> getOrderedPlayerIds(Connection connection) {
        try {
            List<PlayerRanking> playerRankings = new ArrayList<>();
            String sql = "SELECT id, ranking, name, winstreak, loosestreak FROM player_ranking ORDER BY ranking DESC";
            try (PreparedStatement ps = connection.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    playerRankings.add(new PlayerRanking(UUID.fromString(rs.getString("id")), rs.getInt("ranking"), rs.getString("name"), Main.configManager.getPlayerRole(rs.getInt("ranking")), rs.getInt("winstreak"), rs.getInt("loosestreak")));
                }
            }
            return playerRankings;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static PlayerRanking getPlayerRanking(Connection connection, String uuid) {
        try {
            String sql = "SELECT id, ranking, name, winstreak, loosestreak FROM player_ranking WHERE id = ?";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, uuid);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new PlayerRanking(UUID.fromString(rs.getString("id")), rs.getInt("ranking"), rs.getString("name"), Main.configManager.getPlayerRole(rs.getInt("ranking")), rs.getInt("winstreak"), rs.getInt("loosestreak"));
                    }
                }
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void addPlayerRanking(Connection connection, PlayerRanking playerRanking) {
        try{
            String sql = "INSERT INTO player_ranking(id, name, ranking, winstreak, loosestreak) VALUES(?, ?, ?, ?, ?)";
            try(PreparedStatement ps = connection.prepareStatement(sql)){
                ps.setString (1, String.valueOf(playerRanking.getUuid()));
                ps.setString (2, playerRanking.getName());
                ps.setInt (3, playerRanking.getRanking());
                ps.setInt(4, playerRanking.getWinStreak());
                ps.setInt (5, playerRanking.getLooseStreak());
                ps.executeUpdate();
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void createTableIfNotExists(Connection connection) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS player_ranking (" +
                "id VARCHAR(36) PRIMARY KEY, " +
                "name VARCHAR(255) NOT NULL, " +
                "ranking INT NOT NULL, " +
                "winstreak INT NOT NULL, " +
                "loosestreak INT NOT NULL" +
                ")";
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }


    public static void saveOrUpdatePlayerRanking(Connection connection, PlayerRanking ranking) {
        PlayerRanking existing = getPlayerRanking(connection, ranking.getUuid().toString());
        if (existing == null) {
            addPlayerRanking(connection, ranking);
        } else {
            // Se existir, atualiza os pontos e outros campos, por exemplo, winstreak
            String sql = "UPDATE player_ranking SET name = ?,ranking = ?, winstreak = ?, loosestreak = ? WHERE id = ?";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, ranking.getName());
                ps.setInt(2, ranking.getRanking());
                ps.setInt(3, ranking.getWinStreak());
                ps.setInt (4, ranking.getLooseStreak());
                ps.setString(5, ranking.getUuid().toString());
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void deletePlayerRanking(Connection connection) {
        String dropSQL = "DROP TABLE IF EXISTS player_ranking";
        try (PreparedStatement dropStmt = connection.prepareStatement(dropSQL)) {
            dropStmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

