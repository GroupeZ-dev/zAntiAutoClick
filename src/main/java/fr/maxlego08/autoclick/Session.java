package fr.maxlego08.autoclick;

import fr.maxlego08.autoclick.api.ClickSession;
import fr.maxlego08.autoclick.api.storage.dto.InvalidSessionDTO;
import fr.maxlego08.autoclick.zcore.utils.Config;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Session implements ClickSession {

    private final UUID uniqueId;
    private final long startedAt;
    private final List<Integer> differences;
    private long finishedAt;

    private InvalidSessionDTO invalidSession;

    private int id = -1;
    private long lastClickAt;
    private BukkitTask task;

    private double average;
    private double median;
    private double standardDeviation;
    private double cheatPercent;

    public Session(UUID uniqueId, long startedAt) {
        this.uniqueId = uniqueId;
        this.startedAt = startedAt;
        this.differences = new ArrayList<>();
    }

    public Session(UUID uniqueId, long startedAt, long finishedAt, List<Integer> differences) {
        this.uniqueId = uniqueId;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
        this.differences = differences;
    }

    @Override
    public long getStartedAt() {
        return startedAt;
    }

    @Override
    public void update(double average, double median, double standardDeviation, double cheatPercent) {
        this.average = average;
        this.median = median;
        this.standardDeviation = standardDeviation;
        this.cheatPercent = cheatPercent;
    }

    @Override
    public List<Integer> getDifferences() {
        return differences;
    }

    @Override
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
    public double getCheatPercent() {
        return this.invalidSession == null ? this.cheatPercent : this.invalidSession.result();
    }

    @Override
    public double getMedian() {
        return this.invalidSession == null ? this.median : this.invalidSession.median();
    }

    @Override
    public double getAverage() {
        return this.invalidSession == null ? this.average : this.invalidSession.average();
    }

    @Override
    public double getStandardDivision() {
        return this.invalidSession == null ? this.standardDeviation : this.invalidSession.standard_deviation();
    }

    @Override
    public UUID getVerifiedBy() {
        return this.invalidSession == null ? null : this.invalidSession.verified_by();
    }

    @Override
    public long getVerifiedAt() {
        return this.invalidSession == null ? 0 : this.invalidSession.verified_at();
    }

    @Override
    public UUID getUniqueId() {
        return this.uniqueId;
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public InvalidSessionDTO getInvalidSession() {
        return invalidSession;
    }

    public void setInvalidSession(InvalidSessionDTO invalidSession) {
        this.invalidSession = invalidSession;
    }

    @Override
    public boolean isCheat() {
        return this.invalidSession != null;
    }
}
