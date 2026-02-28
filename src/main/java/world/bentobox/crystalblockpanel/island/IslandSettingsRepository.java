package world.bentobox.crystalblockpanel.island;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import world.bentobox.crystalblockpanel.CrystalBlockPanel;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class IslandSettingsRepository {

    private final CrystalBlockPanel addon;
    private final File file;
    private FileConfiguration cfg;
    private boolean dirty = false;

    private final Map<String, IslandEnvSettings> envCache = new HashMap<>();

    public IslandSettingsRepository(@org.jetbrains.annotations.UnknownNullability CrystalBlockPanel addon) {
        this.addon = addon;
        this.file = new File(addon.getDataFolder(), "island-settings.yml");
        reload();
    }

    public void reload() {
        if (!file.exists()) {
            try {
                addon.getDataFolder().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                addon.logError("Cannot create island-settings.yml: " + e.getMessage());
            }
        }
        this.cfg = YamlConfiguration.loadConfiguration(file);
        this.envCache.clear();
    }

    public void save() {
        if (!dirty) return;
        dirty = false;

        try {
            cfg.save(file);
        } catch (IOException e) {
            addon.logError("Cannot save island-settings.yml: " + e.getMessage());
        }
    }

    public IslandEnvSettings getEnv(String islandId) {
        return envCache.computeIfAbsent(islandId, this::loadEnvFromFile);
    }

    public void setEnv(String islandId, IslandEnvSettings env) {
        envCache.put(islandId, env);
        writeEnvToFile(islandId, env);
    }

    public void flush(String islandId) {
        IslandEnvSettings env = envCache.get(islandId);
        if (env != null) {
            writeEnvToFile(islandId, env);
            save();
        }
    }

    // =========================================================

    private IslandEnvSettings loadEnvFromFile(String islandId) {

        IslandEnvSettings s = new IslandEnvSettings();
        String base = "islands." + islandId + ".env.";

        s.setEternalDay(cfg.getBoolean(base + "eternalDay", false));
        s.setEternalNight(cfg.getBoolean(base + "eternalNight", false));
        s.setNormalTime(cfg.getBoolean(base + "normalTime", true));

        s.setEternalSun(cfg.getBoolean(base + "eternalSun", false));
        s.setEternalRain(cfg.getBoolean(base + "eternalRain", false));
        s.setNormalWeather(cfg.getBoolean(base + "normalWeather", true));

        s.setItemPickupAllowed(cfg.getBoolean(base + "itemPickupAllowed", true));
        s.setMobDamageEnabled(cfg.getBoolean(base + "mobDamageEnabled", true));
        s.setAnimalDamageEnabled(cfg.getBoolean(base + "animalDamageEnabled", true));

        s.setSpawnerOnlyMobs(cfg.getBoolean(base + "spawnerOnlyMobs", false));

        s.setButtonsAllowed(cfg.getBoolean(base + "buttonsAllowed", true));
        s.setPressurePlatesAllowed(cfg.getBoolean(base + "pressurePlatesAllowed", true));
        s.setLeversAllowed(cfg.getBoolean(base + "leversAllowed", true));

        return s;
    }

    private void writeEnvToFile(String islandId, IslandEnvSettings s) {

        String base = "islands." + islandId + ".env.";

        cfg.set(base + "eternalDay", s.isEternalDay());
        cfg.set(base + "eternalNight", s.isEternalNight());
        cfg.set(base + "normalTime", s.isNormalTime());

        cfg.set(base + "eternalSun", s.isEternalSun());
        cfg.set(base + "eternalRain", s.isEternalRain());
        cfg.set(base + "normalWeather", s.isNormalWeather());

        cfg.set(base + "itemPickupAllowed", s.isItemPickupAllowed());
        cfg.set(base + "mobDamageEnabled", s.isMobDamageEnabled());
        cfg.set(base + "animalDamageEnabled", s.isAnimalDamageEnabled());

        cfg.set(base + "spawnerOnlyMobs", s.isSpawnerOnlyMobs());

        cfg.set(base + "buttonsAllowed", s.isButtonsAllowed());
        cfg.set(base + "pressurePlatesAllowed", s.isPressurePlatesAllowed());
        cfg.set(base + "leversAllowed", s.isLeversAllowed());
    }
}