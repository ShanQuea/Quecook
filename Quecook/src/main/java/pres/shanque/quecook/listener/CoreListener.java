package pres.shanque.quecook.listener;

import eos.moe.dragoncore.api.gui.event.CustomPacketEvent;
import eos.moe.dragoncore.network.PacketSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class CoreListener implements Listener {
    private final Plugin plugin;

    public CoreListener(Plugin plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onReloadCache(CustomPacketEvent e) {

        if (!"DragonCore".equals(e.getIdentifier()) || e.getData().size() != 1)
            return;
        if (!"cache_loaded".equals(e.getData().get(0)))
            return;
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "Gui/鹊之烹饪界面.yml"));
        YamlConfiguration yaml2 = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "Gui/鹊之烹饪开始界面.yml"));
        PacketSender.sendYaml(e.getPlayer(), "gui/鹊之烹饪界面.yml", yaml);
        PacketSender.sendYaml(e.getPlayer(),"gui/鹊之烹饪开始界面.yml",yaml2);

    }
}
