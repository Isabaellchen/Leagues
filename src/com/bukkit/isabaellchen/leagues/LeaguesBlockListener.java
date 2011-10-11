package com.bukkit.isabaellchen.leagues;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockInteractEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * Groupy block listener
 * @author Isabaellchen
 */
public class LeaguesBlockListener extends BlockListener {

    private final Leagues plugin;

    public LeaguesBlockListener(final Leagues plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onBlockInteract(final BlockInteractEvent event) {

        if (event.isCancelled()) {
            return;
        }

        Callable<Boolean> callable = new Callable<Boolean>() {

            public Boolean call() {
                Player player;
                if (event.isPlayer()) {
                    player = (Player) event.getEntity();
                } else {
                    return false;
                }
                return Leagues.DB.isActionAllowed(player, event.getBlock());
            }
        };
        Future<Boolean> allowed = Leagues.WORKER.submit(callable);

        try {
            event.setCancelled(!allowed.get());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBlockDamage(final BlockDamageEvent event) {

        if (event.isCancelled()) {
            return;
        }

        Callable<Boolean> callable = new Callable<Boolean>() {

            public Boolean call() {
                return Leagues.DB.isActionAllowed(event.getPlayer(), event.getBlock());
            }
        };
        Future<Boolean> allowed = Leagues.WORKER.submit(callable);

        try {
            event.setCancelled(!allowed.get());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBlockPlace(final BlockPlaceEvent event) {

        if (event.isCancelled()) {
            return;
        }

        Callable<Boolean> callable = new Callable<Boolean>() {

            public Boolean call() {
                return Leagues.DB.isActionAllowed(event.getPlayer(), event.getBlockAgainst());
            }
        };
        Future<Boolean> allowed = Leagues.WORKER.submit(callable);

        try {
            event.setCancelled(!allowed.get());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
