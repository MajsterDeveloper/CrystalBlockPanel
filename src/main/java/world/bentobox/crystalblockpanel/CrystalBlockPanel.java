package world.bentobox.crystalblockpanel;

import org.bukkit.Bukkit;

import world.bentobox.bentobox.api.addons.Addon;
import world.bentobox.bentobox.api.configuration.Config;
import world.bentobox.crystalblockpanel.commands.IslandPanelCommand;
import world.bentobox.crystalblockpanel.island.IslandSettingsRepository;
import world.bentobox.crystalblockpanel.island.IslandSettingsService;
import world.bentobox.crystalblockpanel.listeners.IslandEnvListener;

public class CrystalBlockPanel extends Addon {

    private Settings settings;

    private IslandSettingsRepository islandSettingsRepository;
    private IslandSettingsService islandSettingsService;
    private IslandEnvListener islandEnvListener;

    @Override
    public void onLoad() {
        super.onLoad();

        // jeśli masz @StoreAt w Settings -> to zadziała jak w ControlPanel
        saveDefaultConfig();

        settings = new Config<>(this, Settings.class).loadConfigObject();
        if (settings == null) {
            logError("CrystalBlockPanel settings could not load! Addon disabled.");
            setState(State.DISABLED);
        }
    }

    @Override
    public void onEnable() {

        if (getPlugin() == null || !getPlugin().isEnabled()) {
            Bukkit.getLogger().severe("BentoBox is not available or disabled!");
            setState(State.DISABLED);
            return;
        }
        if (getState().equals(State.DISABLED)) return;

        world.bentobox.crystalblockpanel.libs.triumphgui.TriumphGui.setPlugin(getPlugin());

        // Serwisy
        islandSettingsRepository = new IslandSettingsRepository(this);
        islandSettingsService = new IslandSettingsService(islandSettingsRepository);

        // Listener środowiska wysp
        islandEnvListener = new IslandEnvListener(this, islandSettingsService);
        registerListener(islandEnvListener);

        // Podpinamy /is panel do każdego gamemode (np OneBlock)
        getPlugin().getAddonsManager().getGameModeAddons().forEach(gameModeAddon -> {
            gameModeAddon.getPlayerCommand().ifPresent(playerRoot -> {
                new IslandPanelCommand(this, playerRoot); // => /is panel
            });
        });

        // opcjonalny autosave
        getPlugin().getServer().getScheduler().runTaskTimer(
                getPlugin(),
                () -> islandSettingsRepository.save(),
                20L * 60,
                20L * 60
        );

        log("CrystalBlockPanel enabled!");
    }

    @Override
    public void onReload() {
        super.onReload();

        settings = new Config<>(this, Settings.class).loadConfigObject();
        if (settings == null) {
            logError("CrystalBlockPanel settings could not load! Addon disabled.");
            setState(State.DISABLED);
            return;
        }

        if (islandSettingsRepository != null) islandSettingsRepository.save();
    }

    @Override
    public void onDisable() {
        if (islandSettingsRepository != null) islandSettingsRepository.save();
        log("CrystalBlockPanel disabled!");
    }

    public Settings getSettings() {
        return settings;
    }

    public IslandSettingsService getIslandSettingsService() {
        return islandSettingsService;
    }

    public IslandEnvListener getIslandWeatherTimeController() {
        return islandEnvListener;
    }
}