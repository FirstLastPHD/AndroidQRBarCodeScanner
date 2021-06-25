package tajmac.zps.com.qbreader.ui.scanresult;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;
//import com.google.android.gms.ads.AdListener;
//import com.google.android.gms.ads.AdRequest;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

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
import tajmac.zps.com.qbreader.databinding.ActivityScanResultBinding;
import tajmac.zps.com.qbreader.dbConn.PsqlConnActivity;
import tajmac.zps.com.qbreader.helpers.constant.IntentKey;
import tajmac.zps.com.qbreader.helpers.constant.PreferenceKey;
import tajmac.zps.com.qbreader.helpers.model.Code;
import tajmac.zps.com.qbreader.helpers.model.CodeDao;
import tajmac.zps.com.qbreader.helpers.util.SharedPrefUtil;
import tajmac.zps.com.qbreader.helpers.util.TimeUtil;
import tajmac.zps.com.qbreader.helpers.util.database.DatabaseUtil;
import tajmac.zps.com.qbreader.restAPI.QRCodeRESTAPIService;
import tajmac.zps.com.qbreader.ui.settings.SettingsActivity;
import tajmac.zps.com.qbreader.additions.HelperMethods;


public class ScanResultActivity extends AppCompatActivity implements View.OnClickListener {

    private CompositeDisposable mCompositeDisposable;
    private ActivityScanResultBinding mBinding;
    private Menu mToolbarMenu;
    private Code mCurrentCode;
    private boolean mIsHistory, mIsPickedFromGallery;
    private Context mContext;
    private CodeDao mCodeDao;
    static List<Code> mItemList = new ArrayList<>();
     List<Code> mItemListPSql = new ArrayList<>();
     boolean flagEqual = false;
     boolean internetConnection = false;

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

    public Menu getToolbarMenu() {
        return mToolbarMenu;
    }

    public void setToolbarMenu(Menu toolbarMenu) {
        mToolbarMenu = toolbarMenu;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //checkInternetConnection();
        if(isNetworkConnected()){
            Toast.makeText(this, " Network State:  Connected ... ", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, " Network State: Not Connected ... ", Toast.LENGTH_SHORT).show();
        }
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_scan_result);
        setCodeDao(tajmac.zps.com.qbreader.helpers.util.database.QrBarScanDatabase.on().codeDao());
        setCompositeDisposable(new CompositeDisposable());

