package activity;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myfitnesstracker.R;

public class DashboardActivity extends AppCompatActivity {

    ActionBar actionBar;
    LinearLayout cal_counter,step_counter;
    TextView cal_consumed,step_walked;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        init();

    }

    void init(){

        actionBar = findViewById(R.id.action_bar);
        actionBar.setLeftTitle("Dashboard");

        cal_counter = findViewById(R.id.cal_tracker);
        step_counter = findViewById(R.id.step_counter);

        cal_consumed =findViewById(R.id.cal_consumed);
        step_walked = findViewById(R.id.step_walked);

    }
}
