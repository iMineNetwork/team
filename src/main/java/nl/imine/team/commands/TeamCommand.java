package nl.imine.team.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.imine.team.Permissions;
import nl.imine.team.Team;
import nl.imine.team.TeamManager;
import nl.imine.team.Util;
import nl.imine.team.config.Settings;
import nl.imine.team.exceptions.PlayerAlreadyInvitedException;
import nl.imine.team.exceptions.PlayerNotFoundException;
import nl.imine.team.exceptions.PlayerNotInvitedToTeam;
import nl.imine.team.exceptions.TeamFullException;
import nl.imine.team.exceptions.TeamNotFoundException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team.OptionStatus;

/**
 * @author Dennis
 */
public class TeamCommand implements CommandExecutor {

    private static final String NOT_IN_TEAM_MESSAGE = ChatColor.RED + "You have to be in a team to use this command!";
    private static final String HORIZONTAL_LINE = ChatColor.STRIKETHROUGH + "-----------------------------------------------------";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String string, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command");
            return true;
        }
        Player player = (Player) sender;

        if (!player.hasPermission(Permissions.USE_TEAMCOMMAND)) {
            sender.sendMessage(ChatColor.RED + "You don't have the permissions to use this command");
            return true;
        }

        if (args.length == 0) {
            showHelp(player);
        } else {

            switch (args[0]) {
                case "help":
                    showHelp(player);
                    break;
                case "info":
                    info(player);
                    break;
                case "create":
                    create(player, args);
                    break;
                case "invite":
                    invite(player, args);
                    break;
                case "join":
                    join(player, args);
                    break;
                case "leave":
                    leave(player);
                    break;
                case "kick":
                    kick(player, args);
                    break;
                case "options":
                case "option":
                    options(player, args);
                    break;
                case "warp":
                    warp(player, args);
//                    try {
//                        Settings.saveTeamToConfig(TeamManager.getInstance().getTeam(player));
//                    } catch (TeamNotFoundException ex) {
//                    }
                    break;
                default:
                    player.sendMessage(ChatColor.RED + "I could not make chocolade of it, please try something else");
            }
        }

        return true;
    }

    private void showHelp(Player player) {
        player.sendMessage(ChatColor.BLUE + "List of all subcommands:");
        player.sendMessage(ChatColor.YELLOW + "   /team create - creëer a new team");
        player.sendMessage(ChatColor.YELLOW + "   /team invite <playername> [playername2] [playername3]... - invite players to your team");
        player.sendMessage(ChatColor.YELLOW + "   /team join <playername> - join a players team");
        player.sendMessage(ChatColor.YELLOW + "   /team leave - leave your current team");
        player.sendMessage(ChatColor.YELLOW + "   /team kick <playername> - kick a player");
        player.sendMessage(ChatColor.YELLOW + "   /team warp - teleport al teammembers to you");
        player.sendMessage(ChatColor.YELLOW + "   /team create - creëer a new team");
    }

    private void info(Player player) {
        Team team;
        try {
            team = TeamManager.getInstance().getTeam(player);

        } catch (TeamNotFoundException ex) {
            player.sendMessage(ChatColor.RED + "You are not in a team");
            return;
        }

        player.sendMessage(ChatColor.GREEN + HORIZONTAL_LINE);
        player.sendMessage("");
        player.sendMessage(ChatColor.GREEN + "Team name: " + ChatColor.YELLOW + team.getDisplayName());
        player.sendMessage(ChatColor.GREEN + "Maximum amount of players in team: " + ChatColor.YELLOW + team.getMaxPlayersInTeam());
        player.sendMessage(ChatColor.GREEN + "Teamleader: " + ChatColor.YELLOW + team.getTeamLeader().getDisplayName());

        player.sendMessage(ChatColor.GREEN + "Team members: ");

        team.getPlayers().stream()
                // .substring(17, entry.length() - 1) is to get the player's name from the text "CraftPlayer{name=1II1}"
                // the Teamleaders name has been mentioned earlier, so I'll leave it out
                /*.filter(entry -> !entry.substring(17, entry.length() - 1).equals(team.getTeamLeader().getDisplayName()))*/
                .forEach(e -> player.sendMessage(ChatColor.GREEN + "     - " + ChatColor.YELLOW + e.getDisplayName()));

        player.sendMessage("");
        player.sendMessage(ChatColor.GREEN + "Team settings: ");
        player.sendMessage(ChatColor.YELLOW + "      Prefix: " + team.getPrefix());
        player.sendMessage(ChatColor.YELLOW + "      Suffix: " + team.getSuffix());
        player.sendMessage(ChatColor.YELLOW + "      Friendlyfire: " + team.allowFriendlyFire());
        player.sendMessage(ChatColor.YELLOW + "      Collisionrule: " + team.getCollisionRule());
        player.sendMessage(ChatColor.YELLOW + "      Deathmessage visability: " + team.getDeathMessageVisability());
        player.sendMessage(ChatColor.YELLOW + "      Nametag visablity: " + team.getNameTagVisibility());
        player.sendMessage("");
        player.sendMessage(ChatColor.GREEN + HORIZONTAL_LINE);
    }

    private void create(Player player, String[] args) {

        try {
            Team team = TeamManager.getInstance().getTeam(player);

            //if the player is not in a team a TeamNotFoundException should occur and the following code should not be executed
            player.sendMessage(ChatColor.RED + "You are already in team " + team.getDisplayName() + ".");
            return;
        } catch (TeamNotFoundException ex) {
        }

        if (args.length > 1) {
            StringBuilder teamNaam = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                teamNaam.append(args[i]);
                teamNaam.append(" ");
            }

            String finalName = Util.replaceColorCodes(teamNaam.toString().trim());

            TeamManager.getInstance().createTeam(player, finalName);
            player.sendMessage(ChatColor.GREEN + "Succesfully created team " + finalName + ".");
        } else {
            TeamManager.getInstance().createTeam(player);
            player.sendMessage(ChatColor.GREEN + "Succesfully created a new team.");
        }
    }

    private void invite(Player player, String[] args) {

        List<String> playernames = new ArrayList<>(Arrays.asList(args));
        playernames.remove(0); //0 was "invite" so I'll remove it

        //create a list of players that have to be invited
        List<Player> playersToAdd = new ArrayList<>();
        playernames.stream().map(playername -> Bukkit.getPlayer(playername)).forEachOrdered(tmp -> {
            playersToAdd.add(tmp);
        });

        if (playernames.isEmpty()) {
            player.sendMessage(ChatColor.RED + "Use \"/team invite <username>\" to invite a player");
            return;
        }

        Team team;
        try {
            team = TeamManager.getInstance().getTeam(player);
        } catch (TeamNotFoundException ex) {
            player.sendMessage(NOT_IN_TEAM_MESSAGE);
            return;
        }

        if (team.getTeamLeader() != player) {
            player.sendMessage(ChatColor.RED + "You have to be the teamleader to invite players!");
            return;
        }

        playersToAdd.stream().forEach(p -> {
            if (team.hasPlayer(p)) {
                player.sendMessage(ChatColor.RED + p.getDisplayName() + " is already in your team!");
                return;
            }
            try {
                team.invitePlayer(p);
                player.sendMessage(ChatColor.GREEN + "Invited " + p.getDisplayName());
                p.sendMessage(ChatColor.GOLD + "You have been invited to " + player.getDisplayName() + "'s team, use /team join " + player.getDisplayName() + " to join.");
            } catch (PlayerAlreadyInvitedException ex) {
                player.sendMessage(ChatColor.YELLOW + player.getDisplayName() + " was already invited");
            } catch (PlayerNotFoundException ex) {
                player.sendMessage(ChatColor.YELLOW + player.getDisplayName() + " was not found");
            }

        });

        //TODO: clickable invites
    }

    private void join(Player player, String[] args) {
        if (args.length == 1) {
            player.sendMessage(ChatColor.RED + "Use \"/team join <username>\" to join a players team");
            // /team join <teamID> won't be displayed as teamid's are hidden
            return;
        }

        /* 2 posibilities:
                /team join <playername>
                /team join <teamID>
         */
        Team team = null;
        int teamId = -1;

        //get team by ID
        try {
            teamId = Integer.parseInt(args[1]);
            team = TeamManager.getInstance().getTeam(teamId);
        } catch (NumberFormatException nfe) {
            //only reached if a playername was used
        } catch (TeamNotFoundException ex) {
            player.sendMessage(ChatColor.RED + "team " + teamId + " was not found");
            return;
        }

        //get team by player
        if (team == null) {
            try {
                team = TeamManager.getInstance().getTeam(Bukkit.getPlayer(args[1]));
            } catch (TeamNotFoundException ex) {
                player.sendMessage(ChatColor.RED + args[1] + "'s team " + " was not found");
                return;
            }
        }

        try {
            team.addPlayer(player);
            player.sendMessage(ChatColor.GREEN + "You succesfully joined " + team.getTeamLeader().getDisplayName() + "'s team.");
        } catch (TeamFullException ex) {
            player.sendMessage(ChatColor.RED + team.getTeamLeader().getDisplayName() + "'s team is full");
        } catch (PlayerNotInvitedToTeam ex) {
            player.sendMessage(ChatColor.RED + "You are not invited to " + team.getTeamLeader().getDisplayName() + "'s team");
        }

    }

    private void leave(Player player) {
        Team team;
        try {
            team = TeamManager.getInstance().getTeam(player);
        } catch (TeamNotFoundException ex) {
            player.sendMessage(NOT_IN_TEAM_MESSAGE);
            return;
        }

        if (team.getPlayers().size() <= 1) {
            TeamManager.getInstance().disbandTeam(team);
            player.sendMessage(ChatColor.RED + "You succesfully left your team");
            return;
        }

        if (team.getTeamLeader().equals(player)) {
            player.sendMessage(ChatColor.RED + "You are the teamleader, please use /team transfer to pass the ownership to someone else");
            return;
        }

        team.removePlayerFromTeam(player);
        player.sendMessage(ChatColor.GREEN + "You succesfully left " + team.getTeamLeader() + "'s team.");
    }

    private void kick(Player player, String[] args) {

        //TODO: rewrite to /team kick <username> <reason>
        List<String> args1 = new ArrayList<>(Arrays.asList(args));
        args1.remove(0); //0 was "invite" so I'll remove it

        StringBuilder reasonBuilder;
        String reason = "";

        if (args1.isEmpty()) {
            player.sendMessage(ChatColor.RED + "Use \"/team kick <username> [username2] [username3]... \" to kick players");
            return;
        }

        Player playerToKick = Bukkit.getPlayer(args1.get(0));

        args1.remove(0); //remove the playername

        if (!args1.isEmpty()) { //if there is a reason
            reasonBuilder = new StringBuilder();
            for (int i = 0; i < args1.size(); i++) {
                reasonBuilder.append(args1.get(i));
                reasonBuilder.append(" ");
            }

            reason = Util.replaceColorCodes(reasonBuilder.toString().trim());

            if (reason.equals(reasonBuilder.toString().trim())) { //when there are no colorcodes used
                reason = ChatColor.GOLD + reason;
            }
        }

        Team team;
        try {
            team = TeamManager.getInstance().getTeam(player);
        } catch (TeamNotFoundException ex) {
            player.sendMessage(NOT_IN_TEAM_MESSAGE);
            return;
        }

        if (team.getTeamLeader() != player) {
            player.sendMessage(ChatColor.RED + "You have to be the teamleader to kick players!");
            return;
        }

        team.removePlayerFromTeam(playerToKick);

        player.sendMessage(ChatColor.GREEN + HORIZONTAL_LINE);
        playerToKick.sendMessage("");
        playerToKick.sendMessage(ChatColor.GREEN + "You have been kicked from " + player.getDisplayName() + "'s team.");
        if (!reason.isEmpty()) {
            playerToKick.sendMessage(ChatColor.GREEN + "The reason for getting kicked is:");
            playerToKick.sendMessage(reason);
        }
        playerToKick.sendMessage("");
        player.sendMessage(ChatColor.GREEN + HORIZONTAL_LINE);

    }

    private void options(Player player, String[] args) {

        if (!player.hasPermission(Permissions.OPTIONS)) {
            player.sendMessage(ChatColor.RED + "You don't have the permissions to change the team's options");
            return;
        }

        if (args.length == 1) { //no further 
            player.sendMessage(ChatColor.RED + "Use \"/team option <option> <value> \" to set an option players");
            return;
        }

        Team team;
        try {
            team = TeamManager.getInstance().getTeam(player);
        } catch (TeamNotFoundException ex) {
            player.sendMessage(ChatColor.RED + "You have to be in a team to use this command!");
            return;
        }

        if (team.getTeamLeader() != player) {
            player.sendMessage(ChatColor.RED + "You have to be the teamleader to change the team's settings!");
            return;
        }

        String option = args[1].toUpperCase();
        String value = "";
        if (args.length > 2) {
            value = args[2].toUpperCase();
        }

        switch (option) {
            case "FRIENDLYFIRE":
            case "FFIRE":
            case "FIRE":
            case "FF":
            case "F":
                if (!player.hasPermission(Permissions.OPTIONS_FRIENDLYFIRE)) {
                    player.sendMessage(ChatColor.RED + "You don't have the permissions to change the team's friendlyfire option");
                    return;
                }
                switch (value) {
                    case "TRUE":
                    case "WAAR":
                    case "1":
                        if (!player.hasPermission(Permissions.OPTIONS_FRIENDLYFIRE_TRUE)) {
                            player.sendMessage(ChatColor.RED + "You don't have the permissions to change the team's friendlyfire option");
                            return;
                        }

                        team.setAllowFriendlyFire(true);
                        player.sendMessage(ChatColor.GREEN + args[1] + " has been updated to " + team.allowFriendlyFire());
                        break;
                    case "FALSE":
                    case "ONWAAR":
                    case "0":
                        if (!player.hasPermission(Permissions.OPTIONS_FRIENDLYFIRE_FALSE)) {
                            player.sendMessage(ChatColor.RED + "You don't have the permissions to change the team's friendlyfire option");
                            return;
                        }

                        team.setAllowFriendlyFire(false);
                        player.sendMessage(ChatColor.GREEN + args[1] + " has been updated to " + team.allowFriendlyFire());
                        break;
                    case "":
                    case "!":
                        if (!player.hasPermission(Permissions.OPTIONS_FRIENDLYFIRE_TOGGLE)) {
                            player.sendMessage(ChatColor.RED + "You don't have the permissions to change the team's friendlyfire option");
                            return;
                        }

                        team.setAllowFriendlyFire(!team.allowFriendlyFire());
                        player.sendMessage(ChatColor.GREEN + args[1] + " has been updated to " + team.allowFriendlyFire());
                        break;

                    default:
                        player.sendMessage(ChatColor.RED + "Could not update FriendlyFire because I didn't understand " + args[2]);
                        break;
                }
                break;
            case "CANSEEFRIENDLYINVISIBLES":
            case "SEEFRIENDLYINVISIBLES":
            case "FRIENDLYINVISIBLES":
            case "FI":
            case "INVISIBLES":
            case "INVIS":
            case "INV":
            case "I":
                if (!player.hasPermission(Permissions.OPTIONS_FRIENDLYINVISIBLES)) {
                    player.sendMessage(ChatColor.RED + "You don't have the permissions to change the team's FriendlyInvisibles option");
                    return;
                }
                switch (value) {
                    case "TRUE":
                    case "WAAR":
                    case "1":
                        if (!player.hasPermission(Permissions.OPTIONS_FRIENDLYINVISIBLES_TRUE)) {
                            player.sendMessage(ChatColor.RED + "You don't have the permissions to change the team's FriendlyInvisibles option to true");
                            return;
                        }

                        team.setCanSeeFriendlyInvisibles(true);
                        player.sendMessage(ChatColor.GREEN + "SeeFriendlyInvisibles" + " has been updated to " + team.allowFriendlyFire());
                        break;

                    case "FALSE":
                    case "ONWAAR":
                    case "0":
                        if (!player.hasPermission(Permissions.OPTIONS_FRIENDLYINVISIBLES_FALSE)) {
                            player.sendMessage(ChatColor.RED + "You don't have the permissions to change the team's FriendlyInvisibles option to false");
                            return;
                        }

                        team.setCanSeeFriendlyInvisibles(false);
                        player.sendMessage(ChatColor.GREEN + "SeeFriendlyInvisibles" + " has been updated to " + team.allowFriendlyFire());
                        break;

                    case "":
                    case "!":
                        if (!player.hasPermission(Permissions.OPTIONS_FRIENDLYINVISIBLES_TOGGLE)) {
                            player.sendMessage(ChatColor.RED + "You don't have the permissions to switch the team's FriendlyInvisibles option to " + !team.allowFriendlyFire());
                            return;
                        }

                        team.setCanSeeFriendlyInvisibles(!team.allowFriendlyFire());
                        player.sendMessage(ChatColor.GREEN + "SeeFriendlyInvisibles" + " has been updated to " + team.allowFriendlyFire());
                        break;

                    default:
                        player.sendMessage(ChatColor.RED + "Could not update SeeFriendlyInvisibles because I didn't understand " + args[2]);
                        break;
                }
                break;
            case "COLLISIONRULE":
            case "COLISRULE":
            case "COLLIDE":
            case "COLRULE":
            case "CRULE":
            case "COLIS":
            case "COLL":
            case "COL":
            case "CR":
            case "C":
                if (!player.hasPermission(Permissions.OPTIONS_COLLISIONRULE)) {
                    player.sendMessage(ChatColor.RED + "You don't have the permissions to change the team's Collisionrule option to false");
                    return;
                }

                switch (value) {
                    case "ALWAYS":
                    case "ALTIJD":

                    //collision with everyone, true is just to make it easy for players to understand
                    case "TRUE":
                    case "WAAR":
                        if (!player.hasPermission(Permissions.OPTIONS_COLLISIONRULE_ALWAYS)) {
                            player.sendMessage(ChatColor.RED + "You don't have the permissions to change the team's Collisionrule option to false");
                            return;
                        }

                        team.setCollisionRule(OptionStatus.ALWAYS);
                        player.sendMessage(ChatColor.GREEN + "CollisionRule has been updated to true");
                        break;

                    case "FOR_OTHER_TEAMS":
                    case "VOOR_ANDERE_TEAMS":
                    case "OTHER_TEAMS":
                    case "ANDERE_TEAMS":

                    //No collision within own team, false is just to make it easy for players to understand
                    case "FALSE":
                    case "ONWAAR":
                        if (!player.hasPermission(Permissions.OPTIONS_COLLISIONRULE_FOR_OTHER_TEAMS)) {
                            player.sendMessage(ChatColor.RED + "You don't have the permissions to change the team's Collisionrule option to false");
                            return;
                        }

                        team.setCollisionRule(OptionStatus.FOR_OTHER_TEAMS);
                        player.sendMessage(ChatColor.GREEN + "CollisionRule has been updated to HIDDEN");
                        break;
                    case "FOR_OWN_TEAM":
                    case "VOOR_EIGEN_TEAM":
                    case "OWN_TEAM":
                    case "EIGEN_TEAM":
                        if (!player.hasPermission(Permissions.OPTIONS_COLLISIONRULE_FOR_OWN_TEAM)) {
                            player.sendMessage(ChatColor.RED + "You don't have the permissions to change the team's Collisionrule option to FOR_OWN_TEAM");
                            return;
                        }

                        team.setCollisionRule(OptionStatus.FOR_OWN_TEAM);
                        player.sendMessage(ChatColor.GREEN + "CollisionRule has been updated to FOR_OWN_TEAM.");
                        break;
                    case "NEVER":
                    case "NOOIT":
                        if (!player.hasPermission(Permissions.OPTIONS_COLLISIONRULE_NEVER)) {
                            player.sendMessage(ChatColor.RED + "You don't have the permissions to change the team's Collisionrule option to NEVER");
                            return;
                        }

                        team.setCollisionRule(OptionStatus.NEVER);
                        player.sendMessage(ChatColor.GREEN + "CollisionRule has been updated to NEVER");
                        break;
                    case "":
                    case "!":
                        if (!player.hasPermission(Permissions.OPTIONS_COLLISIONRULE_TOGGLE)) {
                            player.sendMessage(ChatColor.RED + "You don't have the permissions to toggle the team's FriendlyInvisibles option");
                            return;
                        }

                        switch (team.getCollisionRule()) {
                            case ALWAYS:
                                if (!player.hasPermission(Permissions.OPTIONS_COLLISIONRULE_FOR_OWN_TEAM)) {
                                    player.sendMessage(ChatColor.RED + "You don't have the permissions to change the team's Collisionrule option to false.");
                                    return;
                                }

                                team.setCollisionRule(OptionStatus.FOR_OWN_TEAM);
                                break;
                            case FOR_OTHER_TEAMS:
                            case FOR_OWN_TEAM:
                            case NEVER:
                            default:
                                if (!player.hasPermission(Permissions.OPTIONS_COLLISIONRULE_ALWAYS)) {
                                    player.sendMessage(ChatColor.RED + "You don't have the permissions to change the team's Collisionrule option to true.");
                                    return;
                                }
                                team.setCollisionRule(OptionStatus.ALWAYS);
                                break;
                        }
                        player.sendMessage(ChatColor.GREEN + "CollisionRule has been updated to " + team.getCollisionRule().toString());
                        break;
                    default:
                        player.sendMessage(ChatColor.RED + "Could not update SeeFriendlyInvisibles because I didn't understand " + args[2]);
                        break;
                }
                break;

            case "DEATHMESSAGEVISABILITY":
            case "DEATHMESSAGES":
            case "DEATH":
            case "DMV":
            case "DV":
            case "D":
                switch (value) {
                    case "ALWAYS":
                    case "ALTIJD":
                    case "A":
                        team.setDeathMessageVisability(OptionStatus.ALWAYS);
                        player.sendMessage(ChatColor.GREEN + "DeathMessageVisability has been updated to " + team.getDeathMessageVisability().toString());
                        break;
                    case "HIDDEN":
                    case "VERBORGEN":
                    case "H":
                    case "V":
                        team.setDeathMessageVisability(OptionStatus.FOR_OTHER_TEAMS);
                        player.sendMessage(ChatColor.GREEN + "DeathMessageVisability has been updated to " + team.getDeathMessageVisability().toString());
                        break;
                    case "":
                    case "!":
                        switch (team.getDeathMessageVisability()) {
                            case ALWAYS:
                                team.setDeathMessageVisability(OptionStatus.FOR_OTHER_TEAMS);
                                break;
                            case FOR_OTHER_TEAMS:
                            case FOR_OWN_TEAM:
                            case NEVER:
                            default:
                                team.setDeathMessageVisability(OptionStatus.ALWAYS);
                                break;
                        }
                        player.sendMessage(ChatColor.GREEN + "DeathMessageVisability has been updated to " + team.getDeathMessageVisability().toString());
                        break;
                    default:
                        player.sendMessage(ChatColor.RED + "Could not update DeathMessageVisability because I didn't understand " + args[2]);
                        break;
                }
                break;
            case "NAMETAGVISABILITY":
            case "NAMETAGS":
            case "NAMETAG":
            case "NAMET":
            case "NT":
                switch (value) {
                    case "ALWAYS":
                    case "ALTIJD":
                    case "A":
                        team.setNameTagVisibility(OptionStatus.ALWAYS);
                        player.sendMessage(ChatColor.GREEN + "DeathMessageVisability has been updated to " + team.getDeathMessageVisability().toString());
                        break;
                    case "HIDDEN":
                    case "VERBORGEN":
                    case "H":
                    case "V":
                        team.setNameTagVisibility(OptionStatus.FOR_OTHER_TEAMS);
                        player.sendMessage(ChatColor.GREEN + "Deathmessage visability has been updated to " + team.getDeathMessageVisability().toString());
                        break;
                    case "":
                    case "!":
                        switch (team.getNameTagVisibility()) {
                            case ALWAYS:
                                team.setNameTagVisibility(OptionStatus.FOR_OTHER_TEAMS);
                                break;
                            case FOR_OTHER_TEAMS:
                            case FOR_OWN_TEAM:
                            case NEVER:
                            default:
                                team.setNameTagVisibility(OptionStatus.ALWAYS);
                                break;
                        }
                        player.sendMessage(ChatColor.GREEN + "Nametag visability has been updated to " + team.getNameTagVisibility().toString());
                        break;
                    default:
                        player.sendMessage(ChatColor.RED + "Could not update deathmessage visability because I didn't understand " + args[2]);
                        break;
                }
                break;
            case "NAME":
            case "N":
                if (value.equals("")) {
                    player.sendMessage(ChatColor.RED + "Use \"/team option name <name> \" to set the team name");
                    return;
                }
                team.setDisplayName(value);
                player.sendMessage(ChatColor.GREEN + "Your team's name has been updated to " + team.getDisplayName());
                break;
            case "PREFIX":
            case "PREF":
            case "P":
                if (value.equals("")) {
                    player.sendMessage(ChatColor.RED + "Use \"/team option prefix <prefix> \" to set the team's prefix");
                    return;
                }
                team.setPrefix(value);
                player.sendMessage(ChatColor.GREEN + "Your team's prefix has been updated to " + team.getPrefix());
                break;
            case "SUFFIX":
            case "SUFF":
            case "S":
                if (value.equals("")) {
                    player.sendMessage(ChatColor.RED + "Use \"/team option suffix <suffix> \" to set the team's suffix");
                    return;
                }
                team.setSuffix(Util.replaceColorCodes(value));
                player.sendMessage(ChatColor.GREEN + "Your team's suffix has been updated to " + team.getSuffix());
                break;
            default:
                player.sendMessage(ChatColor.RED + "I did not recognise " + ChatColor.YELLOW + option + ChatColor.RED + ", if you think it should be added send me (1II1) a message (I love messages). ");
                break;
        }

    }

    private void warp(Player player, String[] args) {
        if (!player.hasPermission(Permissions.WARP)) {
            player.sendMessage(ChatColor.RED + "You do not have the permissions to use warp");
            return;
        }

        boolean hasTeleportedSomeone = false;

        try {
            if (!TeamManager.getInstance().getTeam(player).getTeamLeader().equals(player)) {
                player.sendMessage(ChatColor.RED + "You have to be the team leader to use team warp");
                return;
            }

            for (Player p : TeamManager.getInstance().getTeam(player).getPlayers()) {
                //a^2 + b^2 + c^2 = d^2 with d = distance from the player to the leader
                double distanceToLeader = Math.abs(Math.pow(player.getLocation().getX() - p.getLocation().getX(), 2));
                distanceToLeader += Math.abs(Math.pow(player.getLocation().getY() - p.getLocation().getY(), 2));
                distanceToLeader += Math.abs(Math.pow(player.getLocation().getZ() - p.getLocation().getZ(), 2));
                distanceToLeader = Math.sqrt(distanceToLeader);

                //only teleport if someone is in an other dimension or more then X blocks away
                if (p.getLocation().getWorld() != player.getLocation().getWorld() || distanceToLeader > 100) {
                    p.teleport(player);
                    hasTeleportedSomeone = true;
                }
            }
        } catch (TeamNotFoundException ex) {
        }
        if (!hasTeleportedSomeone) {
            player.sendMessage(ChatColor.RED + "No players where teleported since they are already closeby");
        }
    }
}