        //playAd();
        getWindow().setBackgroundDrawable(null);
        initializeToolbar();
        loadQRCode();
        setListeners();
        //checkInternetConnection();
    }

    /*private void playAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        mBinding.adView.loadAd(adRequest);
        mBinding.adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                mBinding.adView.setVisibility(View.GONE);
            }

            @Override
            public void onAdOpened() {
            }

            @Override
            public void onAdLeftApplication() {
            }

            @Override
            public void onAdClosed() {
            }
        });
    }*/

    /*private boolean  checkInternetConnection() {

        CompositeDisposable disposable = new CompositeDisposable();
        disposable.add(ReactiveNetwork
                .observeNetworkConnectivity(this)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(connectivity -> {
                    if (connectivity.state() == NetworkInfo.State.CONNECTED) {
                        //mBinding.adView.setVisibility(View.VISIBLE);
                        internetConnection = true;
                        Toast.makeText(this, " Network State: Connected ... ", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, " Network State: Not Connected !!!... ", Toast.LENGTH_SHORT).show();
                       // mBinding.adView.setVisibility(View.GONE);
                    }

                }, throwable -> {
                    Toast.makeText(this, getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                }));
        return internetConnection;    }*/

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private void setListeners() {
        mBinding.textViewOpenInBrowser.setOnClickListener(this);
        mBinding.imageViewShare.setOnClickListener(this);
    }

    private void loadQRCode() {
        Intent intent = getIntent();


            if (intent != null) {
            Bundle bundle = intent.getExtras();

            if (bundle != null && bundle.containsKey(IntentKey.MODEL)) {
                setCurrentCode(bundle.getParcelable(IntentKey.MODEL));
            }

            if (bundle != null && bundle.containsKey(IntentKey.IS_HISTORY)) {
                mIsHistory = bundle.getBoolean(IntentKey.IS_HISTORY);
            }

            if (bundle != null && bundle.containsKey(IntentKey.IS_PICKED_FROM_GALLERY)) {
                mIsPickedFromGallery = bundle.getBoolean(IntentKey.IS_PICKED_FROM_GALLERY);
            }
        }

        if (getCurrentCode() != null) {
            mBinding.textViewContent.setText(String.format(Locale.ENGLISH,
                    getString(R.string.content), getCurrentCode().getContent()));

            mBinding.textViewDescribe.setText(String.format(Locale.ENGLISH,
                    getString(R.string.describe), getCurrentCode().getDescribe()));

            mBinding.textViewType.setText(String.format(Locale.ENGLISH, getString(R.string.code_type),
                    getResources().getStringArray(R.array.code_types)[getCurrentCode().getType()]));

            mBinding.textViewTime.setText(String.format(Locale.ENGLISH, getString(R.string.created_time),
                    TimeUtil.getFormattedDateString(getCurrentCode().getTimeStamp())));

            mBinding.textViewOpenInBrowser.setEnabled(URLUtil.isValidUrl(getCurrentCode().getContent()));

            if (!TextUtils.isEmpty(getCurrentCode().getCodeImagePath())) {
                Glide.with(this)
                        .asBitmap()
                        .load(getCurrentCode().getCodeImagePath())
                        .into(mBinding.imageViewScannedCode);
            }

            if (SharedPrefUtil.readBooleanDefaultTrue(PreferenceKey.COPY_TO_CLIPBOARD)
                    && !mIsHistory) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

                if (clipboard != null) {
                    ClipData clip = ClipData.newPlainText(
                            getString(R.string.scanned_qr_code_content),
                            getCurrentCode().getContent());
                    clipboard.setPrimaryClip(clip);

                    //Toast.makeText(this, getString(R.string.copied_to_clipboard),
                           // Toast.LENGTH_SHORT).show();
                }
            }

            if (SharedPrefUtil.readBooleanDefaultTrue(PreferenceKey.SAVE_HISTORY) && !mIsHistory) {
              /* Synchronizace DB psql a sql lite */
                /* Pridat Metodu synchronizace DB */
             /* Porovnavat Tady Array list psql and local DB  */



                /*!!!!!!! V Pripade kdyz budu chtit zapojit lokalni DB   !!!!!!!*/
              /*if(mItemListPSql.size() <=0) {
                  DatabaseUtil.on().getAllCodes()
                          .observeOn(AndroidSchedulers.mainThread())
                          .subscribe(codeList -> {
                              mItemList = codeList;
                          });
              }*/

             // Udelat z tohoto metodu zvlast
               // Synchronizace PSQL a LOCAL DB */
                // Important !!!!!!!!!
                //SynchronizeDB( this.mItemList,  this.mItemListPSql/*,synchFlag*/);

                // Eliminate duplicits here //

                /* Kdyz je pristup do PSQL */

               /// EliminateDuplicitsinPsql();


                getEntriesFromPsql();


            }}
        }


    private void initializeToolbar() {
        setSupportActionBar(mBinding.toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_toolbar_menu, menu);
        setToolbarMenu(menu);
        return true;
    }

    @Override
    public void onClick(View v) {
         String selectedImagePath;

        switch (v.getId()) {
            case R.id.text_view_open_in_browser:
                try{
                if (getCurrentCode() != null
                        && URLUtil.isValidUrl(getCurrentCode().getContent())) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                    if(getCurrentCode().getCodeImagePath()!= null) {
                        browserIntent.setData(Uri.parse(getCurrentCode().getContent()));
                        // try{
                        startActivity(browserIntent);
                    }

                    //}
                    //else{
                        //Toast.makeText(this, " Code was deleted ...", Toast.LENGTH_SHORT).show();
                    ///}

                   // }catch( Exception e){
                        // pokud bude moznost tak realizovat nacteni kodu z databazoveho systemu
                    //    Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                   // }
                }}catch(Exception e){
                    Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.image_view_share:
                if (getCurrentCode() != null) {
                    shareCode(new File(getCurrentCode().getCodeImagePath()));
                }
                break;

            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getCompositeDisposable().dispose();

        if (getCurrentCode() != null
                && !SharedPrefUtil.readBooleanDefaultTrue(PreferenceKey.SAVE_HISTORY)
                && !mIsHistory && !mIsPickedFromGallery) {
            new File(getCurrentCode().getCodeImagePath()).delete();
            //mItemListPSql33.clear();
            //mItemListPSql.clear();

        }

    }

    private void shareCode(File codeImageFile) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            shareIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(this,
                    getString(R.string.file_provider_authority), codeImageFile));
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(codeImageFile));
        }

        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_code_using)));
    }

