package test;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.myfitnesstracker.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


import activity.LoginActivity;

public class MainActivity extends AppCompatActivity implements
        SensorEventListener {

    public static float evsteps;
    public static int cont = 0;

    public static float mSeriesMax = 0f;
    boolean activityRunning;

    private int mBackIndex;
    private int mSeries1Index;
    private int mSeries2Index;
    private int mSeries3Index;
    // Sensor data
    private TextView textView;
    private SensorManager msensorManager;
    private SensorManager sensorManager;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        final String cap1;
        final float[] m = new float[1];
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // Go to data image button
        final ImageView dn = (ImageView) findViewById(R.id.datanext);
        // Go to Chart Data page
//        dn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent1 = new Intent(MainActivity.this, Activity_ViewPager.class);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    ActivityOptionsCompat options = ActivityOptionsCompat.
//                            makeSceneTransitionAnimation(MainActivity.this, v, "testAnimation");
//                    MainActivity.this.startActivity(intent1, options.toBundle());
//                } else {
//                    startActivity(intent1);
//                }
//            }
//        });


       /* if(mSeriesMax == 0)
        {
            Log.d("COming","in");
            final Firebase[] cref = {ref.child("stepgoal")};
            cref[0].addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    System.out.println(dataSnapshot.getValue());
                    Log.d("COming", "in in");
                    mSeriesMax = Float.parseFloat(String.valueOf(dataSnapshot.getValue()));
                    Log.d("mSeries", (String.valueOf(mSeriesMax)));
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        } */
        Log.d("mSeries out", (String.valueOf(mSeriesMax)));
        if (mSeriesMax > 0) {
            Log.d("mSeries out in", (String.valueOf(mSeriesMax)));
            // Create required data series on the DecoView
            createBackSeries();
            createDataSeries1();

            // Setup events to be fired on a schedule
            //createEvents();
        }
    }

    // Step Counter

    @Override
    protected void onResume() {
        super.onResume();
        activityRunning = true;
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (countSensor != null) {
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(this, "Count sensor not available!", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        activityRunning = false;
//         if you unregister the last listener, the hardware will stop detecting step events
//        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (activityRunning) {
            textView = (TextView) findViewById(R.id.textRemaining);
            textView.setText(String.valueOf(event.values[0]));
            evsteps = event.values[0];
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private void createBackSeries() {
        SeriesItem seriesItem = new SeriesItem.Builder(Color.parseColor("#FFE2E2E2"))
                .setRange(0, mSeriesMax, 0)
                .setInitialVisibility(true)
                .build();
    }

    private void createDataSeries1() {
        final SeriesItem seriesItem = new SeriesItem.Builder(Color.parseColor("#FFFF8800"))
                .setRange(0, mSeriesMax, 0)
                .setInitialVisibility(false)
                .build();

        Log.d("mSeries Data1", (String.valueOf(mSeriesMax)));

        final TextView textPercentage = (TextView) findViewById(R.id.textPercentage);
        seriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                float percentFilled = ((currentPosition - seriesItem.getMinValue()) / (seriesItem.getMaxValue() - seriesItem.getMinValue()));
                textPercentage.setText(String.format("%.0f%%", percentFilled * 100f));
            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {

            }
        });


        final TextView textToGo = (TextView) findViewById(R.id.textRemaining);
        seriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                textToGo.setText(String.format("%d Steps to goal", (int) (seriesItem.getMaxValue() - currentPosition)));

            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {

            }
        });

        final TextView textActivity1 = (TextView) findViewById(R.id.textActivity1);
        seriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                textActivity1.setText(String.format("%.0f Steps", currentPosition));
            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {

            }
        });

       // mSeries1Index = mDecoView.addSeries(seriesItem);
    }

//    private void createEvents() {
//        cont++;
//        mDecoView.executeReset();
//
//        if (cont == 1) {
//            resetText();
//            mDecoView.addEvent(new DecoEvent.Builder(DecoDrawEffect.EffectType.EFFECT_SPIRAL_EXPLODE)
//                    .setIndex(mSeries1Index)
//                    .setDelay(0)
//                    .setDuration(1000)
//                    .setDisplayText("")
//                    .setListener(new DecoEvent.ExecuteEventListener() {
//                        @Override
//                        public void onEventStart(DecoEvent decoEvent) {
//
//                        }
//
//                        @Override
//                        public void onEventEnd(DecoEvent decoEvent) {
//                            createEvents();
//                        }
//                    })
//                    .build());
//        }
//        mDecoView.addEvent(new DecoEvent.Builder(mSeriesMax)
//                .setIndex(mBackIndex)
//                .setDuration(3000)
//                .setDelay(100)
//                .build());
//
//        mDecoView.addEvent(new DecoEvent.Builder(evsteps)
//                .setIndex(mSeries1Index)
//                .setDelay(3250)
//                .build());
//
//        mDecoView.addEvent(new DecoEvent.Builder(DecoDrawEffect.EffectType.EFFECT_SPIRAL_EXPLODE)
//                .setIndex(mSeries1Index)
//                .setDelay(20000)
//                .setDuration(3000)
//                .setDisplayText("")
//                .setListener(new DecoEvent.ExecuteEventListener() {
//                    @Override
//                    public void onEventStart(DecoEvent decoEvent) {
//
//                    }
//
//                    @Override
//                    public void onEventEnd(DecoEvent decoEvent) {
//                        createEvents();
//                    }
//                })
//                .build());
//
//    }

    private void resetText() {
        ((TextView) findViewById(R.id.textPercentage)).setText("");
        ((TextView) findViewById(R.id.textRemaining)).setText("");
    }




}
