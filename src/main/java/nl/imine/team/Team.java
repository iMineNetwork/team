package nl.imine.team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nl.imine.team.exceptions.PlayerNotInvitedToTeam;
import nl.imine.team.exceptions.PlayerAlreadyInvitedException;
import nl.imine.team.exceptions.PlayerNotFoundException;
import nl.imine.team.exceptions.TeamFullException;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team.OptionStatus;

/**
 * @author Dennis
 */
public class Team implements ConfigurationSerializable {

    private org.bukkit.scoreboard.Team team;
    private int maxPlayersInTeam = 4;
    private Player teamLeader;
    private List<TeamInvite> invitations = new ArrayList<>();
    private int teamId;

    private Team() {
    }

    public Team(int teamId, Player teamLeader) {

        this.teamLeader = teamLeader;
        this.teamId = teamId;

        init();
        forceAddPlayer(teamLeader);
        setAllowFriendlyFire(false);
        setCanSeeFriendlyInvisibles(true);

    }

    public OfflinePlayer getTeamLeader() {
        return teamLeader;
    }

    /**
     * add a player to a team
     *
     * @param player
     * @throws TeamFullException when the team has reached the maximum amount of
     * players
     * @throws PlayerNotInvitedToTeam when the player was not invited to the
     * team (or if the invitation has expired)
     */
    public void addPlayer(Player player) throws TeamFullException, PlayerNotInvitedToTeam {
        updateInvitations();

        boolean isInvited = false;
        for (TeamInvite invitation : invitations) {
            if (invitation.getPlayer() == player) {
                isInvited = true;
                break;
            }
        }

        if (!isInvited) {
            throw new PlayerNotInvitedToTeam();
        }

        //remove the invitation form the list 
        new ArrayList<>(invitations).stream().filter(invitation -> invitation.getPlayer() == player).forEach(invitation -> invitations.remove(invitation));

        if (!(team.getSize() < getMaxPlayersInTeam())) {
            throw new TeamFullException();
        }

        forceAddPlayer(player);

    }

    /**
     * places a player in a team even when the team is full
     *
     * @param player the player to add to the team
     */
    public void forceAddPlayer(Player player) {
        team.addEntry(player.getPlayerListName());
    }

    public void removePlayerFromTeam(Player player) {
        team.removeEntry(player.getPlayerListName());
    }

    public List<Player> getPlayers() {
        //adding non player entities to a team via /scoreboard will result in some weard players in this list
        List<Player> players = new ArrayList<>();
        team.getEntries().forEach(entry -> players.add(Bukkit.getPlayer(entry)));
        return players;
    }

    public boolean hasPlayer(Player player) {
        return team.hasEntry(player.getPlayerListName());
    }

    public String getDisplayName() {
        return team.getDisplayName();
    }

    public void setDisplayName(String name) {
        team.setDisplayName(name);
    }

    public String getPrefix() {
        return team.getPrefix();
    }

    public void setPrefix(String name) {
        team.setPrefix(name + " ");
    }

    public String getSuffix() {
        return team.getSuffix();
    }

    public void setSuffix(String name) {
        team.setSuffix(" " + name);
    }

    public boolean allowFriendlyFire() {
        return team.allowFriendlyFire();
    }

    public void setAllowFriendlyFire(boolean value) {
        team.setAllowFriendlyFire(value);
    }

    public boolean canSeeFriendlyInvisibles() {
        return team.canSeeFriendlyInvisibles();
    }

    public void setCanSeeFriendlyInvisibles(boolean value) {
        team.setCanSeeFriendlyInvisibles(value);
    }

    public OptionStatus getCollisionRule() {
        return team.getOption(org.bukkit.scoreboard.Team.Option.COLLISION_RULE);
    }

    public void setCollisionRule(OptionStatus status) {
        team.setOption(org.bukkit.scoreboard.Team.Option.COLLISION_RULE, status);
    }

    public OptionStatus getNameTagVisibility() {
        return team.getOption(org.bukkit.scoreboard.Team.Option.NAME_TAG_VISIBILITY);
    }

