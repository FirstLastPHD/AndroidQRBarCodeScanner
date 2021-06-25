package tajmac.zps.com.qbreader.helpers.model;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Query;
import io.reactivex.Flowable;
import io.reactivex.Single;
import tajmac.zps.com.qbreader.helpers.constant.TableNames;
import tajmac.zps.com.qbreader.helpers.util.database.BaseDao;

@Dao

public interface CodeDao extends BaseDao<Code> {
    @Query("SELECT * FROM " + TableNames.CODES)
    Single<List<Code>> getAllFlowableCodes();

}
