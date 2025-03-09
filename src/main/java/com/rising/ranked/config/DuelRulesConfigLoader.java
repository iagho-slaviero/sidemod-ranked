package com.rising.ranked.config;

import com.rising.ranked.models.DuelRules;
import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

public class DuelRulesConfigLoader {
    private static final String CONFIG_FILE_PATH = "config/RisingBattle/configRules.yml";
    public static DuelRules duelRules;

    public static DuelRules loadDuelRules(Logger logger) {
        File configFile = new File(CONFIG_FILE_PATH);
        if (!configFile.exists()) {
            createDefaultConfig(configFile, logger);
        }
        try (FileInputStream fis = new FileInputStream(configFile)) {
            Yaml yaml = new Yaml(new Constructor(DuelRulesWrapper.class));
            DuelRulesWrapper wrapper = yaml.load(fis);
            if (wrapper != null && wrapper.getRules() != null) {
                duelRules = wrapper.getRules();
                logger.info("[Ranqueada] Regras de duelo carregadas com sucesso!");
            }
        } catch (IOException e) {
            logger.error("Erro ao ler o arquivo de configuração de regras: " + e.getMessage());
        }
        return duelRules;
    }

    private static void createDefaultConfig(File configFile, Logger logger) {
        try {
            configFile.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(configFile)) {
                String defaultConfig =
                        "rules:\n" +
                                "  minLevel: 5\n" +
                                "  maxLevel: 100\n" +
                                "  allowLegendary: false\n" +
                                "  prohibitedHeldItems:\n" +
                                "    - \"minecraft:diamond_sword\"\n" +
                                "    - \"minecraft:iron_shovel\"\n" +
                                "  maxPokemon: 3\n";
                writer.write(defaultConfig);
                logger.info("[Ranqueada] Arquivo de configuração de regras padrão criado em: " + configFile.getAbsolutePath());
            }
        } catch (IOException e) {
            logger.error("[Ranqueada] Erro ao criar o arquivo de configuração de regras padrão: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void saveDuelRules(Logger logger) {
        File configFile = new File(CONFIG_FILE_PATH);
        try (FileWriter writer = new FileWriter(configFile)) {
            DuelRulesWrapper wrapper = new DuelRulesWrapper();
            wrapper.setRules(duelRules);
            Yaml yaml = new Yaml();
            yaml.dump(wrapper, writer);
            logger.info("[Ranqueada] Regras de duelo salvas com sucesso!");
        } catch (IOException e) {
            logger.error("[Ranqueada] Erro ao salvar as regras de duelo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
