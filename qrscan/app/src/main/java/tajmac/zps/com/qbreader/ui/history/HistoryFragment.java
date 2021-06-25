package tajmac.zps.com.qbreader.ui.history;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tajmac.zps.com.qbreader.R;
import tajmac.zps.com.qbreader.additions.HelperMethods;
import tajmac.zps.com.qbreader.databinding.FragmentHistoryBinding;
import tajmac.zps.com.qbreader.helpers.constant.IntentKey;
import tajmac.zps.com.qbreader.helpers.itemtouch.OnStartDragListener;
import tajmac.zps.com.qbreader.helpers.itemtouch.SimpleItemTouchHelperCallback;
import tajmac.zps.com.qbreader.helpers.model.Code;
import tajmac.zps.com.qbreader.helpers.util.ProgressDialogUtil;
import tajmac.zps.com.qbreader.helpers.util.database.DatabaseUtil;
import tajmac.zps.com.qbreader.restAPI.QRCodeRESTAPIService;
import tajmac.zps.com.qbreader.ui.base.ItemClickListener;
import tajmac.zps.com.qbreader.ui.scanresult.ScanResultActivity;

public class HistoryFragment extends Fragment implements OnStartDragListener, ItemClickListener<Code> {

    private Context mContext;
    private FragmentHistoryBinding mBinding;
    private CompositeDisposable mCompositeDisposable;
    private ItemTouchHelper mItemTouchHelper;
    private HistoryAdapter mAdapter;
    static List<Code> mItemList = new ArrayList<>();
    static List<Code> mItemListManufacture = new ArrayList<>();
    private CompositeDisposable getCompositeDisposable() {
        return mCompositeDisposable;
    }

    private void setCompositeDisposable(CompositeDisposable compositeDisposable) {
        mCompositeDisposable = compositeDisposable;
    }

    public HistoryFragment() {

    }

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_history, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (mContext != null) {
            mBinding.recyclerViewHistory.setLayoutManager(new LinearLayoutManager(mContext));
            mBinding.recyclerViewHistory.setItemAnimator(new DefaultItemAnimator());
            mAdapter = new HistoryAdapter(this,mContext);
            mBinding.recyclerViewHistory.setAdapter(mAdapter);
            ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter);
            mItemTouchHelper = new ItemTouchHelper(callback);
            mItemTouchHelper.attachToRecyclerView(mBinding.recyclerViewHistory);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCompositeDisposable(new CompositeDisposable());

        if (mContext == null) {
            return;
        }
       /* Tady se dostavaji vsichni kody t.j vsichni entries */
        ProgressDialogUtil.on().showProgressDialog(mContext);

        /* Zkontrolovat DB spojeni pokud existuje tak brat z PSQL pokud ne tak l lokalni db */
        /* A kdyz budu dostavat data z DB tak v tomto pripade musim zkontrolovat model a seriove cislo tabletu anebo telefonu */
        StoreDataFromPsqlDB();
       // TAdy udelam kontrolu na existence obrazku na telefonu
        /// Tady bude metodA
       /* try {
            GiveImageFromDB(mItemListManufacture);
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        /* !!!!!! Tohle pridavat kdyz budu chtit zapojit db na telefonu    !!!!! */

