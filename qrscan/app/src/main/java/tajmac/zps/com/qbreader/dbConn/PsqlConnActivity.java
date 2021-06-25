package tajmac.zps.com.qbreader.dbConn;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;
import com.google.zxing.WriterException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tajmac.zps.com.qbreader.R;
import tajmac.zps.com.qbreader.additions.HelperMethods;
import tajmac.zps.com.qbreader.helpers.model.Code;
import tajmac.zps.com.qbreader.helpers.model.CodeDao;
import tajmac.zps.com.qbreader.helpers.util.ProgressDialogUtil;
import tajmac.zps.com.qbreader.helpers.util.database.DatabaseUtil;
import tajmac.zps.com.qbreader.restAPI.QRCodeRESTAPIService;
import tajmac.zps.com.qbreader.ui.scanresult.ScanResultActivity;


public abstract class PsqlConnActivity extends AppCompatActivity {

    private CompositeDisposable mCompositeDisposable;
    private Context mContext;
    private Code mCurrentCode;
    private CodeDao mCodeDao;
    public static   List<List<Code>> mItemListPSql33 = new ArrayList<List<Code>>();
    public List<Code> mItemListPSql343= new ArrayList<>();
    public boolean flagEqual33 = false;
    boolean psqlDBConn = false;
    static boolean synchFlag = false;



    public CompositeDisposable getCompositeDisposable() {
        return mCompositeDisposable;
    }

    public void setCompositeDisposable(CompositeDisposable compositeDisposable) {
        mCompositeDisposable = compositeDisposable;
    }

    public Code getCurrentCode() {
        return mCurrentCode;
    }

    public void setCurrentCode(Code currentCode) {
        mCurrentCode = currentCode;
    }

    private void setCodeDao(CodeDao codeDao) {
        mCodeDao = codeDao;
    }


    protected void onCreate(Bundle savedInstanceState) {

        //checkInternetConnection();
        super.onCreate(savedInstanceState);

        setCodeDao(tajmac.zps.com.qbreader.helpers.util.database.QrBarScanDatabase.on().codeDao());
        setCompositeDisposable(new CompositeDisposable());
        getEntriesFromPsql(mItemListPSql33,mContext);
        //playAd();


        //checkInternetConnection();
    }


/* MAzat */
   /* private void checkInternetConnection() {
        CompositeDisposable disposable = new CompositeDisposable();
        disposable.add(ReactiveNetwork
                .observeNetworkConnectivity(this)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(connectivity -> {
                    if (connectivity.state() == NetworkInfo.State.CONNECTED) {
                        //mBinding.adView.setVisibility(View.VISIBLE);
                        Toast.makeText(this, " Network State: Connected ... ", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, " Network State: Not Connected !!!... ", Toast.LENGTH_SHORT).show();
                        // mBinding.adView.setVisibility(View.GONE);
                    }

                }, throwable -> {
                    Toast.makeText(this, getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                }));
    }*/


    /* Get Entries from Psql DB */
    /* Get Entries from Psql DB */
    public List<List<Code>>  getEntriesFromPsql(List<List<Code>>mItemListPSql22, Context context){
        getCompositeDisposable().add(QRCodeRESTAPIService.getInstance()
                .getApi()
                .getAllRESTFlowableCodes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<List<Code>, Throwable>() {
                    @Override
                    public void accept(List<Code> codes33, Throwable throwable) throws Exception {
                        if (throwable != null) {
                           // Toast.makeText(context, "Data loading error", Toast.LENGTH_SHORT).show();
                        } else {
                            psqlDBConn = true;
                            mItemListPSql22.add(codes33);
                        }
                        // Log.d("beforeSize", "beforeSize");
                    }
                }));

        //this.mItemListPSql = mItemListPSql33.get(0);

