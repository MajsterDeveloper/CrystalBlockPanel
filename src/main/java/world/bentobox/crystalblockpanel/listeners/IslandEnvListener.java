package world.bentobox.crystalblockpanel.listeners;

import org.bukkit.Bukkit;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.entity.Monster;
import org.bukkit.event.player.*;

import world.bentobox.bentobox.api.events.island.IslandEnterEvent;
import world.bentobox.bentobox.api.events.island.IslandExitEvent;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.crystalblockpanel.CrystalBlockPanel;
import world.bentobox.crystalblockpanel.island.IslandEnvSettings;
import world.bentobox.crystalblockpanel.island.IslandSettingsService;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class IslandEnvListener implements Listener {

    private final CrystalBlockPanel addon;
    private final IslandSettingsService settings;

    // cache: na jakiej wyspie gracz był ostatnio (ID wyspy jako String, bo BentoBox tak to trzyma)
    private final Map<UUID, String> lastIslandId = new HashMap<>();

    // cache chunku żeby nie mielić update co krok
    private final Map<UUID, Long> lastChunkKey = new HashMap<>();

    public IslandEnvListener(CrystalBlockPanel addon, IslandSettingsService settings) {
        this.addon = addon;
        this.settings = settings;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        update(e.getPlayer(), true);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        clearOverrides(e.getPlayer());
        lastIslandId.remove(e.getPlayer().getUniqueId());
        lastChunkKey.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        update(e.getPlayer(), true); // teleport = force
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent e) {
        update(e.getPlayer(), true);
    }

    @EventHandler
    public void onIslandEnter(IslandEnterEvent e) {
        Player p = Bukkit.getPlayer(e.getPlayerUUID());
        if (p == null) return;

        String islandId = e.getIsland().getUniqueId();
        lastIslandId.put(p.getUniqueId(), islandId);

        applyIslandOverrides(p, islandId);
    }

    @EventHandler
    public void onIslandExit(IslandExitEvent e) {
        Player p = Bukkit.getPlayer(e.getPlayerUUID());
        if (p == null) return;

        lastIslandId.remove(p.getUniqueId());
        clearOverrides(p);
    }


    // Blokowanie spawnu potworów dla wiecznego dnia
    @EventHandler(ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent e) {

        // tylko w światach wysp
        if (!isIslandWorld(e.getLocation().getWorld())) return;

        // tylko potwory (hostile)
        if (!(e.getEntity() instanceof Monster)) return;

        // spawner i summon-y mają działać normalnie
        CreatureSpawnEvent.SpawnReason reason = e.getSpawnReason();
        if (reason == CreatureSpawnEvent.SpawnReason.SPAWNER
                || reason == SpawnReason.SPAWNER_EGG
                || reason == SpawnReason.COMMAND
                || reason == SpawnReason.CUSTOM) {
            return;
        }

        // znajdź wyspę po lokacji spawnu
        Optional<Island> islandOpt = addon.getPlugin().getIslands().getIslandAt(e.getLocation());
        if (islandOpt.isEmpty()) return;

        Island island = islandOpt.get();
        String islandId = island.getUniqueId();

        IslandEnvSettings env = settings.env(islandId);

        // jeśli wieczny dzień -> blokuj spawn potworów
        if (env.isEternalDay()) {
            e.setCancelled(true);
        }
    }

    private void update(Player player, boolean force) {

        if (!isIslandWorld(player.getWorld())) {
            clearOverrides(player);
            lastIslandId.remove(player.getUniqueId());
            return;
        }

        Optional<Island> islandOpt = addon.getPlugin().getIslands().getIslandAt(player.getLocation());

        String newIsland = islandOpt.map(Island::getUniqueId).orElse(null);
        String prevIsland = lastIslandId.get(player.getUniqueId());

        if (!force && Objects.equals(prevIsland, newIsland)) return;

        lastIslandId.put(player.getUniqueId(), newIsland);

        if (newIsland == null) {
            clearOverrides(player);
            return;
        }

        applyIslandOverrides(player, newIsland);
    }

    public void forceUpdate(Player player) {
        update(player, true);
    }

    private void applyIslandOverrides(Player player, String islandId) {

        IslandEnvSettings env = settings.env(islandId);

        long dayTicks = addon.getSettings().getDayTicks();
        long nightTicks = addon.getSettings().getNightTicks();

        // ===== CZAS (player-specific) =====
        if (env.isEternalDay() && !env.isEternalNight() && !env.isNormalTime()) {
            player.setPlayerTime(dayTicks, false);   // rano
        } else if (env.isEternalNight() && !env.isEternalDay() && !env.isNormalTime()) {
            player.setPlayerTime(nightTicks, false);  // noc
        } else{
            player.resetPlayerTime(); // brak override
        }

        // ===== POGODA (player-specific) =====
        if (env.isEternalSun() && !env.isEternalRain() && !env.isNormalWeather()) {
            player.setPlayerWeather(WeatherType.CLEAR);
        } else if (env.isEternalRain() && !env.isEternalSun() && !env.isNormalWeather()) {
            player.setPlayerWeather(WeatherType.DOWNFALL);
        } else {
            player.resetPlayerWeather();
        }
    }

    private void clearOverrides(Player player) {
        player.resetPlayerTime();
        player.resetPlayerWeather();
    }

    private boolean isIslandWorld(World w) {
        String base = addon.getSettings().getWorldName().toLowerCase();
        String name = w.getName().toLowerCase();
        return name.equals(base) || name.equals(base + "_nether") || name.equals(base + "_the_end");
    }
}