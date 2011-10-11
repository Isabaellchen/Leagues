/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bukkit.isabaellchen.leagues;

/**
 *
 * @author Isa
 */
public class DBSchema {

    //TABLE PREFIX
    public static String prefix = "mcl_";
    //TABLES
    public static String T_USERS = prefix + "users";
    public static String T_MEMBERSHIPS = prefix + "memberships";
    public static String T_GROUPS = prefix + "groups";
    public static String T_GROUP_MEMBERSHIPS = prefix + "group_memberships";
    public static String T_OPERATORS = prefix + "operators";
    public static String T_BLOCKS = prefix + "blocks";
    public static String T_GROUP_TYPES = prefix + "group_types";
    public static String T_TYPES_ACCEPT = prefix + "types_accept";
    //VIEWS
    public static String V_MEMBERSHIPS_ALL = prefix + "memberships_all";
    public static String V_MEMBERSHIPS_NORSTC = prefix + "memberships_norstc";
    //TABLE USERS
    public static String USERS_ID = "id";
    public static String USERS_USERNAME = "username";
    //TABLE MEMBERSHIPS
    public static String MEMBERSHIPS_ID = "id";
    public static String MEMBERSHIPS_USER_ID = "user_id";
    public static String MEMBERSHIPS_GROUP_ID = "group_id";
    //TABLE GROUPS
    public static String GROUPS_ID = "id";
    public static String GROUPS_NAME = "name";
    public static String GROUPS_LEADER_ID = "leader_id";
    public static String GROUPS_TYPE_ID = "type_id";
    public static String GROUPS_RESTRICTIVE = "restrictive";
    //TABLE GROUP MEMBERSHIPS
    public static String GROUP_MEMBERSHIPS_ID = "id";
    public static String GROUP_MEMBERSHIPS_HOST_GROUP = "host_group";
    public static String GROUP_MEMBERSHIPS_CLIENT_GROUP = "client_group";
    //TABLE OPERATORS
    public static String OPERATORS_ID = "id";
    public static String OPERATORS_USER_ID = "user_id";
    public static String OPERATORS_GROUP_ID = "group_id";
    //TABLE BLOCKS
    public static String BLOCKS_ID = "id";
    public static String BLOCKS_X_COORD = "x_coord";
    public static String BLOCKS_Z_COORD = "z_coord";
    public static String BLOCKS_GROUP_ID = "group_id";
    //TABLE GROUP TYPES
    public static String GROUP_TYPES_ID = "id";
    public static String GROUP_TYPES_NAME = "name";
    public static String GROUP_TYPES_EXCLUSIVE = "exclusive";
    public static String GROUP_TYPES_MIN_MEMBERS = "min_members";
    public static String GROUP_TYPES_INITIAL_MEMBERCOUNT = "initial_membercount";
    public static String GROUP_TYPES_MIN_BLOCKS = "min_blocks";
    public static String GROUP_TYPES_BLOCKS_PER_MEMBER = "blocks_per_member";
    //TABLE TYPES ACCEPT
    public static String TYPES_ACCEPT_ID = "id";
    public static String TYPES_ACCEPT_HOST_TYPE = "host_type";
    public static String TYPES_ACCEPT_CLIENT_TYPE = "client_type";
    //VIEW MEMBERSHIPS ALL
    public static String MEMBERSHIPS_ALL_USER_ID = "user_id";
    public static String MEMBERSHIPS_ALL_GROUP_ID = "group_id";
    //VIEW MEMBERSHIPS NO RESTRICTION
    public static String MEMBERSHIPS_NORSTC_USER_ID = "user_id";
    public static String MEMBERSHIPS_NORSTC_GROUP_ID = "group_id";
    //CREATE VIEW MEMBERSHIPS ALL
    public static String CREATE_VIEW_MEMBERSHIPS_ALL =
            "CREATE OR REPLACE VIEW " + V_MEMBERSHIPS_ALL + " AS "
            + "SELECT " + T_MEMBERSHIPS + "." + MEMBERSHIPS_USER_ID + " AS " + MEMBERSHIPS_ALL_USER_ID
            + ", " + T_GROUP_MEMBERSHIPS + "." + GROUP_MEMBERSHIPS_HOST_GROUP + " AS " + MEMBERSHIPS_ALL_GROUP_ID + " "
            + "FROM " + T_MEMBERSHIPS + " JOIN " + T_GROUP_MEMBERSHIPS + " "
            + "ON " + T_MEMBERSHIPS + "." + MEMBERSHIPS_GROUP_ID + " = " + T_GROUP_MEMBERSHIPS + "." + GROUP_MEMBERSHIPS_CLIENT_GROUP;
    //CREATE VIEW MEMBERSHIPS ALL
    public static String CREATE_VIEW_MEMBERSHIPS_NORSTC =
            "CREATE OR REPLACE VIEW " + V_MEMBERSHIPS_NORSTC + " AS "
            + "SELECT " + T_MEMBERSHIPS + "." + MEMBERSHIPS_USER_ID + " AS " + MEMBERSHIPS_NORSTC_USER_ID
            + ", " + T_GROUP_MEMBERSHIPS + "." + GROUP_MEMBERSHIPS_HOST_GROUP + " AS " + MEMBERSHIPS_NORSTC_GROUP_ID + " "
            + "FROM " + T_MEMBERSHIPS + " JOIN " + T_GROUP_MEMBERSHIPS + " "
            + "ON " + T_MEMBERSHIPS + "." + MEMBERSHIPS_GROUP_ID + " = " + T_GROUP_MEMBERSHIPS + "." + GROUP_MEMBERSHIPS_CLIENT_GROUP + " "
            + "JOIN " + T_GROUPS + " "
            + "ON " + T_GROUP_MEMBERSHIPS + "." + GROUP_MEMBERSHIPS_HOST_GROUP + " = " + T_GROUPS + "." + GROUPS_ID + " "
            + "WHERE " + T_GROUPS + "." + GROUPS_RESTRICTIVE + " = 0";
}
