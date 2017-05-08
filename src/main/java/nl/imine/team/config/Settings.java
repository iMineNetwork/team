/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.imine.team.config;

import java.util.ArrayList;
import java.util.List;
import nl.imine.team.BlacklistedNames;
import nl.imine.team.Team;
import nl.imine.team.TeamPlugin;
import org.bukkit.configuration.file.FileConfiguration;

/**
 *
 * @author Dennis
 */
public class Settings {

    private static FileConfiguration configuration;

    public Settings(FileConfiguration configuration) {
        this.configuration = configuration;
    }

    public void createDefaults() {
        //configuration.addDefault("blacklistedWords", BlacklistedNames.getInstance().getBlacklist());

        configuration.options().copyDefaults(true);
    }

    public static List<String> getWordBlackList() {
        Object[] objects = (Object[]) configuration.get("blacklistedWords");
        List<String> blacklist = new ArrayList<>();

        for (Object object1 : objects) {
            blacklist.add((String) object1);
        }

        return blacklist;
    }

    public static void saveTeamToConfig(Team team) {
        System.out.println("saving team " + team.getId());
        configuration.set("teams." + team.getId(), team);
        TeamPlugin.getInstance().saveConfig();
    }

    public static List<Team> getTeamsFromConfig() {
        List<Team> teams = new ArrayList<>();

        Object[] obj = (Object[]) configuration.get("teams");
        
        for (Object team : obj) {
            teams.add((Team) team);
        }

        return teams;
    }

    public static void removeTeamFromConfig(Team team) {
        configuration.set("teams." + team.getId(), null);
        TeamPlugin.getInstance().saveConfig();
    }
}
