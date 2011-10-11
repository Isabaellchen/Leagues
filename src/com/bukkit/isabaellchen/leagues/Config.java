package com.bukkit.isabaellchen.leagues;

import org.bukkit.ChatColor;

/**
 *
 * @author Isa
 */
public class Config {

    public static int WORKLOAD_FACTOR = 16;
    public static int MINIMUM_THREADS = 2;
    public static String EMPTY_BLOCK = "empty";
    public static int BLOCK_DIMENSION = 10;
    public static String TEXTCOLOR_SUCCESS = ChatColor.GREEN.toString();
    public static String TEXTCOLOR_FAIL = ChatColor.RED.toString();
    public static String TEXTCOLOR_SYS = ChatColor.GOLD.toString();
    public static boolean VERBOSE = true;
    public static String POOLNAME = "leagues";
    public static String DEFAULT_RESTRICTIVE = "0";
    public static String[] FORBIDDEN_GROUP_NAMES = {"accept"};
}