/* Vytvorit trudu anebo class a importovat class !!!! */

 //davat to na zacatku aplikace
   // Eliminate duplicits
   void EliminateDuplicits(List<Code> mItemList){
         int count = 0;
        for(int i = 0 ; i <mItemList.size(); i ++){
            for(int j = 1 ; j <mItemList.size(); j ++){
                if((mItemList.get(i).getContent().equals(mItemList.get(j).getContent()))){
                    count ++;
                }
                if(count >1){
                    // Databa se remove element
                    mItemList.remove(mItemList.get(j));
                }
            }
            count = 0;

        }
   }

// Synchronize Databases
    void SynchronizeDB(List<Code> mItemList, List<Code> mItemListPSql){

       try{

           Toast.makeText(ScanResultActivity.this, " Probíhá synchronizace... ", Toast.LENGTH_LONG).show();

        //Log.d(String.valueOf(mItemList.size()), "SynchronizeDB1: ");
        //Log.d(String.valueOf(mItemListPSql.size()), "SynchronizeDB2: ");
        int counter = 0;
        /* V cyclech overuji sqlLite a Psql DB seznamy jejich schodu */

       if(mItemList.size()!=0 && mItemListPSql.size() != 0)
        for (int i = 0 ; i <mItemList.size(); i ++) {
            for (int j = 0; j < mItemListPSql.size(); j++) {

                if (mItemList.get(i).getContent().equals(mItemListPSql.get(j).getContent())) {
                    counter++;
                }
            }
            if (counter == 0) {
                // Vlozit item do DB systemu
                // Udealt Json Object //

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

                HashMap<String, Object> postData33 = new HashMap<>();

                postData33.put("mUserId", 1);
                postData33.put("mContent", mItemList.get(i).getContent());
                postData33.put("mDescribe", "Put Code Description Here ...");
                postData33.put("mType", mItemList.get(i).getType());
                postData33.put("mCodeImagePath", mItemList.get(i).getCodeImagePath());
                //postData33.put("mCodeImg",mItemList.get(i).getMCodeImg());
                postData33.put("mCodeImg", img1);
                postData33.put("mTimeStamp", mItemList.get(i).getTimeStamp());
                postData33.put("mDName", manufacturer);
                postData33.put("mDSerial", serial);
                //postData33.put("mDName",mItemList.get(i).getMDName());
                //postData33.put("mDSerial",mItemList.get(i).getMDSerial());


                Call<ResponseBody> call = QRCodeRESTAPIService
                        .getInstance()
                        .getApi()
                        .createCode(postData33);

                call.enqueue(new Callback<ResponseBody>() {

                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        if (response.body() != null) {
                            try {
                                String s = response.body().string();
                                //Toast.makeText(ScanResultActivity.this, s, Toast.LENGTH_LONG).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(ScanResultActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                counter = 0;
            }
        }
        }catch(Exception e){}}


    /* End Synchronize method */
    /* Dodelat tuto metodu dle LocalDB */
   public List<Code> getEntriesFromLocalDB(int flag){
       try {
           getCompositeDisposable().add(DatabaseUtil.on().getAllCodes()
                   .subscribeOn(Schedulers.io())
                   .observeOn(AndroidSchedulers.mainThread())
                   .subscribe(new BiConsumer<List<Code>, Throwable>() {
                       @Override
                       public void accept(List<Code> codes33, Throwable throwable) throws Exception {
                           if (throwable != null) {
                               // pracujeme s lokalni sqlLiteDB
                               Toast.makeText(ScanResultActivity.this, "Data loading error", Toast.LENGTH_SHORT).show();
                           } else {
                               List<List<Code>> mItemListSql44 = new ArrayList<List<Code>>();
                               //List<Code> mItemListPSql = new ArrayList<>();
                               mItemList.clear();
                               mItemListSql44.add(codes33);

                               if (mItemListSql44.size() > 0) {

                                   for (List<Code> codeElement : mItemListSql44) {
                                       for (Code codes : codeElement) {
                                           mItemList.add(codes);
                                       }
                                   }
                               }
                               //flag -- 1 Scan
                               //flag -- 2 return arrayList
                               if (flag == 1) {
                                   mItemList.clear();
                                   EliminateDuplicitsFromDB(getCurrentCode(), mItemListSql44, mItemList, 2);
                               }

                           }
                       }
                   }));
       }catch(Exception e){}
        return mItemList;
    }

    // V budoucnu to zablokuji
    /* Get Entries from Psql DB */
    public void getEntriesFromPsql(){
        try {
            getCompositeDisposable().add(QRCodeRESTAPIService.getInstance()
                    .getApi()
                    .getAllRESTFlowableCodes()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new BiConsumer<List<Code>, Throwable>() {
                        @Override
                        public void accept(List<Code> codes33, Throwable throwable) throws Exception {
                            if (throwable != null) {
                                // pracujeme s lokalni sqlLiteDB
                                Toast.makeText(ScanResultActivity.this, "Zkusíme uložit naskenované data do interního úložiště zařízení...", Toast.LENGTH_SHORT).show();
                                getEntriesFromLocalDB(1);
                            } else {
                                List<List<Code>> mItemListPSql33 = new ArrayList<List<Code>>();
                                //List<Code> mItemListPSql = new ArrayList<>();
                                mItemListPSql.clear();
                                mItemListPSql33.add(codes33);
                                EliminateDuplicitsFromDB(getCurrentCode(), mItemListPSql33, mItemListPSql, 1);

                            }
                        }
                    }));
        }catch(Exception e){}
        }

/*  Comparing code  scanned entry with db entries */

    public boolean CombaringCodeWithDB(Code code, List<Code>codesList) {
        boolean egualityFlag = false;
        int i = 0;
        try{
        if (code != null && codesList.size()>0){
            do {
                // Tohle selhava  a nevim proc
                if (codesList.get(i).getContent().equalsIgnoreCase(code.getContent())) {
                    egualityFlag = true;
                    Toast.makeText(ScanResultActivity.this, " Polzka uz exsistuje v databazovem systemu!!! ", Toast.LENGTH_SHORT).show();
                    break;
                }
                i++;

            } while (i < codesList.size());
    }}catch(Exception e){}
     return egualityFlag;
    }


/* FIll Post Data Array for psql DB Inserting */
    public HashMap<String, Object> FillPostArray( HashMap<String, Object> postData){
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
        //Log.d(String.valueOf(getCurrentCode().getUserId()), "loadUserId: ");
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
    public void SendDataToPsqlDB(HashMap<String, Object> postData){

         try {
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
                             Toast.makeText(ScanResultActivity.this, "Kód je úspěšně naskenován... ", Toast.LENGTH_LONG).show();
                             //Toast.makeText(ScanResultActivity.this, s, Toast.LENGTH_LONG).show();
                         } catch (IOException e) {
                             e.printStackTrace();
                         }
                     }

                 }

                 @Override
                 public void onFailure(Call<ResponseBody> call, Throwable t) {
                     Toast.makeText(ScanResultActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                 }
             });
         }catch(Exception e){}
    }

    /* Send Data to Local SQL LITE DB */
    public void SendDataToSLQLIteA(){

        try {

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
        }catch(Exception e){}

    }
