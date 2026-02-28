package world.bentobox.crystalblockpanel.ui;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.bentobox.lists.Flags;
import world.bentobox.bentobox.managers.RanksManager;
import world.bentobox.crystalblockpanel.CrystalBlockPanel;
import world.bentobox.crystalblockpanel.island.IslandEnvSettings;
import world.bentobox.crystalblockpanel.listeners.IslandEnvListener;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class IslandSettingsPanel {

    private final CrystalBlockPanel addon;
    private static final MiniMessage MM = MiniMessage.miniMessage();

    public IslandSettingsPanel(CrystalBlockPanel addon) {
        this.addon = addon;
    }

    public void open(Player player) {

        Optional<Island> islandOpt = addon.getPlugin().getIslands().getIslandAt(player.getLocation());

        if (islandOpt.isEmpty()) {
            player.sendMessage("§cNie jesteś na żadnej wyspie.");
            return;
        }

        Island island = islandOpt.get();
        String islandId = island.getUniqueId();
        IslandEnvSettings env = addon.getIslandSettingsService().env(islandId);
        IslandEnvListener timeController = addon.getIslandWeatherTimeController();

        // Ustawienia uprawnień na wyspie
        var flagService = new world.bentobox.crystalblockpanel.island.IslandFlagService();
        int itemPickupMinRank = flagService.getMinRank(island, Flags.ITEM_PICKUP);
        int buttonUseMinRank = flagService.getMinRank(island, Flags.BUTTON);
        int pressPlateMinRank = flagService.getMinRank(island, Flags.PRESSURE_PLATE);
        int leverUseMinRank = flagService.getMinRank(island, Flags.LEVER);
        int attackMobsMinRank = flagService.getMinRank(island, Flags.HURT_MONSTERS);
        int attackAnimalsMinRank = flagService.getMinRank(island, Flags.HURT_ANIMALS);

        // MobSpawn
        var mode = flagService.getMonsterSpawnMode(island);
        String modeText = switch (mode) {
            case NATURAL_AND_SPAWNER -> "<green>TRYB STANDARDOWY</green>";
            case SPAWNER_ONLY -> "<green>TYLKO SPAWNERY</green>";
            case NATURAL_ONLY -> "<green>TYLKO NATURAL BEZ SPAWNERÓW</green>";
            case OFF -> "<red>WYŁĄCZONE</red>";
        };

        Gui gui = Gui.gui()
                .title(MM.deserialize("<aqua><bold>CrystalBlock</bold> <gray>– Ustawienia Wyspy"))
                .rows(3)
                .create();

        gui.disableAllInteractions();

        // ===== Dzień / Noc =====
        if( env.isEternalDay() ) {
            gui.setItem(10, guiItem(Material.CLOCK,
                    "Wieczny dzień",
                    "Tryb wiecznego dnia jest uruchomiony.",
                    "Naciśnij aby zmienić na wieczną noc",
                    true,
                    player,
                    () -> {
                        env.setEternalNight(true);
                        addon.getIslandSettingsService().set(islandId, env);
                        timeController.forceUpdate(player);
                        addon.getPlugin().getIslands().saveIsland(island);
                        open(player); // refresh
                    }
            ));
        }
        else if( env.isEternalNight() ) {
            gui.setItem(10, guiItem(Material.CLOCK,
                    "Wieczna noc",
                    "Tryb wiecznej nocy jest uruchomiony.",
                    "Naciśnij aby zmienić na zwykły tryb",
                    true,
                    player,
                    () -> {
                        env.setNormalTime(true);
                        addon.getIslandSettingsService().set(islandId, env);
                        timeController.forceUpdate(player);
                        open(player); // refresh
                    }
            ));
        }
        else{
            gui.setItem(10, guiItem(Material.CLOCK,
                    "Tryb Normalny",
                    "Jest to zwykły tryb Dzień/Noc oraz opady deszczu/śniegu.",
                    "Naciśnij aby zmienić na wieczny dzień",
                    true,
                    player,
                    () -> {
                        env.setEternalDay(true);
                        addon.getIslandSettingsService().set(islandId, env);
                        timeController.forceUpdate(player);
                        open(player); // refresh
                    }
            ));
        }


        // ===== Pogoda =====
        if( env.isEternalSun() ) {
            gui.setItem(11, guiItem(Material.SUNFLOWER,
                    "Wieczne słońce",
                    "Tryb wiecznego słońca jest uruchomiony.",
                    "Naciśnij aby zmienić na wieczny deszcz",
                    true,
                    player,
                    () -> {
                        env.setEternalRain(true);
                        addon.getIslandSettingsService().set(islandId, env);
                        timeController.forceUpdate(player);
                        open(player); // refresh
                    }
            ));
        }
        else if( env.isEternalRain() ) {
            gui.setItem(11, guiItem(Material.WATER_BUCKET,
                    "Wieczny Deszcz",
                    "Tryb wiecznego deszczu jest uruchomiony.",
                    "Naciśnij aby zmienić na wieczne słońce.",
                    true,
                    player,
                    () -> {
                        env.setNormalWeather(true);
                        addon.getIslandSettingsService().set(islandId, env);
                        timeController.forceUpdate(player);
                        open(player); // refresh
                    }
            ));
        }
        else {
            gui.setItem(11, guiItem(Material.SUNFLOWER,
                    "Tryb Normalny",
                    "Zwykły tryb z opadami.",
                    "Naciśnij aby zmienić na wieczne słońce.",
                    true,
                    player,
                    () -> {
                        env.setEternalSun(true);
                        addon.getIslandSettingsService().set(islandId, env);
                        timeController.forceUpdate(player);
                        open(player); // refresh
                    }
            ));
        }


        gui.setItem(12, ItemBuilder.from(Material.HOPPER)
                .name(MM.deserialize("<yellow>Podnoszenie przedmiotów</yellow>"))
                .lore(rankLore(player, itemPickupMinRank))
                .asGuiItem(event -> {
                    event.setCancelled(true);

                    flagService.cycleMinRank(island, world.bentobox.bentobox.lists.Flags.ITEM_PICKUP);
                    addon.getPlugin().getIslands().saveIsland(island);

                    // odśwież GUI natychmiast
                    open((Player) event.getWhoClicked());
                })
        );
        gui.setItem(13, ItemBuilder.from(Material.SKELETON_SKULL)
                .name(MM.deserialize("<yellow>Atakowanie mobów</yellow>"))
                .lore(rankLore(player, attackMobsMinRank))
                .asGuiItem(event -> {
                    event.setCancelled(true);

                    flagService.cycleMinRank(island, Flags.HURT_MONSTERS);
                    addon.getPlugin().getIslands().saveIsland(island);

                    // odśwież GUI natychmiast
                    open((Player) event.getWhoClicked());
                })
        );
        gui.setItem(14, ItemBuilder.from(Material.DIAMOND_SWORD)
                .name(MM.deserialize("<yellow>Atakowanie zwierząt</yellow>"))
                .lore(rankLore(player, attackAnimalsMinRank))
                .asGuiItem(event -> {
                    event.setCancelled(true);

                    flagService.cycleMinRank(island, Flags.HURT_ANIMALS);
                    addon.getPlugin().getIslands().saveIsland(island);

                    // odśwież GUI natychmiast
                    open((Player) event.getWhoClicked());
                })
        );
        gui.setItem(15, ItemBuilder.from(Material.SPAWNER)
                .name(MM.deserialize("<yellow>Spawn mobów</yellow>"))
                .lore(
                        MM.deserialize("<gray>Tryb: " + modeText),
                        MM.deserialize("<dark_gray>Kliknij aby zmienić")
                )
                .asGuiItem(event -> {
                    event.setCancelled(true);

                    flagService.cycleMonsterSpawnMode(island);
                    flagService.setAnimalSpawnMode(island, flagService.getMonsterSpawnMode(island));

                    addon.getPlugin().getIslands().saveIsland(island);
                    open((Player) event.getWhoClicked());
                })
        );
        gui.setItem(16, ItemBuilder.from(Material.STONE_BUTTON)
                .name(MM.deserialize("<yellow>Klikanie przycisków</yellow>"))
                .lore(rankLore(player, buttonUseMinRank))
                .asGuiItem(event -> {
                    event.setCancelled(true);

                    flagService.cycleMinRank(island, Flags.BUTTON);
                    addon.getPlugin().getIslands().saveIsland(island);

                    // odśwież GUI natychmiast
                    open((Player) event.getWhoClicked());
                })
        );
        gui.setItem(17, ItemBuilder.from(Material.HEAVY_WEIGHTED_PRESSURE_PLATE)
                .name(MM.deserialize("<yellow>Aktywacja płytek naciskowych</yellow>"))
                .lore(rankLore(player, pressPlateMinRank))
                .asGuiItem(event -> {
                    event.setCancelled(true);

                    flagService.cycleMinRank(island, Flags.PRESSURE_PLATE);
                    addon.getPlugin().getIslands().saveIsland(island);

                    // odśwież GUI natychmiast
                    open((Player) event.getWhoClicked());
                })
        );
        gui.setItem(18, ItemBuilder.from(Material.LEVER)
                .name(MM.deserialize("<yellow>Używanie Dźwigni</yellow>"))
                .lore(rankLore(player, leverUseMinRank))
                .asGuiItem(event -> {
                    event.setCancelled(true);

                    flagService.cycleMinRank(island, Flags.LEVER);
                    addon.getPlugin().getIslands().saveIsland(island);

                    // odśwież GUI natychmiast
                    open((Player) event.getWhoClicked());
                })
        );

        // ===== Powrót =====
        gui.setItem(26, ItemBuilder.from(Material.OAK_DOOR)
                .name(MM.deserialize("<gray>Powrót"))
                .asGuiItem(event -> {
                    event.setCancelled(true);
                    player.closeInventory();
                    new IslandPanel(addon).open(player);
                }));

        gui.open(player);
    }

    // Pobieranie tłumaczeń z BentBox
    private String tr(Player p, String key) {
        return User.getInstance(p).getTranslation(key);
    }

    private List<Component> rankLore(Player p, int minRank) {
        return List.of(
                MM.deserialize(colorRank(minRank, RanksManager.VISITOR_RANK) + tr(p, RanksManager.VISITOR_RANK_REF)),
                MM.deserialize(colorRank(minRank, RanksManager.COOP_RANK)    + tr(p, RanksManager.COOP_RANK_REF)),
                MM.deserialize(colorRank(minRank, RanksManager.TRUSTED_RANK) + tr(p, RanksManager.TRUSTED_RANK_REF)),
                MM.deserialize(colorRank(minRank, RanksManager.MEMBER_RANK)  + tr(p, RanksManager.MEMBER_RANK_REF)),
                MM.deserialize(colorRank(minRank, RanksManager.OWNER_RANK)   + tr(p, RanksManager.OWNER_RANK_REF)),
                MM.deserialize("<dark_gray>Kliknij aby zmienić")
        );
    }

    private String colorRank(int minRank, int rank) {
        // jeśli rank >= minRank => może
        return (rank >= minRank) ? "<green>✔ <gray>" : "<red>✖ <gray>";
    }

    private @NotNull GuiItem toggleItem(
            Material material,
            String name,
            boolean state,
            Player player,
            Runnable onToggle
    ) {
        String color = state ? "<green>" : "<red>";
        String status = state ? "<green>WŁĄCZONE" : "<red>WYŁĄCZONE";

        return ItemBuilder.from(material)
                .name(MM.deserialize(color + name))
                .lore(
                        MM.deserialize("<gray>Status: " + status),
                        MM.deserialize("<dark_gray>Kliknij, aby zmienić")
                )
                .asGuiItem(event -> {
                    event.setCancelled(true);
                    onToggle.run();
                });
    }

    private @NotNull GuiItem guiItem(
            Material material,
            String name,
            String desc1,
            String desc2,
            boolean state,
            Player player,
            Runnable onToggle
    ) {
        String color = state ? "<green>" : "<red>";
        String status = state ? "<green>WŁĄCZONE" : "<red>WYŁĄCZONE";

        return ItemBuilder.from(material)
                .name(MM.deserialize(color + name))
                .lore(
                        MM.deserialize("<gray>" + desc1),
                        MM.deserialize("<dark_gray>" + desc2)
                )
                .asGuiItem(event -> {
                    event.setCancelled(true);
                    onToggle.run();
                });
    }

    private @NotNull GuiItem placeholderToggle(Material material, String name) {
        return ItemBuilder.from(material)
                .name(MM.deserialize("<gray>" + name))
                .lore(
                        MM.deserialize("<dark_gray>Mechanika dojdzie za chwilę")
                )
                .asGuiItem(event -> event.setCancelled(true));
    }
}