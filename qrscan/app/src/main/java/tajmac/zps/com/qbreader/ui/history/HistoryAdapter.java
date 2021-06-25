package tajmac.zps.com.qbreader.ui.history;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tajmac.zps.com.qbreader.R;
import tajmac.zps.com.qbreader.databinding.ItemHistoryBinding;
import tajmac.zps.com.qbreader.helpers.constant.AppConstants;
import tajmac.zps.com.qbreader.helpers.itemtouch.ItemTouchHelperAdapter;
import tajmac.zps.com.qbreader.helpers.model.Code;
import tajmac.zps.com.qbreader.helpers.util.ProgressDialogUtil;
import tajmac.zps.com.qbreader.helpers.util.TimeUtil;
import tajmac.zps.com.qbreader.helpers.util.database.DatabaseUtil;
import tajmac.zps.com.qbreader.restAPI.QRCodeRESTAPIService;
import tajmac.zps.com.qbreader.ui.base.ItemClickListener;
import tajmac.zps.com.qbreader.ui.home.HomeActivity;

import static androidx.core.content.ContextCompat.startActivity;


public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> implements ItemTouchHelperAdapter {
    /**
     * Fields
     */
    public Context mContext;
    public List<Code> mItemList;
    private ItemClickListener<Code> mItemClickListener;
    static List<Code> mItemListPSql = new ArrayList<>();

    public HistoryAdapter(ItemClickListener<Code> itemClickListener,Context mContext) {
        mItemList = new ArrayList<>();
        mItemClickListener = itemClickListener;
        this.mContext = mContext;
    }

    private boolean isEqual(Code left, Code right) {
        return /*left.equals(right)*/false;
    }

    public void clear() {
        mItemList.clear();
        notifyDataSetChanged();
    }

    public void setItemList(List<Code> itemList) {
        mItemList = itemList;

    }

    public List<Code> getItems() {
        return mItemList;
    }

    public void removeItem(Code item) {
        int index = getItemPosition(item);
        if (index < 0 || index >= mItemList.size()) return;
        mItemList.remove(index);
        notifyItemRemoved(index);
    }

    public Code getItem(int position) {
        //if (position < 0 || position >= mItemList.size()-1) return null;
        if (position < 0 || position >= mItemList.size()) return null;
        return mItemList.get(position);
    }

    public int getItemPosition(Code item) {
        return mItemList.indexOf(item);
    }

    public int addItem(Code item) {

        Code oldItem = findItem(item);
        if (oldItem == null ) {
            mItemList.add(item);
            notifyItemInserted(mItemList.size() - 1);

            return mItemList.size() - 1;
        }
        return updateItem(item, item);
        // return 0;
    }

    public void addItem(List<Code> items) {

        for (Code item : items) {
            addItem(item);
        }

    }

    public void addItemToPosition(Code item, int position) {
        mItemList.add(position, item);
        notifyItemInserted(position);
    }

    public void addItemToPosition(List<Code> item, int position) {
        mItemList.addAll(position, item);
        notifyItemRangeChanged(position, item.size());
    }

    public Code findItem(Code item) {
        for (Code currentItem : mItemList) {
            if (isEqual(item, currentItem)) {
                return currentItem;
            }
        }
        return null;
    }

    public int updateItem(Code oldItem, Code newItem) {
        int oldItemIndex = getItemPosition(oldItem);
        mItemList.set(oldItemIndex, newItem);
        notifyItemChanged(oldItemIndex);
        return oldItemIndex;
    }

    public int updateItem(Code newItem, int position) {
        mItemList.set(position, newItem);
        notifyItemChanged(position);
        return position;
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HistoryViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_history, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        Code item = getItem(position);

        if (item != null)
            holder.bind(item);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        return false;
    }

    @Override
    public void onItemDismiss(int position) {

        // tady controla !!!

        GetDataFromPsql();

        if( mItemListPSql.size() <=0) {

            try {
                AsyncTask.execute(() -> {
                    try {
                        DatabaseUtil.on().deleteEntity(getItem(position));
                        mItemList.remove(position);
                    }catch(Exception e){}
                });
            }catch(Exception e){}

        }
        else{

           // DeleteEntryFromPsql(position);
             Toast.makeText(mContext, "It is not possible to remove item from Psql server !!! " , Toast.LENGTH_SHORT).show();
       if( mItemListPSql.size() >0) {

           notifyDataSetChanged();
       }

        }

        mItemListPSql.clear();
    }

/* Receive entries from psql */
public void GetDataFromPsql(){


    QRCodeRESTAPIService.getInstance()
            .getApi()
            .getAllRESTFlowableCodes()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(codeList33 -> {
                mItemListPSql = codeList33;
                   /* try {
                        for (int i = 0; i < mItemListPSql.size(); i++) {
                            Log.d(String.valueOf(codeList33.get(i).getId()), "onItehhh: ");
                        }
                    }catch(Exception e){}*/

                ProgressDialogUtil.on().hideProgressDialog();
            }, e -> ProgressDialogUtil.on().hideProgressDialog());
}

/* Delete Entry From Psql */
    public void DeleteEntryFromPsql(int position){


        //AsyncTask.execute(() -> {
        try {
            // dostat tady id
            int id = mItemListPSql.get(position).getId();
            // long idS =mItemListPSql.get(position).getId() ;
            Log.d(String.valueOf(position), "indexPosition: ");

            Call<ResponseBody> call = QRCodeRESTAPIService
                    .getInstance()
                    .getApi()
                    .deleteCode(id);

            call.enqueue(new Callback<ResponseBody>() {

                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    if (response.body() != null) {
                        try {
                            String s = response.body().string();
                            //Toast.makeText(this, s, Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.d(" Error...", "onItemDismiss: ");
                    // Toast.makeText(ScanResultActivity.this, t.getMessage(),Toast.LENGTH_LONG).show();
                }
            });
            Log.d(" Error...", "onItemDismiss: ");
        }catch(Exception e){
            Toast.makeText(mContext, "It is not possible to remove item from Psql server !!! " , Toast.LENGTH_SHORT).show();
            notifyDataSetChanged();
        }

        //});*/

        //Toast toast = new Toast(mContext);
        // V jinem Pripade
       /* Toast.makeText(mContext, "It is not possible to remove item from Psql server !!! " , Toast.LENGTH_SHORT).show();
       if( mItemListPSql.size() >0) {

           notifyDataSetChanged();
       }*/

        //Intent intent = new Intent(mContext, HomeActivity.class);
        //Intent intent=new Intent(String.valueOf(R.layout.activity_splash));
        //androidx.databinding.DataBindingUtil.setContentView(R.layout.activity_home);
        //startActivity(mContext, intent,null);
        //startActivity(intent);

    }

    class HistoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ItemHistoryBinding mBinding;

        HistoryViewHolder(@NonNull ItemHistoryBinding itemBinding) {
            super(itemBinding.getRoot());
            mBinding = itemBinding;
        }

        void bind(Code item) {
            Context context = mBinding.getRoot().getContext();

            if (context != null) {
                Glide.with(context)
                        .asBitmap()
                        .apply(new RequestOptions()
                                .skipMemoryCache(false)
                                .diskCacheStrategy(DiskCacheStrategy.ALL))
                        .load(item.getCodeImagePath())
                        .into(mBinding.imageViewCode);

                String scanType = String.format(Locale.ENGLISH,
                        context.getString(R.string.code_scan),
                        context.getResources().getStringArray(R.array.code_types)[item.getType()]);

                mBinding.textViewCodeType.setText(scanType);

                mBinding.textViewTime.setText(
                        TimeUtil.getFormattedDateString(item.getTimeStamp(),
                                AppConstants.APP_HISTORY_DATE_FORMAT));
            }

            mBinding.constraintLayoutContainer.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getItem(getAdapterPosition()), getAdapterPosition());
            }
        }


    }
}







