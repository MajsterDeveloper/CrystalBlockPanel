package world.bentobox.crystalblockpanel.ui;

import dev.triumphteam.gui.builder.item.ItemBuilder;import dev.triumphteam.gui.guis.Gui;import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import world.bentobox.crystalblockpanel.CrystalBlockPanel;

public class IslandPanel {

    private final CrystalBlockPanel addon;
    private static final MiniMessage MM = MiniMessage.miniMessage();

    public IslandPanel(CrystalBlockPanel addon) {
        this.addon = addon;
    }

    public void open(Player player) {

        Gui gui = Gui.gui()
                .title(MM.deserialize("<aqua><bold>CrystalBlock</bold> <gray>– Panel Wyspy</gray>"))
                .rows(3)
                .create();

        gui.disableAllInteractions();

        gui.setItem(10, ItemBuilder.from(Material.COMPARATOR)
                .name(MM.deserialize("<yellow>Ustawienia wyspy</yellow>"))
                .asGuiItem(event -> new IslandSettingsPanel(addon).open(player)));

        gui.setItem(11, ItemBuilder.from(Material.ENDER_CHEST)
                .name(MM.deserialize("<green>Skarbiec wyspy</green>"))
                .asGuiItem(event -> player.sendMessage("Otwieram Skarbiec...")));

        gui.setItem(12, ItemBuilder.from(Material.GRASS_BLOCK)
                .name(MM.deserialize("<dark_green>Biomy</dark_green>"))
                .asGuiItem(event -> player.sendMessage("Otwieram Biomy...")));

        gui.setItem(13, ItemBuilder.from(Material.BARRIER)
                .name(MM.deserialize("<red>Zablokowani gracze</red>"))
                .asGuiItem(event -> player.sendMessage("Otwieram Zablokowanych Graczy...")));

        gui.setItem(14, ItemBuilder.from(Material.PLAYER_HEAD)
                .name(MM.deserialize("<aqua>Uprawnienia członków</aqua>"))
                .asGuiItem(event -> player.sendMessage("Otwieram Uprawnienia członków...")));

        gui.setItem(15, ItemBuilder.from(Material.NAME_TAG)
                .name(MM.deserialize("<light_purple>Uprawnienia COOP</light_purple>"))
                .asGuiItem(event -> player.sendMessage("Otwieram Uprawnienia COOP...")));

        gui.setItem(26, ItemBuilder.from(Material.OAK_DOOR)
                .name(MM.deserialize("<gray>Zamknij</gray>"))
                .asGuiItem(event -> player.closeInventory()));

        gui.open(player);
    }
}