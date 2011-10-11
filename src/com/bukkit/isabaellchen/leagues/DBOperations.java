/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bukkit.isabaellchen.leagues;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 *
 * @author Isa
 */
public class DBOperations {

    private DBManager man;

    public DBOperations(String url, String user, String password, String driver, int poolsize) {
        this.man = new DBManager(url, user, password, driver, poolsize);
    }

    public boolean isActionAllowed(Player player, Block block) {
        int userId = getUserId(player);
        int ownerId = getOwnerId(block.getX(), block.getZ());

        if (ownerId == 0) {
            return true;
        }

        HashMap<String, String> filter = new HashMap<String, String>();

        filter.put(DBSchema.MEMBERSHIPS_USER_ID, String.valueOf(userId));
        filter.put(DBSchema.MEMBERSHIPS_GROUP_ID, String.valueOf(ownerId));

        ArrayList<Map<String, String>> result =
                man.select(DBSchema.T_MEMBERSHIPS, filter);

        if (!result.isEmpty()) {
            return true;
        }

        filter = new HashMap<String, String>();

        filter.put(DBSchema.MEMBERSHIPS_NORSTC_USER_ID, String.valueOf(userId));
        filter.put(DBSchema.MEMBERSHIPS_NORSTC_GROUP_ID, String.valueOf(ownerId));

        result = man.select(DBSchema.V_MEMBERSHIPS_NORSTC, filter);

        return !result.isEmpty();
    }

    public boolean isGroupNameAllowed(String name) {
        for (String n : Config.FORBIDDEN_GROUP_NAMES) {
            if (n.equals(name)) {
                return false;
            }
        }

        HashMap<String, String> filter = new HashMap<String, String>();

        filter.put(DBSchema.GROUPS_NAME, name);

        ArrayList<Map<String, String>> result =
                man.select(DBSchema.T_GROUPS, filter);

        return !result.isEmpty();
    }

    public boolean isExclusive(int typeId) {
        HashMap<String, String> filter = new HashMap<String, String>();

        filter.put(DBSchema.GROUP_TYPES_ID, String.valueOf(typeId));

        ArrayList<Map<String, String>> result =
                man.select(DBSchema.T_GROUP_TYPES, filter);

        return result.get(0).get(DBSchema.GROUP_TYPES_EXCLUSIVE).equals("1");
    }

    public int getUserId(Player player) {
        HashMap<String, String> filter = new HashMap<String, String>();

        filter.put(DBSchema.USERS_USERNAME, player.getName());

        ArrayList<Map<String, String>> result =
                man.select(DBSchema.T_USERS, filter);

        return result.isEmpty() ? 0 : Integer.parseInt(result.get(0).get(DBSchema.USERS_ID));
    }

    public int getGroupId(String name) {
        HashMap<String, String> filter = new HashMap<String, String>();

        filter.put(DBSchema.GROUPS_NAME, name);

        ArrayList<Map<String, String>> result =
                man.select(DBSchema.T_GROUPS, filter);

        return result.isEmpty() ? 0 : Integer.parseInt(result.get(0).get(DBSchema.GROUPS_ID));
    }

    public int getOwnerId(int x, int z) {
        x = x / Config.BLOCK_DIMENSION;
        z = z / Config.BLOCK_DIMENSION;

        HashMap<String, String> filter = new HashMap<String, String>();

        filter.put(DBSchema.BLOCKS_X_COORD, String.valueOf(x));
        filter.put(DBSchema.BLOCKS_Z_COORD, String.valueOf(z));

        ArrayList<Map<String, String>> result =
                man.select(DBSchema.T_BLOCKS, filter);

        return result.isEmpty() ? 0 : Integer.parseInt(result.get(0).get(DBSchema.BLOCKS_GROUP_ID));
    }

    public int getTypeId(String name) {
        HashMap<String, String> filter = new HashMap<String, String>();

        filter.put(DBSchema.GROUP_TYPES_NAME, name);

        ArrayList<Map<String, String>> result =
                man.select(DBSchema.T_GROUP_TYPES, filter);

        return result.isEmpty() ? 0 : Integer.parseInt(result.get(0).get(DBSchema.GROUP_TYPES_ID));
    }

    public String getTypeName(int id) {
        HashMap<String, String> filter = new HashMap<String, String>();

        filter.put(DBSchema.GROUP_TYPES_ID, String.valueOf(id));

        ArrayList<Map<String, String>> result =
                man.select(DBSchema.T_GROUP_TYPES, filter);

        return result.isEmpty() ? "" : result.get(0).get(DBSchema.GROUP_TYPES_NAME);
    }

