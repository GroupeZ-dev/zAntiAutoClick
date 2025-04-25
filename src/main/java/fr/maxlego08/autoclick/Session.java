package fr.maxlego08.autoclick;

import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class Session {

    private final long startedAt;
    private final List<Long> differences = new ArrayList<>();
    private long finishedAt;

    private long lastClickAt;
    private BukkitTask task;

    public Session(long startedAt) {
        this.startedAt = startedAt;
    }

    public long getStartedAt() {
        return startedAt;
    }

    public List<Long> getDifferences() {
        return differences;
    }

    public long getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(long finishedAt) {
        this.finishedAt = finishedAt;
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

    public void addDifferences(long difference) {
        this.differences.add(difference);
    }

    public int count() {
        return this.differences.size();
    }

    public long getDuration() {
        return this.finishedAt - this.startedAt;
    }
}
