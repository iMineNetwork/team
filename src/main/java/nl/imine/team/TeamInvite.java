package nl.imine.team;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * @author Dennis
 */
class TeamInvite {

    private static final long TIME_UNTIL_EXPIRATION = 10000; //10 sec???
    private boolean isExpired = false;
    private Player player;

    //prevent classes from making invalid invites
    private TeamInvite() {
    }

    TeamInvite(Player player) {
        this.player = player;
        Bukkit.getScheduler().runTaskLater(TeamPlugin.getInstance(), () -> {
            isExpired = true;
        }, TIME_UNTIL_EXPIRATION);
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isExpired() {
        return isExpired;
    }

}
