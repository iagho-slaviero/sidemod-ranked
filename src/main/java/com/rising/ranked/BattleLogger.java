package com.rising.ranked;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BattleLogger {

    private static File logFile = null;
    private static String currentDate = null;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * Recupera (ou cria) o arquivo de log com base na data atual.
     */
    private static File getLogFile() {
        String today = LocalDate.now().format(DATE_FORMAT);
        // Se o arquivo ainda não foi criado ou a data mudou, cria um novo arquivo
        if (logFile == null || !today.equals(currentDate)) {
            currentDate = today;
            logFile = new File("config/RisingBattle/logs/battles_" + today + ".log");
            try {
                // Cria o diretório se necessário e o arquivo se ele ainda não existir
                if (!logFile.exists()) {
                    logFile.getParentFile().mkdirs();
                    logFile.createNewFile();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return logFile;
    }

    /**
     * Registra uma mensagem de batalha no arquivo de log, criando-o se necessário.
     * Se a mensagem estiver vazia, nada é feito.
     */
    public static synchronized void logBattle(String message) {
        if (message == null || message.trim().isEmpty()) {
            return;
        }
        File file = getLogFile();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            String timestamp = LocalDateTime.now().format(TIME_FORMAT);
            writer.write("[" + timestamp + "] " + message);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
