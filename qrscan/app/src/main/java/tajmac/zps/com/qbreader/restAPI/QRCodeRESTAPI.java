package tajmac.zps.com.qbreader.restAPI;


import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Path;
import tajmac.zps.com.qbreader.helpers.model.Code;

public interface QRCodeRESTAPI {

    @POST("codes")
    //@Headers({ "Content-Type: application/json;charset=UTF-8"})
    @Headers({ "Content-Type: application/json"})
    //@Headers({"Content-Type: application/json", "Authorization: " + "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtb2hpdCIsImV4cCI6MTYxNTk4MjQxOSwiaWF0IjoxNjE1MjAxNTY3fQ.xi_UZX8EAYkRY1Bq2RsGDsB1Tp8FUL3qVryyZEw1enI"})
    Call<ResponseBody> createCode(@Body HashMap<String, Object> json);

    @GET("codes")
    //@Headers({"Content-Type: application/json;charset=UTF-8", "Authorization: " + "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtb2hpdCIsImV4cCI6MTYxNTk4MjQxOSwiaWF0IjoxNjE1MjAxNTY3fQ.xi_UZX8EAYkRY1Bq2RsGDsB1Tp8FUL3qVryyZEw1enI"})
    Single<List<Code>> getAllRESTFlowableCodes();

    @DELETE("codes/{id}")
    Call<ResponseBody> deleteCode(@Path("id")int id);

    //@Headers({"Content-Type: application/json;charset=UTF-8", "Authorization: " + "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtb2hpdCIsImV4cCI6MTYxNTk4MjQxOSwiaWF0IjoxNjE1MjAxNTY3fQ.xi_UZX8EAYkRY1Bq2RsGDsB1Tp8FUL3qVryyZEw1enI"})
    @GET("codes")
     Call<List<Code>> selectAllCodes();
}
