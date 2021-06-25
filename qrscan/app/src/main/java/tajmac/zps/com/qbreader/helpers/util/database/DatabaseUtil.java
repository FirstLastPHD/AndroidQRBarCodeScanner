package tajmac.zps.com.qbreader.helpers.util.database;

import android.content.Context;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import tajmac.zps.com.qbreader.helpers.model.Code;
import tajmac.zps.com.qbreader.helpers.model.CodeDao;

public class DatabaseUtil /*extends SQLiteOpenHelper*/ {
    /**
     * Fields
     */
    private static DatabaseUtil sInstance;
    private CodeDao mCodeDao;

    private DatabaseUtil() {

        setCodeDao(tajmac.zps.com.qbreader.helpers.util.database.QrBarScanDatabase.on().codeDao());
    }



    /**
     * This method builds an instance
     */
    public static void init(Context context) {
        tajmac.zps.com.qbreader.helpers.util.database.QrBarScanDatabase.init(context);

        if (sInstance == null) {
            sInstance = new DatabaseUtil();
        }
    }

    public static DatabaseUtil on() {
        if (sInstance == null) {
            sInstance = new DatabaseUtil();
        }

        return sInstance;
    }

    private CodeDao getCodeDao() {
        return mCodeDao;
    }

    private void setCodeDao(CodeDao codeDao) {
        mCodeDao = codeDao;
    }

    public Completable insertCode(Code code) {
        return getCodeDao().insert(code);
    }
    
    public Single/*Flowable*/<List<Code>> getAllCodes() {
        return getCodeDao().getAllFlowableCodes();
    }



    public int deleteEntity(Code code) {
        return getCodeDao().delete(code);
    }


}