    public boolean isPlayerGrouptypeMember(int playerId, int typeId) {
        HashMap<String, String> filter = new HashMap<String, String>();

        filter.put(DBSchema.MEMBERSHIPS_USER_ID, String.valueOf(playerId));
        filter.put(DBSchema.GROUPS_TYPE_ID, String.valueOf(typeId));

        ArrayList<Map<String, String>> result =
                man.join(DBSchema.T_MEMBERSHIPS, DBSchema.T_GROUPS,
                DBSchema.MEMBERSHIPS_GROUP_ID, DBSchema.GROUPS_ID, "*", filter);

        return !result.isEmpty();
    }

    public boolean isLeader(String leagueName, Player player) {
        int playerId = getUserId(player);

        HashMap<String, String> filter = new HashMap<String, String>();

        filter.put(DBSchema.GROUPS_LEADER_ID, String.valueOf(playerId));
        filter.put(DBSchema.GROUPS_NAME, leagueName);

        ArrayList<Map<String, String>> result =
                man.select(DBSchema.T_GROUPS, filter);

        return !result.isEmpty();
    }

    public boolean isGroupGrouptypeMember(int groupId, int typeId) {
        HashMap<String, String> filter = new HashMap<String, String>();

        filter.put(DBSchema.GROUP_MEMBERSHIPS_CLIENT_GROUP, String.valueOf(groupId));
        filter.put(DBSchema.GROUPS_TYPE_ID, String.valueOf(typeId));

        ArrayList<Map<String, String>> result =
                man.join(DBSchema.T_GROUP_MEMBERSHIPS, DBSchema.T_GROUPS,
                DBSchema.GROUP_MEMBERSHIPS_HOST_GROUP, DBSchema.GROUPS_ID, "*", filter);

        return !result.isEmpty();
    }

    public void registerPlayer(Player player) {
        if (isUserRegistered(player)) {
            return;
        }

        HashMap<String, String> data = new HashMap<String, String>();

        data.put(DBSchema.USERS_USERNAME, player.getName());

        man.insert(DBSchema.T_USERS, data);
    }

    private boolean isUserRegistered(Player player) {
        HashMap<String, String> filter = new HashMap<String, String>();

        filter.put(DBSchema.USERS_USERNAME, player.getName());

        ArrayList<Map<String, String>> result =
                man.select(DBSchema.T_USERS, filter);

        return !result.isEmpty();
    }

    public String getOwnerName(int x, int z) {
        int id = getOwnerId(x, z);

        if (id == 0) {
            return Config.EMPTY_BLOCK;
        } else {
            return getGroupName(id);
        }
    }

    public String getGroupName(int id) {
        HashMap<String, String> filter = new HashMap<String, String>();

        filter.put(DBSchema.GROUPS_ID, String.valueOf(id));

        ArrayList<Map<String, String>> result =
                man.select(DBSchema.T_GROUPS, filter);

        return result.isEmpty() ? "" : result.get(0).get(DBSchema.GROUPS_NAME);
    }

    public int getMinMemberCount(int typeId) {
        HashMap<String, String> filter = new HashMap<String, String>();

        filter.put(DBSchema.GROUP_TYPES_ID, String.valueOf(typeId));

        ArrayList<Map<String, String>> result =
                man.select(DBSchema.T_GROUP_TYPES, filter);

        return result.isEmpty() ? 0 : Integer.parseInt(result.get(0).get(DBSchema.GROUP_TYPES_MIN_MEMBERS));
    }

    public int getInitMemberCount(int typeId) {
        HashMap<String, String> filter = new HashMap<String, String>();

        filter.put(DBSchema.GROUP_TYPES_ID, String.valueOf(typeId));

        ArrayList<Map<String, String>> result =
                man.select(DBSchema.T_GROUP_TYPES, filter);

        return result.isEmpty() ? 0 : Integer.parseInt(result.get(0).get(DBSchema.GROUP_TYPES_INITIAL_MEMBERCOUNT));
    }

    public void createLeague(TempLeague league) {
        HashMap<String, String> data = new HashMap<String, String>();

        data.put(DBSchema.GROUPS_LEADER_ID, String.valueOf(league.getLeader()));
        data.put(DBSchema.GROUPS_NAME, league.getName());
        data.put(DBSchema.GROUPS_TYPE_ID, String.valueOf(league.getType()));
        data.put(DBSchema.GROUPS_RESTRICTIVE, Config.DEFAULT_RESTRICTIVE);

        man.insert(DBSchema.T_USERS, data);

        int groupId = getGroupId(league.getName());

        for (int p : league.getPlayers()) {
            data = new HashMap<String, String>();

            data.put(DBSchema.MEMBERSHIPS_USER_ID, String.valueOf(p));
            data.put(DBSchema.MEMBERSHIPS_GROUP_ID, String.valueOf(groupId));

            man.insert(DBSchema.T_MEMBERSHIPS, data);
        }

        for (int l : league.getLeagues()) {
            data = new HashMap<String, String>();

            data.put(DBSchema.GROUP_MEMBERSHIPS_CLIENT_GROUP, String.valueOf(l));
            data.put(DBSchema.GROUP_MEMBERSHIPS_HOST_GROUP, String.valueOf(groupId));

            man.insert(DBSchema.T_GROUP_MEMBERSHIPS, data);
        }
    }

