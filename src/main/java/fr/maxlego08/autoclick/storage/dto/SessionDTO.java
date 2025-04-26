package fr.maxlego08.autoclick.storage.dto;

import fr.maxlego08.autoclick.Config;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public record SessionDTO(int id, UUID unique_id, String differences, Date started_at, Date finished_at) {

    public List<Integer> getDifferences() {
        return this.differences == null || this.differences.isEmpty() ? List.of() : Arrays.stream(this.differences.split(",")).filter(e -> !e.isEmpty()).map(Integer::parseInt).toList();
    }

    public long getDuration() {
        return this.finished_at.getTime() - this.started_at.getTime();
    }

    public boolean isValid() {
        return this.getDifferences().size() >= Config.minimumSessionClicks && this.getDuration() >= Config.minimumSessionDuration;
    }

}