        return mItemListPSql22;
    }


    /*  Comparing code  scanned entry with db entries */
    public boolean CombaringCodeWithDB(List<Code> mItemListPSql, boolean FlagEqual) {
        int i = 0;
        if (i == 0 && getCurrentCode() != null && mItemListPSql.size()>0){
            do {
                // Tohle selhava  a nevim proc
                if (mItemListPSql.get(i).getContent().equalsIgnoreCase(getCurrentCode().getContent())) {
                    FlagEqual = true;
                    break;
                }
                i++;

            } while (i < mItemListPSql.size());
            i = 0;
        }
        /* Kontrola */
        return FlagEqual;

    }

    /* FIll Post Data Array for psql DB Inserting */
    public HashMap<String, Object> FillPostArray(HashMap<String, Object> postData){
        Bitmap bitmap = null;
        try {
            bitmap = HelperMethods.encodeAsBitmap(getCurrentCode().getCodeImagePath());
        } catch (WriterException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] img1 = stream.toByteArray();
        String manufacturer = android.os.Build.MANUFACTURER;
        String serial = android.os.Build.SERIAL;
        /* !!!!!! Additional information !!!!!!! */
        /*!!!!!!!!!!!!!!!!!! tady dostavat user id pouzivatele pokud to bude nutny !!!!!!!!!!!!!!*/

        getCurrentCode().setUserId(1);
        getCurrentCode().setDescribe(" Put Additional code information here if it is necessary...  ");
        // getCurrentCode().setMCodeImg(img1);
        getCurrentCode().setMDName(manufacturer);
        getCurrentCode().setMDSerial(serial);
        Log.d(String.valueOf(getCurrentCode().getUserId()), "loadUserId: ");
        try {
            postData.put("mUserId", getCurrentCode().getUserId());
            postData.put("mContent", getCurrentCode().getContent());
            postData.put("mDescribe",getCurrentCode().getDescribe());
            postData.put("mType", getCurrentCode().getType());
            postData.put("mCodeImagePath", getCurrentCode().getCodeImagePath());
            //postData.put("mCodeImg",getCurrentCode().getMCodeImg());
            postData.put("mCodeImg", img1);
            postData.put("mTimeStamp", getCurrentCode().getTimeStamp());
            postData.put("mDName", getCurrentCode().getMDName());
            postData.put("mDSerial", getCurrentCode().getMDSerial());

        } catch (Exception e) {
        }
        return postData;
    }


    /* Send data to Psql DB */
    public void SendDataToPsqlDB(HashMap<String, Object> postData, Context context){


        Call<ResponseBody> call = QRCodeRESTAPIService
                .getInstance()
                .getApi()
                .createCode(postData);

        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.body() != null) {
                    try {
                        String s = response.body().string();
                        //Toast.makeText(context, s, Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    /* Send Data to Local SQL LITE DB */
    public void SendDataToSLQLIteA(){

        getCompositeDisposable().add(DatabaseUtil.on().insertCode(getCurrentCode())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                }));

    }
    /* Eliminate Duplicits in PsqlDB */

    public void EliminateDuplicitsinPsql(List<List<Code>>mItemListPSql33, List<Code> mItemListPSql, Context context){


        if(getCurrentCode()!=null && mItemListPSql33.size()>0){


            for( List<Code>codeElement: mItemListPSql33 ){
                for(Code codes : codeElement) {
                    mItemListPSql.add(codes);
                }}

            /*if (mItemListPSql.size() > 0) {

                CombaringCodeWithDB(mItemListPSql, flagEqual);

            }*/
            if (!CombaringCodeWithDB(mItemListPSql, flagEqual33)/* || mItemListPSql.size() <= 0*/) {
                /* REST Service implementation */
                /* Creating a Json file to psql sending */
                HashMap<String, Object> postData = new HashMap<>();
                FillPostArray(postData);
                SendDataToPsqlDB( postData, context);

                //}
                /* End REST SERVISE Implementation */
                /* Store Data to local DB */
                //SendDataToSLQLIteA();
            }
            // End Eliminate duplicits and data inserting
        }
        else{
            mItemListPSql33.clear();
            mItemListPSql.clear();
            // flag = false;
        }
    }


}
