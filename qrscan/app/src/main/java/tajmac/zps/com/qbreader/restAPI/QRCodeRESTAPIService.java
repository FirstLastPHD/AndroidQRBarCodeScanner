package tajmac.zps.com.qbreader.restAPI;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class QRCodeRESTAPIService {
    //eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtb2hpdCIsImV4cCI6MTYxNDEwMzMyNiwiaWF0IjoxNjE0MDY3MzI2fQ.kFMBZkaTkGJfBOgNDz8y6CV6Sf3NLkrTWw-d0-V5S-E
    public static String KEY = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtb2hpdCIsImV4cCI6MTYxNDEwMzMyNiwiaWF0IjoxNjE0MDY3MzI2fQ.kFMBZkaTkGJfBOgNDz8y6CV6Sf3NLkrTWw-d0-V5S-E";
    private static final String BASE_URL = "http://172.20.9.49:9090/api/v1/";
    //private static final String BASE_URL = "http://172.20.9.49:9595/";
    private static QRCodeRESTAPIService mInstance;
    private Retrofit retrofit;


    private QRCodeRESTAPIService(){

        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.connectTimeout(35, TimeUnit.SECONDS);
        client.readTimeout(35, TimeUnit.SECONDS);
        client.writeTimeout(35, TimeUnit.SECONDS);


        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                     .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                     .addConverterFactory(GsonConverterFactory.create())
                    .client(client.build())
                    .build();
        }

    }

    public static synchronized  QRCodeRESTAPIService getInstance(){

        if(mInstance == null){
            mInstance = new QRCodeRESTAPIService();
        }

        return mInstance;
    }

    public QRCodeRESTAPI getApi(){

        return retrofit.create(QRCodeRESTAPI.class);
    }

}


