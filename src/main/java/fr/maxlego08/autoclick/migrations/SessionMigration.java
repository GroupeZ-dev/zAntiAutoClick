package fr.maxlego08.autoclick.migrations;

import fr.maxlego08.autoclick.api.storage.Tables;
import fr.maxlego08.sarah.database.Migration;

public class SessionMigration extends Migration {
    @Override
    public void up() {
        this.create(Tables.SESSIONS, table -> {
            table.autoIncrement("id");
            table.uuid("unique_id");
            table.longText("differences");
            table.timestamp("started_at");
            table.timestamp("finished_at");
        });
    }
}
