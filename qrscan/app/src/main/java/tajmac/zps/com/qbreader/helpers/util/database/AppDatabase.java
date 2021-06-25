package tajmac.zps.com.qbreader.helpers.util.database;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public abstract class AppDatabase extends RoomDatabase {
    protected static <T extends RoomDatabase> T createDb(Context context, String dbName,
                                                         Class<T> dbService,
                                                         String... migrationScripts) {
        RoomDatabase.Builder<T> builder = Room.databaseBuilder(context, dbService, dbName);
        Log.d(context.toString(), "createDb: ");
        for (Migration migration : getMigrations(migrationScripts)) {
            builder.addMigrations(migration);
        }
        Log.d(migrationScripts.toString(), "createDbBuild: ");
        //context.deleteDatabase(dbName);
        return builder.build();
    }

    private static List<Migration> getMigrations(String... migrationScripts) {
        List<Migration> migrationList = new ArrayList<>();

        int startVersion = 1;
        int endVersion = 2;

        Migration migration;

        for (final String migrationSchema : migrationScripts) {
            migration = new Migration(startVersion, endVersion) {
                @Override
                public void migrate(@NonNull SupportSQLiteDatabase database) {
                    Log.d(migrationSchema, "migrate1111: ");
                    database.execSQL(migrationSchema);
                }
            };

            startVersion++;
            endVersion++;

            migrationList.add(migration);
        }

        return migrationList;
    }
}
