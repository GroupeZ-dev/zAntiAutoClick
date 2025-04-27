package fr.maxlego08.autoclick;

import fr.maxlego08.autoclick.api.ClickSession;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Session implements ClickSession {

    private final UUID uuid;
    private final long startedAt;
    private final List<Integer> differences = new ArrayList<>();
    private long finishedAt;

    private long lastClickAt;
    private BukkitTask task;

    public Session(UUID uuid, long startedAt) {
        this.uuid = uuid;
        this.startedAt = startedAt;
    }

    public long getStartedAt() {
        return startedAt;
    }

    @Override
    public List<Integer> getDifferences() {
        return differences;
    }

    public long getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(long finishedAt) {
        this.finishedAt = finishedAt;
        this.lastClickAt = 0;
    }

    public BukkitTask getTask() {
        return task;
    }

    public void setTask(BukkitTask task) {
        this.task = task;
    }

    public long getLastClickAt() {
        return lastClickAt;
    }

    public void setLastClickAt(long lastClickAt) {
        this.lastClickAt = lastClickAt;
    }

    public void addDifferences(int difference) {
        this.differences.add(difference);
    }

    public int count() {
        return this.differences.size();
    }

    @Override
    public long getDuration() {
        return this.finishedAt - this.startedAt;
    }

    @Override
    public boolean isValid() {
        return this.differences.size() >= Config.minimumSessionClicks && this.getDuration() >= Config.minimumSessionDuration;
    }

    @Override
    public UUID getUniqueId() {
        return this.uuid;
    }

    @Override
    public int getId() {
        return -1;
    }
}
