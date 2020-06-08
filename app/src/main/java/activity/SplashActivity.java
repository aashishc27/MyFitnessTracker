package activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.myfitnesstracker.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Comment;

import java.util.ArrayList;

import helper.CommonConstants;
import helper.Util;
import models.FoodItemModel;

public class SplashActivity extends AppCompatActivity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 5000;
    boolean isDeepLinkedCalled = false;
    boolean fromSplash = true;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    PackageManager manager;
    PackageInfo info;
    private DatabaseReference mPostReference;


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {


               /* SharedPreferences getPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());*/
                //  Create a new boolean and preference and set it to true

                sharedPreferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();

                editor.putBoolean(CommonConstants.IN_APP_REJECTED, false);

                // boolean isFirstStart = getPrefs.getBoolean("firstStart", true);

                boolean isFirstStart = sharedPreferences.getBoolean(CommonConstants.firstStart, true);


                // This method will be executed once the timer is over
                // Start your app main activityx
                if (isFirstStart) {

                    //  Make a new preferences editor
                    // SharedPreferences.Editor e = getPrefs.edit();

                    editor.putBoolean(CommonConstants.firstStart, false);

                    //  Edit preference to make it false because we don't want this to run again
                    // e.putBoolean("firstStart", false);

                    //  Apply changes
                    editor.apply();

                    //SplashActivity.this
                    Intent intent = new Intent(SplashActivity.this, DataCollectionActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();


                }  else {
                    // boolean isLoggedIn = getPrefs.getBoolean(CommonConstants.IS_LOGIN, false);

                    boolean isLoggedIn = sharedPreferences.getBoolean(CommonConstants.IS_LOGIN, false);

                    if (isLoggedIn) {

                        if(sharedPreferences.getBoolean(CommonConstants.PLAN_GENERATED,false)){
                            //open dashboard
                            Intent intent = new Intent(SplashActivity.this, DashboardActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }else{

                            //complete form
                            Intent intent = new Intent(SplashActivity.this, DataCollectionActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();

                        }

                    } else {
                        Intent intent = new Intent(SplashActivity.this, DataCollectionActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }

                }

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        manager = this.getPackageManager();
        try {
            info = manager.getPackageInfo(this.getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }



    }


}