    public String getLeaderName(String leagueName) {
        HashMap<String, String> filter = new HashMap<String, String>();

        filter.put(DBSchema.GROUPS_NAME, String.valueOf(leagueName));

        ArrayList<Map<String, String>> result =
                man.join(DBSchema.T_GROUPS, DBSchema.T_USERS,
                DBSchema.GROUPS_LEADER_ID, DBSchema.USERS_ID, filter);

        return result.isEmpty() ? "" : result.get(0).get(DBSchema.USERS_USERNAME);
    }

    public String getLeaguesTypeName(String leagueName) {
        HashMap<String, String> filter = new HashMap<String, String>();

        filter.put(DBSchema.GROUPS_NAME, String.valueOf(leagueName));

        ArrayList<Map<String, String>> result =
                man.join(DBSchema.T_GROUPS, DBSchema.T_GROUP_TYPES,
                DBSchema.GROUPS_TYPE_ID, DBSchema.GROUP_TYPES_ID, filter);

        return result.isEmpty() ? "" : result.get(0).get(DBSchema.GROUP_TYPES_NAME);
    }

    public boolean hostAcceptsClient(int host, int client) {
        HashMap<String, String> filter = new HashMap<String, String>();

        filter.put(DBSchema.TYPES_ACCEPT_HOST_TYPE, String.valueOf(host));
        filter.put(DBSchema.TYPES_ACCEPT_CLIENT_TYPE, String.valueOf(client));

        ArrayList<Map<String, String>> result =
                man.select(DBSchema.T_TYPES_ACCEPT, filter);

        return !result.isEmpty();
    }

    public boolean hasOpRights(String name, Player player) {
        if (isLeader(name, player)) {
            return true;
        }

        int playerId = getUserId(player);
        int groupId = getGroupId(name);

        HashMap<String, String> filter = new HashMap<String, String>();

        filter.put(DBSchema.OPERATORS_USER_ID, String.valueOf(playerId));
        filter.put(DBSchema.OPERATORS_GROUP_ID, String.valueOf(groupId));

        ArrayList<Map<String, String>> result =
                man.select(DBSchema.T_OPERATORS, filter);

        return !result.isEmpty();
    }

    public void claim(int x, int z, int groupID) {

        HashMap<String, String> data = new HashMap<String, String>();

        data.put(DBSchema.BLOCKS_X_COORD, String.valueOf(x));
        data.put(DBSchema.BLOCKS_Z_COORD, String.valueOf(z));
        data.put(DBSchema.BLOCKS_GROUP_ID, String.valueOf(groupID));

        man.insert(DBSchema.T_USERS, data);
    }

    public int getMemberCount(String leagueName) {
        int memberCount = 0;

        int leagueID = getGroupId(leagueName);

        HashMap<String, String> filter = new HashMap<String, String>();

        filter.put(DBSchema.MEMBERSHIPS_GROUP_ID, String.valueOf(leagueID));

        ArrayList<Map<String, String>> result =
                man.select(DBSchema.T_MEMBERSHIPS, filter);

        memberCount += result.size();

        filter = new HashMap<String, String>();

        filter.put(DBSchema.GROUP_MEMBERSHIPS_HOST_GROUP, String.valueOf(leagueID));

        result = man.select(DBSchema.T_GROUP_MEMBERSHIPS, filter);

        memberCount += result.size();

        return memberCount;
    }

    public int getPlotSize(String leagueName) {
        int leagueID = getGroupId(leagueName);

        HashMap<String, String> filter = new HashMap<String, String>();

        filter.put(DBSchema.BLOCKS_GROUP_ID, String.valueOf(leagueID));

        ArrayList<Map<String, String>> result =
                man.select(DBSchema.T_BLOCKS, filter);

        return result.size();
    }

    public int getMaxPlotSize(String leagueName) {
        HashMap<String, String> filter = new HashMap<String, String>();

        filter.put(DBSchema.GROUPS_NAME, leagueName);

        ArrayList<Map<String, String>> result =
                man.join(DBSchema.T_GROUPS, DBSchema.T_GROUP_TYPES,
                DBSchema.GROUPS_TYPE_ID, DBSchema.GROUP_TYPES_ID, filter);

        int minBlocks = Integer.parseInt(result.get(0)
                .get(DBSchema.GROUP_TYPES_MIN_BLOCKS));
        int blocksPerMember = Integer.parseInt(result.get(0)
                .get(DBSchema.GROUP_TYPES_BLOCKS_PER_MEMBER));

        int blocksAllowed = blocksPerMember * getMemberCount(leagueName);

        if (blocksAllowed < minBlocks) {
            blocksAllowed = minBlocks;
        }

        return blocksAllowed;
    }
}
