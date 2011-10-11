package com.bukkit.isabaellchen.leagues;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Handle events for all Player related events
 * @author Isabaellchen
 */
public class LeaguesPlayerListener extends PlayerListener {

    private final Leagues plugin;
    private HashMap<Player, Date> lastFounding;
    private HashMap<String, TempLeague> tempLeagues;

    public LeaguesPlayerListener(Leagues instance) {
        this.plugin = instance;
    }

    @Override
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final Location from = event.getFrom();
        final Location to = event.getTo();
        final Player player = event.getPlayer();

        Runnable runnable = new Runnable() {

            public void run() {

                int fromX = (int) (from.getX() / Config.BLOCK_DIMENSION);
                int fromZ = (int) (from.getZ() / Config.BLOCK_DIMENSION);

                int toX = (int) (to.getX() / Config.BLOCK_DIMENSION);
                int toZ = (int) (to.getZ() / Config.BLOCK_DIMENSION);

                if (fromX != toX || fromZ != toZ) {
                    if (Config.VERBOSE) {
                        player.sendMessage(toX + ":" + toZ);
                    }

                    String ownerFrom = Leagues.DB.getOwnerName((int) from.getX(), (int) from.getZ());
                    String ownerTo = Leagues.DB.getOwnerName((int) to.getX(), (int) to.getZ());
                    if (!ownerFrom.equals(ownerTo)) {
                        if (!ownerTo.equals(Config.EMPTY_BLOCK)) {
                            player.sendMessage(ownerTo);
                        }
                    }
                }
            }
        };
        Leagues.WORKER.execute(runnable);
    }

    @Override
    public void onPlayerLogin(final PlayerLoginEvent event) {
        Runnable runnable = new Runnable() {

            public void run() {
                Leagues.DB.registerPlayer(event.getPlayer());
            }
        };
        Leagues.WORKER.execute(runnable);
    }

    public boolean foundLeague(final String name, final String type,
            final ArrayList<String> players, final ArrayList<String> leagues,
            final Player founder) {

        Date now = new Date();

        if (now.getTime() - lastFounding.get(founder).getTime() < 300000) {
            founder.sendMessage(Config.TEXTCOLOR_SYS
                    + "Please wait. You can only found a new " + type + " every five minutes.");
            return false;
        } else {
            lastFounding.put(founder, now);
        }

        final int typeId = Leagues.DB.getTypeId(type);

        if (typeId == 0) {
            founder.sendMessage(Config.TEXTCOLOR_FAIL
                    + type + " is not a valid type.");
            return false;
        }

        if (!Leagues.DB.isGroupNameAllowed(name) || tempLeagues.containsKey(name)) {
            founder.sendMessage(Config.TEXTCOLOR_FAIL
                    + "Group creation failed, the name already exists.");
            return false;
        }

        int founderId = Leagues.DB.getUserId(founder);

        if (Leagues.DB.isExclusive(typeId) && Leagues.DB.isPlayerGrouptypeMember(founderId, typeId)) {
            founder.sendMessage(Config.TEXTCOLOR_FAIL
                    + "Group creation failed, you are already member of a " + type);
            return false;
        }

        final TempLeague league = new TempLeague(name, typeId, founderId);

        for (String p : players) {
            sendPlayerInvitation(p, name, founder);
        }

        for (String l : leagues) {
            sendLeagueInvitation(l, name, founder);
        }

        tempLeagues.put(name, league);

        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                int initMembers = Leagues.DB.getInitMemberCount(typeId);
                if (league.playerCount() + league.leagueCount() < initMembers) {
                    founder.sendMessage(Config.TEXTCOLOR_FAIL
                            + "Membercount was too low to create " + name);
                } else {
                    Leagues.DB.createLeague(league);
                    founder.sendMessage(Config.TEXTCOLOR_SUCCESS
                            + "You succesfully founded " + name + "!");
                }
                tempLeagues.remove(name);
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, 120000);

        return true;
    }

    private void sendPlayerInvitation(String p, String name, Player founder) {
        Player player = plugin.getServer().getPlayer(p);

        if (player == null) {
            return;
        }

        player.sendMessage(Config.TEXTCOLOR_SYS + "You, " + p + ", have been invited by "
                + founder.getName() + " to join " + name + ".");
        player.sendMessage(Config.TEXTCOLOR_SYS + "Please type \"/invitation accept "
                + name + "\" within the next two minutes to accept.");
    }

    private void sendLeagueInvitation(String l, String name, Player founder) {
        String leaderName = Leagues.DB.getLeaderName(l);
        String typeName = Leagues.DB.getLeaguesTypeName(l);

        if (leaderName.equals("") || typeName.equals("")) {
            return;
        }

        Player player = plugin.getServer().getPlayer(leaderName);

        player.sendMessage(Config.TEXTCOLOR_SYS + "Your " + typeName + " " + l + ", has been invited by "
                + founder.getName() + " to join " + name + ".");
        player.sendMessage(Config.TEXTCOLOR_SYS + "Please type \"/invitation " + l + " accept "
                + name + "\" within the next two minutes to accept.");
    }

    public boolean playerAcceptInvitation(String leagueName, Player player) {
        TempLeague league = tempLeagues.get(leagueName);
        String typeName = Leagues.DB.getTypeName(league.getType());

        if (!Leagues.DB.hostAcceptsClient(league.getType(), 0)) {
            player.sendMessage(Config.TEXTCOLOR_FAIL
                    + "Players can not join " + typeName + "s.");
            return false;
        }


        int playerId = Leagues.DB.getUserId(player);

        if (Leagues.DB.isExclusive(league.getType())
                && Leagues.DB.isPlayerGrouptypeMember(playerId, league.getType())) {
            player.sendMessage(Config.TEXTCOLOR_FAIL + "Can not join " + leagueName + "! "
                    + "You are already a member of another " + typeName);
            return false;
        }

        league.addPlayer(playerId);
        return true;
    }

    public boolean leagueAcceptInvitation(String clientLeague, String hostLeague, Player player) {
        if (!Leagues.DB.isLeader(clientLeague, player)) {
            player.sendMessage(Config.TEXTCOLOR_FAIL + "Only leaders can accept invitations.");
            return false;
        }

        TempLeague league = tempLeagues.get(hostLeague);
        String hostTypeName = Leagues.DB.getTypeName(league.getType());

        int clientTypeId = Leagues.DB.getTypeId(clientLeague);
        String clientTypeName = Leagues.DB.getTypeName(clientTypeId);

        if (!Leagues.DB.hostAcceptsClient(league.getType(), clientTypeId)) {
            player.sendMessage(Config.TEXTCOLOR_FAIL
                    + clientTypeName + "s can not join " + hostTypeName + "s.");
            return false;
        }


        int clientId = Leagues.DB.getGroupId(clientLeague);

        if (Leagues.DB.isExclusive(league.getType())
                && Leagues.DB.isPlayerGrouptypeMember(clientId, league.getType())) {
            player.sendMessage(Config.TEXTCOLOR_FAIL + "Can not join "
                    + hostLeague + "! ");
            player.sendMessage(Config.TEXTCOLOR_FAIL + clientLeague
                    + " is already a member of another " + hostTypeName);
            return false;
        }

        league.addLeague(clientId);
        return true;
    }

    public boolean claim(String name, Player player) {
        if (!Leagues.DB.hasOpRights(name, player)) {
            player.sendMessage(Config.TEXTCOLOR_FAIL + "You dont have OP rights.");
            return false;
        }

        int x = player.getLocation().getBlockX() / Config.BLOCK_DIMENSION;
        int z = player.getLocation().getBlockZ() / Config.BLOCK_DIMENSION;

        int i = Leagues.DB.getOwnerId(x, z);
        if (i != 0) {
            String groupName = Leagues.DB.getGroupName(i);
            player.sendMessage(Config.TEXTCOLOR_FAIL + "This land belongs to "+ groupName);
            return false;
        }

        if (Leagues.DB.getPlotSize(name) >= Leagues.DB.getMaxPlotSize(name)) {
            String groupName = Leagues.DB.getGroupName(i);
            player.sendMessage(Config.TEXTCOLOR_FAIL + groupName + " has reached their max number of blocks");
            return false;
        }


    }
}

