package nl.imine.team;

import java.util.ArrayList;
import java.util.List;
import nl.imine.team.config.Settings;
import nl.imine.team.exceptions.TeamNotFoundException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * @author Dennis
 */
public class TeamManager {

    private static TeamManager instance = new TeamManager();
    private static List<Team> teams = new ArrayList<>();
    private static int nextId = 0;

    private TeamManager() {
    }

    public static TeamManager getInstance() {
        return instance;
    }

    public void createTeam(Player teamLeader) {
        Team team = new Team(nextId, teamLeader);
        nextId++;

        team.setDisplayName(teamLeader.getDisplayName() + "'s team");

        teams.add(team);
    }

    public void createTeam(Player teamLeader, String teamName) {

        Team team = new Team(nextId, teamLeader);
        nextId++;

        team.setDisplayName(teamName);

        teams.add(team);
    }

    public Team getTeam(int id) throws TeamNotFoundException {
        Team team = teams.stream().filter(t -> t.getId() == id).findFirst().orElse(null);
        if (team == null) {
            throw new TeamNotFoundException();
        }
        return team;
    }

    public Team getTeam(Player p) throws TeamNotFoundException {
        Team team = teams.stream().filter(t -> t.hasPlayer(p)).findFirst().orElse(null);
        if (team == null) {
            throw new TeamNotFoundException();
        }

        return team;
    }

    public void disbandTeam(Team team) {
        Settings.removeTeamFromConfig(team);
        team.disband();
        teams.remove(team);
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void loadTeamsFromConfig() {
        teams.clear();
        Settings.getTeamsFromConfig().stream().forEach(team -> {
            // team.init();

            if (team.hasPlayersOnline()) { //in case of a restart there are no online players and that causes trouble
                teams.add(team);
                nextId = team.getId() + 1;//this way the next team that will be created will have a higher number than the team with the highest number
            }
        });
    }

    public void saveTeamsToConfig() {
        teams.stream().forEach(team -> {
            Settings.saveTeamToConfig(team);
        });
    }

    /**
     * call this when disabeling the plugin, or else it will cause problems the
     * next time the server starts
     */
    public void unloadPlugin() {
        Bukkit.getScoreboardManager().getMainScoreboard().getTeams().stream().filter(team -> team.getName().startsWith("TEAMPLUGIN_")).forEach(team -> {
            team.unregister();
        }
        );

    }
}
