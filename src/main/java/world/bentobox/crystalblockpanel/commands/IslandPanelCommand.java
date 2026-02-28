package world.bentobox.crystalblockpanel.commands;

import java.util.List;

import org.bukkit.entity.Player;
import world.bentobox.bentobox.api.commands.CompositeCommand;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.crystalblockpanel.CrystalBlockPanel;
import world.bentobox.crystalblockpanel.ui.IslandPanel;

public class IslandPanelCommand extends CompositeCommand {

    private CrystalBlockPanel addon;

    // parentCommand = gamemodeAddon.getPlayerCommand().get()
    public IslandPanelCommand(CrystalBlockPanel addon, CompositeCommand parentCommand) {
        super(addon, parentCommand, "panel");
    }

    @Override
    public void setup() {
        this.addon = getAddon();
        setOnlyPlayer(true);
        setPermission("panel"); // da finalnie permission: bentobox.crystalblockpanel.panel (zależy od prefixu)
        setDescription("crystalblockpanel.commands.panel.description");
    }

    @Override
    public boolean execute(User user, String label, List<String> args) {
        Player p = user.getPlayer();
        if (p == null) return true;

        // Odpal Twoje GUI
        new IslandPanel(addon).open(p);
        return true;
    }
}