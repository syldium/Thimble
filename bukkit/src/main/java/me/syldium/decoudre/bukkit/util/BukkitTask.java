package me.syldium.decoudre.bukkit.util;

import me.syldium.decoudre.common.util.Task;
import org.bukkit.Bukkit;

public class BukkitTask implements Task {

    private final int id;

    public BukkitTask(int id) {
        this.id = id;
    }

    @Override
    public void cancel() {
        Bukkit.getScheduler().cancelTask(this.id);
    }
}
