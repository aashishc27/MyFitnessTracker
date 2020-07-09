package activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myfitnesstracker.R;

import helper.ExpandableCardView;

public class TestExpandView extends AppCompatActivity {

    ExpandableCardView meal_1,meal_2,meal_3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_expand_layout);

        meal_1 = findViewById(R.id.main_meal_1);
        meal_2 = findViewById(R.id.main_meal_2);
        meal_3 = findViewById(R.id.main_meal_3);

    }
}