        /*if ( mItemListManufacture.size()<=0) {

            mItemListManufacture.clear();
       Toast.makeText(getActivity(), " Psql connection not success, I will be Using a local storage DB...... ", Toast.LENGTH_SHORT).show();
        getCompositeDisposable().add(DatabaseUtil.on().getAllCodes()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(codeList -> {
                    if (codeList.isEmpty()) {
                        mBinding.imageViewEmptyBox.setVisibility(View.VISIBLE);
                        mBinding.textViewNoItemPlaceholder.setVisibility(View.VISIBLE);
                    } else {
                        mBinding.textViewNoItemPlaceholder.setVisibility(View.GONE);
                        mBinding.imageViewEmptyBox.setVisibility(View.INVISIBLE);
                    }
                    /* Kontrola zarizeni */
                   /* String manufacturer = android.os.Build.MANUFACTURER;
                    String serial =  android.os.Build.SERIAL;

                    for(int i = 0 ; i < codeList.size(); i++ ){
                        if(codeList.get(i).getMDName() == manufacturer &&  codeList.get(i).getMDSerial() ==  serial){
                            mItemListManufacture.add(codeList.get(i));
                        }
                    }
                    //getAdapter().clear();
                    //getAdapter().addItem(mItemListManufacture);
                   getAdapter().clear();
                    getAdapter().addItem(codeList);
                    ProgressDialogUtil.on().hideProgressDialog();
                }, e -> ProgressDialogUtil.on().hideProgressDialog()));
   }
        else{
            mItemListManufacture.clear();
        }*/
    }

    private HistoryAdapter getAdapter() {
        return (HistoryAdapter) mBinding.recyclerViewHistory.getAdapter();
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }
    /* Receiving just one ithem */
    @Override
    public void onItemClick(View view, Code item, int position) {
        Intent intent = new Intent(mContext, ScanResultActivity.class);
        intent.putExtra(IntentKey.MODEL, item);
        intent.putExtra(IntentKey.IS_HISTORY, true);
        startActivity(intent);
        //Log.d(item.getContent(), "a Current Item is: ");
    }



    /* Upload Image From DB To Mobile if it is nessesary */
    void GiveImageFromDB(List<Code>mItemListManufacture) throws IOException {
        if (  mItemListManufacture.size()>0){
            for(int i = 0 ; i < mItemListManufacture.size(); i ++){
                /* Tady bitmap img po path a if po bitmap  */
                Bitmap bitmap = null;
                try {
                    bitmap = HelperMethods.encodeAsBitmap(mItemListManufacture.get(i).getCodeImagePath());
                } catch (WriterException e) {
                    e.printStackTrace();
                }

                if(bitmap != null){
                    Log.d(" Image exsist...", "onCreateBitmaap: ");
                    Toast.makeText(getActivity(), " Image exsist... ", Toast.LENGTH_SHORT).show();

                   /* URL url = new URL("http://172.20.9.49:9090/api/v1/codes/");
                    URLConnection connection = url.openConnection();
                    try {
                        Bitmap bmp = BitmapFactory.decodeStream(connection.getInputStream());
                    }catch(Exception e){
                        Log.d(e.toString(), "ExceptionDBImage: ");
                    }*/

                    Log.d(" Good done success... ", "GiveImageFromDB: ");
                }
                else{
                    // Do rest serveru pridat metodu servise jak dostat obrazek
                    //tady je potreba tento image  pridat, t.j dostat z databazoveho systemu a nacist do telefonu
                    // nejak to stahnout z databazoveho systemu
                    //Picasso.get().load(posterUrl).fit().centerInside()
                    //.info(imageViewHolder)
                }
            }
        }
    }


    public void StoreDataFromLocalDB(){

        try{

        getCompositeDisposable().add(DatabaseUtil.on().getAllCodes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<List<Code>, Throwable>() {
                    @Override
                    public void accept(List<Code> codes44, Throwable throwable) throws Exception {
                        if (throwable != null) {

                            // pracujeme s lokalni sqlLiteDB
                            Toast.makeText(mContext, "Přístup k internímu úložišti je odepřen...", Toast.LENGTH_SHORT).show();
                        } else {
                            if (codes44.isEmpty()) {
                                mBinding.imageViewEmptyBox.setVisibility(View.VISIBLE);
                                mBinding.textViewNoItemPlaceholder.setVisibility(View.VISIBLE);
                            } else {
                                Toast.makeText(getActivity(), " Přístup ke SqlLite Internímu úložiště zařízení je uspěšni... ", Toast.LENGTH_SHORT).show();
                                mBinding.textViewNoItemPlaceholder.setVisibility(View.GONE);
                                mBinding.imageViewEmptyBox.setVisibility(View.INVISIBLE);
                            }
                            
                            mItemList.clear();
                            for(int i = 0 ; i < codes44.size(); i++ ){
                                    mItemList.add(codes44.get(i));
                            }
                            getAdapter().clear();
                            getAdapter().addItem(mItemList);
                            ProgressDialogUtil.on().hideProgressDialog();
                        }
                    }
                }));}catch(Exception e){}
    }

    /* Store Data From Psql DB */
    public void StoreDataFromPsqlDB(){

        try{

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
                            Toast.makeText(mContext, "Není přístup ke vzdálenému datovému  úložišti ... používáme lokálně datové úložiště ...", Toast.LENGTH_SHORT).show();
                            StoreDataFromLocalDB();
                        } else {
                            if (codes33.isEmpty()) {
                                mBinding.imageViewEmptyBox.setVisibility(View.VISIBLE);
                                mBinding.textViewNoItemPlaceholder.setVisibility(View.VISIBLE);
                            } else {
                                Toast.makeText(getActivity(), " Přístup ke vzdálenému datovému úložišti je uspěšni!!! ", Toast.LENGTH_SHORT).show();
                                mBinding.textViewNoItemPlaceholder.setVisibility(View.GONE);
                                mBinding.imageViewEmptyBox.setVisibility(View.INVISIBLE);
                            }

                            String manufacturer = android.os.Build.MANUFACTURER;
                            String serial =  android.os.Build.SERIAL;

                            mItemListManufacture.clear();
                            for(int i = 0 ; i < codes33.size(); i++ ){
                                if(codes33.get(i).getMDName()!=null && codes33.get(i).getMDSerial()!= null && codes33.get(i).getMDName().equals(manufacturer) && codes33.get(i).getMDSerial().equals(serial)){

                                    //codeList.remove(codeList.get(i));
                                    mItemListManufacture.add(codes33.get(i));
                                }
                            }

                            getAdapter().clear();
                            getAdapter().addItem(mItemListManufacture);
                            //mItemListManufacture.clear();
                            //getAdapter().addItem(codeList);
                            ProgressDialogUtil.on().hideProgressDialog();

                        }
                    }
                }));}
        catch(Exception e){}
    }



}
