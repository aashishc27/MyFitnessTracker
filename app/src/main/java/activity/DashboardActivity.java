package activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myfitnesstracker.R;

import test.SetGoalActivity;

public class DashboardActivity extends AppCompatActivity {

    ActionBar actionBar;
    LinearLayout cal_counter,step_counter;
    TextView cal_consumed,step_walked;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        init();

        listener();

    }

    void init(){

        actionBar = findViewById(R.id.action_bar);
        actionBar.setLeftTitle("Dashboard");

        cal_counter = findViewById(R.id.cal_tracker);
        step_counter = findViewById(R.id.step_counter);

        cal_consumed =findViewById(R.id.cal_consumed);
        step_walked = findViewById(R.id.step_walked);

    }

    void listener(){
        step_counter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, SetGoalActivity.class);
                startActivity(intent);
            }
        });
    }
}