/* Eliminate Duplicits */
    public void EliminateDuplicitsFromDB(Code code, List<List<Code>> mItemListPSql33, List<Code> mItemListPSql, int flagDB){

        try{
        if(code !=null && mItemListPSql33.size()>0) {

            for (List<Code> codeElement : mItemListPSql33) {
                for (Code codes : codeElement) {
                    mItemListPSql.add(codes);
                }
            }
        }
        //List<Code>mItemList1 = getEntriesFromLocalDB(2);
        SynchronizeDB( getEntriesFromLocalDB(2), mItemListPSql);

        if( mItemListPSql33.size()>0){

            if (!CombaringCodeWithDB(code,mItemListPSql )) {
                /* REST Service implementation */
                /* Creating a Json file to psql sending */
                //1 - PSQL
                //2- SQLLite
                if(flagDB == 1) {
                    HashMap<String, Object> postData = new HashMap<>();
                    FillPostArray(postData);
                    SendDataToPsqlDB(postData);
                }else {
                    // Tady bude jiny Insert
                    SendDataToSLQLIteA();
                }
            }
        }
        else{
            mItemListPSql33.clear();
        }}catch(Exception e){}
    }
/* Eliminate Duplicits in PsqlDB */

   // @Override
   /* public void onBackPressed() {
        //th.interrupt();
        //Loop.this.finish();
        finish();
        Thread.currentThread().interrupt();
        super.onBackPressed();
        //System.exit(0);
// add finish() if you want to kill current activity
    }*/




}


