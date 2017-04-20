/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.imine.team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nl.imine.team.exceptions.TeamFullException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.OptionStatus;

/**
 *
 * @author Dennis
 */
public class TeamManager {

    private static ScoreboardManager manager = Bukkit.getScoreboardManager();
    private static List<Team> teams = new ArrayList<>();
    private static Map<Team, Player> teamLeaders = new HashMap<>();
    private static Map<Player, ArrayList<Team>> invitations = new HashMap<>();
    private static TeamManager instance = new TeamManager();
    private static int nextId = 0;
    private static final int MAX_PLAYERS_PER_TEAM = 4;

    private TeamManager() {
    }

    public static TeamManager getInstance() {
        return instance;
    }

    public void createTeam(Player teamLeader) {

        Team team = manager.getNewScoreboard().registerNewTeam(nextId + "");
        nextId++;

        team.setDisplayName(teamLeader.getDisplayName() + "'s team");
        team.setAllowFriendlyFire(false);
        team.setCanSeeFriendlyInvisibles(true);

        teamLeaders.put(team, teamLeader);
        teams.add(team);
    }

    public void createTeam(Player teamLeader, String teamName) {

        Team team = manager.getNewScoreboard().registerNewTeam(nextId + "");
        nextId++;

        team.setDisplayName(teamName);
        team.setAllowFriendlyFire(false);
        team.setCanSeeFriendlyInvisibles(true);

        teamLeaders.put(team, teamLeader);
        teams.add(team);
    }

    public Player getTeamLeader(Team team) {
        return teamLeaders.get(team);
    }

    public Team getTeam(int id) {
        return teams.stream().filter(team -> team.getName().equals(id)).findFirst().orElse(null);
    }

    public Team getTeam(Player p) {
        for (Team team : teams) {
            for (String entry : team.getEntries()) {
                if (entry.equals(p.toString())) {
                    return team;
                }
            }
        }
        return null;
    }

    public void addToTeam(Player entry, int teamId) throws TeamFullException {
        Team team = getTeam(teamId);
        if (team.getSize() < MAX_PLAYERS_PER_TEAM) {
            team.addEntry(entry.toString());
        } else {
            throw new TeamFullException("team " + teamId + " is vol");
        }
    }

    public void removeFromTeam(Player entry) {
        for (Team team : teams) {
            for (String entryList : team.getEntries()) {
                if (entryList.equals(entry.toString())) {
                    team.removeEntry(entry.toString());
                }
            }
        }
    }

    public void setTeamName(int teamId, String name) {
        getTeam(teamId).setDisplayName(name);
    }

    public void setTeamPrefix(int teamId, String name) {
        getTeam(teamId).setPrefix(name);
    }

    public void setTeamSuffix(int teamId, String name) {
        getTeam(teamId).setSuffix(name);
    }

    public void setNameTagVisibility(int teamId, OptionStatus status) {
        getTeam(teamId).setOption(Team.Option.NAME_TAG_VISIBILITY, status);
    }

    public void setAllowFriendlyFire(int teamId, boolean value) {
        getTeam(teamId).setAllowFriendlyFire(value);
    }

    public boolean isInvited(Player player, int teamId) {
        return invitations.get(player).stream().anyMatch(team -> team.getName().equals(teamId));
    }

    public void createInvite(Player player, Team team) {
        if (invitations.containsKey(player)) {
            ArrayList<Team> list = invitations.get(player);
            list.add(team);
            invitations.remove(player);
            invitations.put(player, list);
        } else {
            ArrayList<Team> list = new ArrayList<>();
            list.add(team);
            invitations.put(player, list);
        }

    }
    
    public int getMaxPlayersPerTeam(){
        return MAX_PLAYERS_PER_TEAM;
    }
}
