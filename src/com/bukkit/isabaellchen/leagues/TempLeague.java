
package com.bukkit.isabaellchen.leagues;

import java.util.ArrayList;

/**
 *
 * @author Isa
 */
public class TempLeague {
    private String name;
    private int type, leader;
    private ArrayList<Integer> players, leagues;

    public TempLeague(String name, int type, int leader) {
        this.name = name;
        this.type = type;
        this.leader = leader;
        this.players = new ArrayList<Integer>();
        this.leagues = new ArrayList<Integer>();
    }

    public void addPlayer(int p) {
        players.add(p);
    }

    public int playerCount() {
        return players.size();
    }

    public int leagueCount() {
        return leagues.size();
    }

    public void addLeague(int g) {
        leagues.add(g);
    }

    public ArrayList<Integer> getLeagues() {
        return leagues;
    }

    public int getLeader() {
        return leader;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Integer> getPlayers() {
        return players;
    }

    public int getType() {
        return type;
    }
}
