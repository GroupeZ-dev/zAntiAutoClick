package fr.maxlego08.autoclick.migrations;

import fr.maxlego08.autoclick.api.storage.Tables;
import fr.maxlego08.sarah.database.Migration;

public class InvalidSessionMigration extends Migration {
    @Override
    public void up() {
        this.create(Tables.INVALID_SESSIONS, table -> {
            table.autoIncrement("id");
            table.integer("session_id").foreignKey(Tables.SESSIONS, "id", true);
            table.decimal("result", 65, 2);
            table.decimal("average", 65, 2);
            table.decimal("median", 65, 2);
            table.decimal("standard_deviation", 65, 2);
            table.uuid("verified_by").nullable();
            table.timestamp("verified_at").nullable();
        });
    }
}
