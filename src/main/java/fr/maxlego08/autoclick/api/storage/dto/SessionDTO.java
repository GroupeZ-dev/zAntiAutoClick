package fr.maxlego08.autoclick.api.storage.dto;

import fr.maxlego08.autoclick.zcore.utils.Config;
import fr.maxlego08.autoclick.api.ClickSession;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public record SessionDTO(int id, UUID unique_id, String differences, Date started_at, Date finished_at) implements ClickSession {

    @Override
    public List<Integer> getDifferences() {
        return this.differences == null || this.differences.isEmpty() ? List.of() : Arrays.stream(this.differences.split(",")).filter(e -> !e.isEmpty()).map(Integer::parseInt).toList();
    }

    @Override
    public long getDuration() {
        return this.finished_at.getTime() - this.started_at.getTime();
    }

    @Override
    public boolean isValid() {
        return this.getDifferences().size() >= Config.minimumSessionClicks && this.getDuration() >= Config.minimumSessionDuration;
    }

    @Override
    public double getCheatPercent() {
        return 0;
    }

    @Override
    public double getMedian() {
        return 0;
    }

    @Override
    public double getAverage() {
        return 0;
    }

    @Override
    public double getStandardDivision() {
        return 0;
    }

    @Override
    public UUID getVerifiedBy() {
        return null;
    }

    @Override
    public long getVerifiedAt() {
        return 0;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public UUID getUniqueId() {
        return this.unique_id;
    }

    @Override
    public boolean isCheat() {
        return false;
    }

    @Override
    public long getStartedAt() {
        return this.started_at.getTime();
    }

    @Override
    public long getFinishedAt() {
        return this.finished_at.getTime();
    }

    @Override
    public void update(double average, double median, double standardDeviation, double cheatPercent) {

    }
}
