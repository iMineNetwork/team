/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.imine.team.commands;

import nl.imine.team.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Dennis
 */
public class TeamCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String string, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command");
            return true;
        }
        Player player = (Player) sender;

        if (args.length == 0) {
            showHelp(player);
        } else {

            if (args[0].equals("create")) {
                create(player, args);
            } else if (args[0].equals("invite")) {
                invite(player, args);
            } else if (args[0].equals("join")) {

            } else if (args[0].equals("leave")) {

            } else if (args[0].equals("remove")) {

            } else if (args[0].equals("options")) {

            }
        }

        return true;
    }

    private void showHelp(Player player) {
        player.sendMessage(ChatColor.BLUE + "List of all subcommands:");
    }

    private void create(Player player, String[] args) {
        if (args.length > 1) {
            StringBuilder teamNaam = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                teamNaam.append(args[i]);
                teamNaam.append(" ");
            }
            TeamManager.getInstance().createTeam(player, teamNaam.toString());
        } else {
            TeamManager.getInstance().createTeam(player);
        }
    }

    private void invite(Player player, String[] args) {
        if (args.length == 1) {
            player.sendMessage(ChatColor.RED + "Use \"/team invite <username>\" to invite a player");
        } else {
            
            if(TeamManager.getInstance().getTeam(player).getSize() >= TeamManager.getInstance().getMaxPlayersPerTeam()){
                player.sendMessage(ChatColor.RED + "Your team is already full, a team consists at most out of " + TeamManager.getInstance().getMaxPlayersPerTeam() + " players." );
                return;
            }
            Player invitedPlayer = Bukkit.getPlayer(args[2]);
            TeamManager.getInstance().createInvite(invitedPlayer, TeamManager.getInstance().getTeam(player));
            
            player.sendMessage(ChatColor.GREEN + "Invited " + args[2]);
        }
    }

    private void join(Player player, String[] args) {

    }

    private void leave(Player player, String[] args) {

    }

    private void remove(Player player, String[] args) {

    }

    private void options(Player player, String[] args) {

    }

}
