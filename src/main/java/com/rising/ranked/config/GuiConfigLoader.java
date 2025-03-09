package com.rising.ranked.config;


import com.rising.ranked.gui.GuiConfig;
import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.*;

public class GuiConfigLoader {
    private static final String CONFIG_FILE_PATH = "config/RisingBattle/configGui.yml";

    public static GuiConfig loadGuiConfig(Logger logger) {
        File configFile = new File(CONFIG_FILE_PATH);
        if (!configFile.exists()) {
            createDefaultConfig(configFile);
        }
        try (InputStream inputStream = new FileInputStream(configFile)) {
            Yaml yaml = new Yaml(new Constructor(GuiConfig.class));
            GuiConfig config = yaml.load(inputStream);
            return config;
        } catch (IOException e) {
            logger.error("Erro ao ler o arquivo de configuração da GUI: " + e.getMessage());
        }
        return null;
    }

    private static void createDefaultConfig(File configFile) {
        try {
            configFile.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(configFile)) {
                String defaultConfig =
                        "   gui:\n" +
                        "       titleGui: \"Rising Ranked\"\n" +
                        "       searchMatch:\n" +
                        "           itemID: \"minecraft:emerald\"\n" +
                        "           meta: 0\n" +
                        "           slotItem: 10\n" +
                        "           itemTitle: \"Entrar na fila\"\n" +
                        "           itemLore: [\"Clique para buscar partida\"]\n" +
                        "       cancelMatch:\n"+
                        "           itemID: \"minecraft:barrier\"\n" +
                        "           meta: 0\n" +
                        "           slotItem: 12\n" +
                        "           itemTitle: \"Sair da fila\"\n" +
                        "           itemLore: [\"Clique para sair da file\"]\n" +
                        "       tierList:\n" +
                        "           itemID: \"minecraft:paper\"\n" +
                        "           meta: 0\n" +
                        "           slotItem: 15\n" +
                        "           itemTitle: \"Ver tierlist\"\n" +
                        "           itemLore: [\"Clique para ver a tierlist\"]\n" +
                        "   guiListPlayers:\n" +
                        "       titleGui: \"Rising Ranked\"\n" +
                        "       decorate:\n" +
                        "         - itemID: \"\"\n" +
                        "           meta: 0\n" +
                        "           slotItem: 0\n" +
                        "           itemTitle: \"\"\n" +
                        "           itemLore: [\"\"]\n" +
                        "         - itemID: \"\"\n" +
                        "           meta: 0\n" +
                        "           slotItem: 1\n" +
                        "           itemTitle: \"\"\n" +
                        "           itemLore: [\"\"]\n" +
                        "       nextPage:\n" +
                        "           itemID: \"\"\n" +
                        "           meta: 0\n" +
                        "           slotItem: 0\n" +
                        "           itemTitle: \"\"\n" +
                        "           itemLore: [\"\"]\n" +
                        "       prevPage:\n" +
                        "           itemID: \"\"\n" +
                        "           meta: 0\n" +
                        "           slotItem: 0\n" +
                        "           itemTitle: \"\"\n" +
                        "           itemLore: [\"\"]\n";
                writer.write(defaultConfig);
                System.out.println("[Ranqueada] Arquivo de configuração padrão criado em: " + configFile.getAbsolutePath());
            }
        } catch (IOException e) {
            System.out.println("[Ranqueada] Erro ao criar o arquivo de configuração padrão: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
