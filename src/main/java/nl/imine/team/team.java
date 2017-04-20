/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.imine.team;

import nl.imine.team.commands.TeamCommand;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Dennis
 */
public class team extends JavaPlugin{

    @Override
    public void onEnable() {
        this.getCommand("team").setExecutor(new TeamCommand());
    }

    @Override
    public void onDisable() {
        super.onDisable(); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
}
