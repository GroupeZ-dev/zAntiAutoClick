package fr.maxlego08.autoclick.storage.dto;

import java.util.Date;
import java.util.UUID;

public record SessionDTO(int id, UUID unique_id, String differences, Date started_at, Date finished_at) {
}
