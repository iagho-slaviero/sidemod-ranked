package com.rising.ranked.config;

import com.rising.ranked.models.Arena;
import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.Yaml;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ArenaConfigLoader {
    private static final String CONFIG_FILE_PATH = "config/RisingBattle/configArena.yml";
    public static List<Arena> arenaList;

    public static List<Arena> loadArenas(Logger logger) {
        File configFile = new File(CONFIG_FILE_PATH);
        if (!configFile.exists()) {
            createDefaultConfig(configFile, logger);
        }
        try (java.io.InputStream inputStream = new java.io.FileInputStream(configFile)) {
            Yaml yaml = new Yaml(new org.yaml.snakeyaml.constructor.Constructor(ArenaConfigWrapper.class));
            ArenaConfigWrapper wrapper = yaml.load(inputStream);
            if (wrapper != null && wrapper.getArenas() != null) {
                arenaList = wrapper.getArenas();
            }
            logger.info("[Ranqueada] Configuração de arenas carregada com sucesso!");
        } catch (IOException e) {
            logger.error("Erro ao ler o arquivo de configuração de arenas: " + e.getMessage());
        }
        return arenaList;
    }

    private static void createDefaultConfig(File configFile, Logger logger) {
        try {
            configFile.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(configFile)) {
                String defaultConfig =
                        "arenas:\n" +
                                "  - name: \"Arena1\"\n" +
                                "    pos1:\n" +
                                "      x: 0\n" +
                                "      y: 0\n" +
                                "      z: 0\n" +
                                "    yaw1: 0\n" +
                                "    pitch1: 0\n" +
                                "    pos2:\n" +
                                "      x: 0\n" +
                                "      y: 0\n" +
                                "      z: 0\n" +
                                "    yaw2: 0\n" +
                                "    pitch2: 0\n";
                writer.write(defaultConfig);
                logger.info("[Ranqueada] Arquivo de configuração de arenas padrão criado em: " + configFile.getAbsolutePath());
            }
        } catch (IOException e) {
            logger.error("[Ranqueada] Erro ao criar o arquivo de configuração de arenas padrão: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void saveArenas(Logger logger) {
        File configFile = new File(CONFIG_FILE_PATH);
        try (FileWriter writer = new FileWriter(configFile)) {
            ArenaConfigWrapper wrapper = new ArenaConfigWrapper();
            wrapper.setArenas(arenaList);
            Yaml yaml = new Yaml();
            yaml.dump(wrapper, writer);
            logger.info("[Ranqueada] Arenas salvas com sucesso!");
        } catch (IOException e) {
            logger.error("[Ranqueada] Erro ao salvar as arenas: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