    public void setNameTagVisibility(OptionStatus status) {
        team.setOption(org.bukkit.scoreboard.Team.Option.NAME_TAG_VISIBILITY, status);
    }

    public OptionStatus getDeathMessageVisability() {
        return team.getOption(org.bukkit.scoreboard.Team.Option.DEATH_MESSAGE_VISIBILITY);
    }

    public void setDeathMessageVisability(OptionStatus status) {
        team.setOption(org.bukkit.scoreboard.Team.Option.DEATH_MESSAGE_VISIBILITY, status);
    }

    public void invitePlayer(Player player) throws PlayerAlreadyInvitedException, PlayerNotFoundException {
        if (!player.isOnline()) {
            throw new PlayerNotFoundException();
        }

        if (isInvited(player)) {
            throw new PlayerAlreadyInvitedException();
        }

        TeamInvite invitation = new TeamInvite(player);
        invitations.add(invitation);

    }

    public boolean isInvited(Player player) {
        return invitations.stream().anyMatch(invitation -> invitation.getPlayer().equals(player));
    }

    public int getMaxPlayersInTeam() {
        return maxPlayersInTeam;
    }

    public int getId() {
        return teamId;
    }

    /**
     * removes all expired invitations
     */
    private void updateInvitations() {
        new ArrayList<>(invitations).forEach(invitation -> { //Thx MakerTim 
            if (invitation.isExpired()) {
                invitations.remove(invitation);
            }
        });
    }

    public void init() {
        this.team = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam("TEAMPLUGIN_" + teamId);
        System.out.println(getPlayers());
    }

    @Override
    public Map<String, Object> serialize() {

        Map<String, Object> map = new HashMap<>();

        map.put("id", getId());
        map.put("display_name", getDisplayName());
        map.put("teamleader", getTeamLeader());
        map.put("players", getPlayers());

        map.put("options.allow_friendly_fire", allowFriendlyFire());
        map.put("options.see_friendly_invisibles", canSeeFriendlyInvisibles());

//        map.put("options.collisionrule", getCollisionRule());
//        map.put("options.deathmessage_visability", getDeathMessageVisability());
//        map.put("options.nametag_visibility", getNameTagVisibility());
        map.put("options.max_players_in_team", getMaxPlayersInTeam());
        map.put("options.prefix", getPrefix());
        map.put("options.suffix", getSuffix());

        return map;
    }

    public Team(Map<String, Object> map) {

        //the ID is required for initialising the team
        this.teamId = (int) map.get("id");
        init();

        team.setDisplayName(((String) map.get("display_name")));

        if (map.get("teamleader") instanceof Player) {
            this.teamLeader = (Player) map.get("teamleader");
        } else {
            this.teamLeader = null;
        }

        //offline players are appearantly a CraftOfflinePlayer, therefore I'll filter them out 
        ((List<?>) map.get("players")).stream().forEach(player -> {
            if (player instanceof Player) {
                forceAddPlayer((Player) player);
            }
        });

        team.setAllowFriendlyFire((boolean) map.get("options.allow_friendly_fire"));
        team.setCanSeeFriendlyInvisibles((boolean) map.get("options.see_friendly_invisibles"));

        //OptionStatus is not serialisable?
//        setCollisionRule((OptionStatus) map.get("options.collisionrule"));
//        setDeathMessageVisability((OptionStatus) map.get("options.deathmessage_visability"));
//        setNameTagVisibility((OptionStatus) map.get("options.nametag_visibility"));
        maxPlayersInTeam = (int) map.get("options.max_players_in_team");

        team.setPrefix((String) map.get("options.prefix"));
        team.setSuffix((String) map.get("options.suffix"));
    }

    public void disband() {
        team.unregister();
    }

    public boolean hasPlayersOnline() {
        boolean hasOnlinePlayers = false;
        for (Player player : getPlayers()) {
            if (player.isOnline()) {
                hasOnlinePlayers = true;
            }
        }
        return hasOnlinePlayers;
    }
}
