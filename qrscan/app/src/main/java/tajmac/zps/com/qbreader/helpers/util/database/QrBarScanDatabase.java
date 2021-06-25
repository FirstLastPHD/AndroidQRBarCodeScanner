package tajmac.zps.com.qbreader.helpers.util.database;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import tajmac.zps.com.qbreader.R;
import tajmac.zps.com.qbreader.helpers.model.Code;
import tajmac.zps.com.qbreader.helpers.model.CodeDao;

@Database(entities = {Code.class},
        version = 1, exportSchema = false)
public abstract class QrBarScanDatabase extends AppDatabase {

    private static volatile QrBarScanDatabase sInstance;

    // Get a database instance
    public static synchronized QrBarScanDatabase on() {
        return sInstance;
    }

    public static synchronized void init(Context context) {

        if (sInstance == null) {
            synchronized (QrBarScanDatabase.class) {
                sInstance = createDb(context, context.getString(R.string.app_name), QrBarScanDatabase.class);
                //Log.d(sInstance.toString(), "sInstance: ");
            }
        }
    }

    public abstract CodeDao codeDao();
}
