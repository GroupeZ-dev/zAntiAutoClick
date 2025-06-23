package fr.maxlego08.autoclick.api.storage.dto;

import java.util.UUID;

public record InvalidSessionDTO(int id, int session_id, double result, double average, double median,
                                double standard_deviation, UUID verified_by, Long verified_at) {


}
