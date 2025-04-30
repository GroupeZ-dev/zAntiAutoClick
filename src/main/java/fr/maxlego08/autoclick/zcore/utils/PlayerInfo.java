package fr.maxlego08.autoclick.zcore.utils;

import fr.maxlego08.autoclick.api.ClickSession;

import java.util.List;
import java.util.UUID;

public class PlayerInfo {

    private final UUID uniqueId;
    private final List<ClickSession> clickSessions;

    public PlayerInfo(UUID uniqueId, List<ClickSession> clickSessions) {
        this.uniqueId = uniqueId;
        this.clickSessions = clickSessions;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public List<ClickSession> getClickSessions() {
        return clickSessions;
    }
}
