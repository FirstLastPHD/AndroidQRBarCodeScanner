package tajmac.zps.com.qbreader.helpers.util.database;

import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;
import tajmac.zps.com.qbreader.helpers.constant.ColumnNames;

public abstract class BaseEntity implements Parcelable {
    /**
     * Fields
     */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ColumnNames.ID)
    @NonNull
    //public long mId;
    public int mId;

    /**
     * Getter and setter methods of the model
     */
    public int getId() {
        return mId;
    }
   // public long getId() {
       // return mId;
    //}
}
