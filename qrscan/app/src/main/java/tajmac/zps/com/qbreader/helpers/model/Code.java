package tajmac.zps.com.qbreader.helpers.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.Ignore;
import tajmac.zps.com.qbreader.helpers.constant.TableNames;
import tajmac.zps.com.qbreader.helpers.util.database.BaseEntity;

@Entity(tableName = TableNames.CODES)
public class Code extends BaseEntity {

    /**
     * Constants
     */
    public static final int QR_CODE = 1;
    public static final int BAR_CODE = 2;
    public static final Parcelable.Creator<Code> CREATOR = new Parcelable.Creator<Code>() {
        @Override
        public Code createFromParcel(Parcel source) {
            return new Code(source);
        }

        @Override
        public Code[] newArray(int size) {
            return new Code[size];
        }
    };

    /**
     * Fields
     */
    private int mUserId;
    private String mContent;
    private  String mDescribe;
    private int mType;
    private String mCodeImagePath;
    //private byte[] mCodeImg;
    private long mTimeStamp;
    private String mDName;
    private String mDSerial;



    public Code() {
    }

    @Ignore
    public Code(String content, int type) {
        mContent = content;
        mType = type;
    }

    @Ignore
    public Code(String content, int type, long timeStamp) {
        mContent = content;
        mType = type;
        mTimeStamp = timeStamp;
    }

    @Ignore
    public Code(String content, String describe, int type){
        mContent = content;
        mDescribe = describe;
        mType = type;
    }

    @Ignore
    public Code(int userId,String content, String describe, int type, String codeImagePath,byte[] mCodeImg, long timeStamp, String mDName, String mDSerial ) {

        mUserId = userId;
        mContent = content;
        mDescribe = describe;
        mType = type;
        mCodeImagePath = codeImagePath;
        mCodeImg = mCodeImg;
        mTimeStamp = timeStamp;
        mDName = mDName;
        mDSerial = mDSerial;

    }

    @Ignore
    public Code(String content, int type, String codeImagePath,  long timeStamp) {
        mContent = content;
        mType = type;
        mCodeImagePath = codeImagePath;
        mTimeStamp = timeStamp;
    }

    @Ignore
    protected Code(Parcel in) {
        this.mUserId = in.readInt();
        this.mContent = in.readString();
        this.mDescribe = in.readString();
        this.mType = in.readInt();
        this.mTimeStamp = in.readLong();
        this.mCodeImagePath = in.readString();
        //this.mCodeImg = in.readByteArray();
    }


    public int getUserId() {
        return mUserId;
    }

    public void setUserId(int userId) {
        this.mUserId = userId;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public String getDescribe() {
        return mDescribe;
    }

    public void setDescribe(String describe ){ mDescribe= describe;  }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        mType = type;
    }

    public String getCodeImagePath() {
        return mCodeImagePath;
    }

    public void setCodeImagePath(String codeImagePath) {
        mCodeImagePath = codeImagePath;
    }

    public long getTimeStamp() {
        return mTimeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        mTimeStamp = timeStamp;
    }

    public void setmContent(String mContent) {
        this.mContent = mContent;
    }


    /*public byte[] getMCodeImg() {
        return mCodeImg;
    }

    public void setMCodeImg(byte[] mCodeImg) {
        this.mCodeImg = mCodeImg;
    }
*/
    public void setMDName(String mDName) {
        this.mDName = mDName;
    }

    public void setMDSerial(String mDSerial) {
        this.mDSerial = mDSerial;
    }



    public String getMDName() {
        return mDName;
    }

    public String getMDSerial() {
        return mDSerial;
    }

    /**
     * Below codes are written in order to make the object parcelable
     */
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mUserId);
        dest.writeString(this.mContent);
        dest.writeString(this.mDescribe);
        dest.writeInt(this.mType);
        dest.writeLong(this.mTimeStamp);
        dest.writeString(this.mCodeImagePath);
        //dest.writeByteArray(this.mCodeImg);
        dest.writeString(this.mDName);
        dest.writeString(this.mDSerial);
    }
}
