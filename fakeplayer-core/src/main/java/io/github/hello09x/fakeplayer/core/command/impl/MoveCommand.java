package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.inject.Singleton;
import io.github.hello09x.fakeplayer.core.Main;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Singleton
public class MoveCommand extends AbstractCommand {

    private final Map<UUID, BukkitTask> stopTasks = new HashMap<>();

    public void move(@NotNull Player fake, @Range(from = -1, to = 1) float forward, @Range(from = -1, to = 1) float strafing) {
        var handle = bridge.fromPlayer(fake);
        float vel = fake.isSneaking() ? 0.3F : 1.0F;
        if (forward != 0.0F) {
            handle.setZza(vel * forward);
        }
        if (strafing != 0.0F) {
            handle.setXxa(vel * strafing);
        }

        var task = stopTasks.remove(fake.getUniqueId());
        if (task != null && !task.isCancelled()) {
            task.cancel();
        }

        var fakeId = fake.getUniqueId();
        var stopping = new BukkitRunnable() {
            @Override
            public void run() {
                handle.setXxa(0);
                handle.setZza(0);
                var self = stopTasks.get(fakeId);
                if (self != null && self.getTaskId() == this.getTaskId()) {
                    stopTasks.remove(fakeId);
                }
            }
        };

        this.stopTasks.put(fakeId, stopping.runTaskLater(Main.getInstance(), fake.isSprinting() ? 40 : 20));
    }

}
