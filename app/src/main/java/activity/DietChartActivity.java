package activity;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.myfitnesstracker.R;

import java.util.ArrayList;

import adapter.MealAdapter;
import helper.CommonConstants;
import models.MealModel;

public class DietChartActivity extends AppCompatActivity {

    ActionBar actionBar;
    ListView lv_break,lv_lunch,lv_dinner;
    ArrayList<MealModel> break_list,lunch_list,dinner_list;
    MealAdapter ad_break,ad_lunch,ad_dinner;
    TextView total_cal,total_bmi;
    Button submit;
    LottieAnimationView animation_view;
    RelativeLayout mainView;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet_chart);

        init();

        listener();

        animation_view.playAnimation();
    }

    void init(){

        sharedPreferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        actionBar = findViewById(R.id.action_bar);
        actionBar.setLeftTitle("Calculated Values");

        total_cal = findViewById(R.id.cal_calories);
        total_bmi = findViewById(R.id.cal_bmi);

        animation_view = findViewById(R.id.animationView);

        total_cal.setText("2500 Cal");
        total_bmi.setText("27.2(Normal Weight)");

        lv_break = findViewById(R.id.lv_break);
        lv_lunch = findViewById(R.id.lv_lunch);
        lv_dinner = findViewById(R.id.lv_dinner);

        break_list = new ArrayList<>();
        lunch_list = new ArrayList<>();
        dinner_list = new ArrayList<>();

        mainView = findViewById(R.id.mainView);



        MealModel mealModel1 = new MealModel();
        mealModel1.setName("Cooked Oats");
        mealModel1.setCalories("200 Cal");
        mealModel1.setPotion_size("80 gm");
        MealModel mealModel2 = new MealModel();
        mealModel2.setName("Milk");
        mealModel2.setCalories("40 Cal");
        mealModel2.setPotion_size("50 ml");

        MealModel mealModel3 = new MealModel();
        mealModel3.setName("Peri Peri chicken");
        mealModel3.setCalories("250 Cal");
        mealModel3.setPotion_size("250gm");

        MealModel mealModel4 = new MealModel();
        mealModel4.setName("Salmon Fish Curry");
        mealModel4.setCalories("350 Cal");
        mealModel4.setPotion_size("150gm");

        MealModel mealModel5 = new MealModel();
        mealModel5.setName("Rice");
        mealModel5.setCalories("150 Cal");
        mealModel5.setPotion_size("200gm");

        break_list.add(mealModel1);
        lunch_list.add(mealModel3);
        dinner_list.add(mealModel4);

        break_list.add(mealModel2);
        lunch_list.add(mealModel5);
        dinner_list.add(mealModel5);



        ad_break = new MealAdapter(this,break_list);
        lv_break.setAdapter(ad_break);

        ad_lunch = new MealAdapter(this,lunch_list);
        lv_lunch.setAdapter(ad_lunch);

        ad_dinner = new MealAdapter(this,dinner_list);
        lv_dinner.setAdapter(ad_dinner);

        submit = findViewById(R.id.go_dashboard);


    }

    void listener(){
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editor.putBoolean(CommonConstants.PLAN_GENERATED,true);
                editor.apply();

                Intent intent = new Intent(DietChartActivity.this,DashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });




        animation_view.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mainView.setVisibility(View.VISIBLE);
                animation_view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animation_view.setRepeatCount(1);
        animation_view.setProgress(0.5f);

    }
}
