/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.imine.team;

import java.util.ArrayList;
import java.util.List;
import nl.imine.team.config.Settings;

/**
 *
 * @author Dennis
 */
public class BlacklistedNames {

    private static List<String> blacklistedItems = new ArrayList<>();

    private static BlacklistedNames instance = new BlacklistedNames();

    private BlacklistedNames() {
    }

    public static BlacklistedNames getInstance() {
        return instance;
    }

    public void loadBlacklist() {
//        TeamPlugin.getConfiguration().s
        blacklistedItems = Settings.getWordBlackList();

    }

    public List<String> getBlacklist() {
//        return blacklistedItems;
        List<String> t = new ArrayList<>();
        t.add("t");
        return t;
    }
}
