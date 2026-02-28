package world.bentobox.crystalblockpanel.libs.triumphgui;

import org.bukkit.plugin.Plugin;

public final class TriumphGui {

    private static Plugin plugin;

    private TriumphGui() {}

    public static void setPlugin(Plugin pl) {
        plugin = pl;
    }

    public static Plugin getPlugin() {
        if (plugin == null) {
            throw new IllegalStateException("TriumphGui plugin is not set. Call TriumphGui.setPlugin(...) in onEnable().");
        }
        return plugin;
    }
}