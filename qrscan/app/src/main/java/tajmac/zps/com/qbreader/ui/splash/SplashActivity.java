package tajmac.zps.com.qbreader.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tajmac.zps.com.qbreader.R;
import tajmac.zps.com.qbreader.restAPI.QRCodeRESTAPIService;
import tajmac.zps.com.qbreader.ui.home.HomeActivity;
import tajmac.zps.com.qbreader.ui.scanresult.ScanResultActivity;

public class SplashActivity extends AppCompatActivity {

    /**
     * Constants
     */
    private final int SPLASH_DELAY = 2500;

    /**
     * Fields
     */
    private ImageView mImageViewLogo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getWindow().setBackgroundDrawable(null);

        initializeViews();
        animateLogo();
        /* !!!!!!!! Secure !!!!!!!!! */
        /* Expiration time of using application */
        // three monthes
        long threeMonthes = (long) (7.88923149  * Math.pow(10,9));
        // 1 year
        long oneYear = (long) (3.1556926 * Math.pow(10,10));
        // 6 monthes
        long halfYesr = (long) (1.5778463 * Math.pow(10,10));
        //final long FIVE_MINUTES = 1000 * 60 * 40; //5 minutes in milliseconds
        final long FIVE_MINUTES = threeMonthes;

        long currentTime = new Date().getTime();
        Log.d(String.valueOf(currentTime), "CurrentTime: ");
        //long previousTime = mPreviousTime;
        long previousTime = 1621397837790l;
        //Log.d(String.valueOf(currentTime), "CurrentTime: ");
        long differ = (currentTime - previousTime);
        if (differ < FIVE_MINUTES && differ > -FIVE_MINUTES ){
        goToHomePage();
        }else{
           /* while(true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
                Toast.makeText(SplashActivity.this, " Application Time was expired !!!! It is All .....Done....", Toast.LENGTH_LONG).show();
           // }
            //System.exit(1);
        }

    }

    /**
     * This method initializes the views
     */
    private void initializeViews() {
        mImageViewLogo = findViewById(R.id.image_view_logo);
    }

    /**
     * This method takes user to the main page
     */
    private void goToHomePage() {
        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, HomeActivity.class));
            finish();
        }, SPLASH_DELAY);
    }

    /**
     * This method animates the logo
     */
    private void animateLogo() {
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in_without_duration);
        fadeInAnimation.setDuration(SPLASH_DELAY);

        mImageViewLogo.startAnimation(fadeInAnimation);
    }
}
