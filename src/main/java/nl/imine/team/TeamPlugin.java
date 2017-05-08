package nl.imine.team;

import nl.imine.team.commands.TeamCommand;
import nl.imine.team.config.Settings;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Dennis
 */
public class TeamPlugin extends JavaPlugin {

    private static Plugin plugin;
    private static Settings settings;

    @Override
    public void onEnable() {

        plugin = this;
        ConfigurationSerialization.registerClass(Team.class);
        setUpConfig();

        this.getCommand("team").setExecutor(new TeamCommand());

        TeamManager.getInstance().loadTeamsFromConfig();
    }

    @Override
    public void onDisable() {
        TeamManager.getInstance().saveTeamsToConfig();
        TeamManager.getInstance().unloadPlugin();
    }
    

    public static Plugin getInstance() {
        return plugin;
    }

    public static BlacklistedNames getBlackList() {
        return BlacklistedNames.getInstance();
    }

    private void setUpConfig() {
        settings = new Settings(this.getConfig());
        settings.createDefaults();
        this.saveConfig();
    }
}
