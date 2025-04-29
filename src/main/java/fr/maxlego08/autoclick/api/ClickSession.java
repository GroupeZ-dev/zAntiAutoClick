package fr.maxlego08.autoclick.api;

import java.util.List;
import java.util.UUID;

public interface ClickSession {

    UUID getUniqueId();

    int getId();

    long getDuration();

    List<Integer> getDifferences();

    boolean isValid();

    double getCheatPercent();

    double getMedian();

    double getAverage();

    double getStandardDivision();

    UUID getVerifiedBy();

    long getVerifiedAt();

    boolean isCheat();

}
