package activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myfitnesstracker.R;

import java.util.ArrayList;

import helper.CommonConstants;
import helper.DatabaseHelper;
import helper.TagLayout;
import helper.Util;
import models.Calories;

public class DataCollectionActivity extends AppCompatActivity {
    
    TagLayout tg_gender,tg_activity,tg_food, tg_goals;
    EditText et_weight,et_height,et_age;
    int idGender,idActivity,idFood,idGoals;
    ArrayList<String> gender,activity_level,food_pref,fitness_goals;
    LayoutInflater layoutInflater;
    String selected_gender,selected_activity,selected_food,user_code,selected_goal;
    Button submit;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ActionBar actionBar;

    DatabaseHelper myDb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_collection);
        
        init();

        listener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAllSavedData();
    }

    void listener(){

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String weight = et_weight.getText().toString();
                String height = et_weight.getText().toString();
                String age = et_weight.getText().toString();

                if(TextUtils.isEmpty(weight)||TextUtils.isEmpty(height)||TextUtils.isEmpty(age)||TextUtils.isEmpty(selected_gender)||TextUtils.isEmpty(selected_activity)||TextUtils.isEmpty(selected_food)||TextUtils.isEmpty(selected_goal)){
                    Util.showToast("Values are missing",DataCollectionActivity.this);
                }
                else{
                    Calories calories = Util.calculateCalories(DataCollectionActivity.this,et_weight.getText().toString(),et_height.getText().toString(),et_age.getText().toString(),selected_gender,selected_goal,selected_activity);
                    float bmi = Util.calculateBMI(et_weight.getText().toString(),et_height.getText().toString());
                    String serializedData = calories.serialize();

                    editor.putString(CommonConstants.TOTAL_CAL,serializedData);
                    editor.putString(CommonConstants.BMI,bmi+"");

                    editor.apply();

                    myDb.insertData(user_code,et_weight.getText().toString(),et_height.getText().toString(),et_age.getText().toString(),selected_gender,selected_activity,selected_food,selected_goal);
                    Intent intent = new Intent(DataCollectionActivity.this,FoodItemSelection.class);
                    intent.putExtra("food_pref",selected_food);
                    startActivity(intent);
                }
            }
        });
    }

    void getAllSavedData() {
        Cursor res = myDb.getAllData();


        while (res.moveToNext()) {
            et_weight.setText(res.getString(1));
            et_height.setText(res.getString(2));
            et_age.setText(res.getString(3));
            selected_gender = res.getString(4);
            selected_activity = res.getString(5);
            selected_food = res.getString(6);
            selected_goal = res.getString(7);

            prefillData();
//            if(TextUtils.isEmpty(res.getString(2))||TextUtils.isEmpty(res.getString(3))||TextUtils.isEmpty(res.getString(4))||TextUtils.isEmpty(selected_gender)||TextUtils.isEmpty(selected_activity)||TextUtils.isEmpty(selected_food)){
//                prefillData();
//            }
//            else{
//                Intent intent = new Intent(DataCollectionActivity.this,FoodItemSelection.class);
//                intent.putExtra("food_pref",selected_food);
//                startActivity(intent);
//            }


        }
    }


    void prefillData(){
        if (!selected_gender.equals("") && gender.indexOf(selected_gender) != -1) {

            for (int j = 0; j < gender.size(); j++) {

                TextView genderChild = tg_gender.getChildAt(j).findViewById(R.id.tagTextView);
                genderChild.setBackground(getResources().getDrawable(R.drawable.unselected_tag));
                genderChild.setTextColor(getResources().getColor(R.color.unselected_tag_color));

            }

            tg_gender.setTag(gender.indexOf(selected_gender));
            TextView genderChild = tg_gender.getChildAt((Integer) tg_gender.getTag()).findViewById(R.id.tagTextView);
            genderChild.setBackground(getResources().getDrawable(R.drawable.selected_tag));
            genderChild.setTextColor(getResources().getColor(R.color.white));

        }
        if (!selected_activity.equals("") && activity_level.indexOf(selected_activity) != -1) {

            for (int j = 0; j < activity_level.size(); j++) {

                TextView genderChild = tg_activity.getChildAt(j).findViewById(R.id.tagTextView);
                genderChild.setBackground(getResources().getDrawable(R.drawable.unselected_tag));
                genderChild.setTextColor(getResources().getColor(R.color.unselected_tag_color));

            }

            tg_activity.setTag(activity_level.indexOf(selected_activity));
            TextView genderChild = tg_activity.getChildAt((Integer) tg_activity.getTag()).findViewById(R.id.tagTextView);
            genderChild.setBackground(getResources().getDrawable(R.drawable.selected_tag));
            genderChild.setTextColor(getResources().getColor(R.color.white));

        }
        if (!selected_food.equals("") && food_pref.indexOf(selected_food) != -1) {

            for (int j = 0; j < food_pref.size(); j++) {

                TextView genderChild = tg_food.getChildAt(j).findViewById(R.id.tagTextView);
                genderChild.setBackground(getResources().getDrawable(R.drawable.unselected_tag));
                genderChild.setTextColor(getResources().getColor(R.color.unselected_tag_color));

            }

            tg_food.setTag(food_pref.indexOf(selected_food));
            TextView genderChild = tg_food.getChildAt((Integer) tg_food.getTag()).findViewById(R.id.tagTextView);
            genderChild.setBackground(getResources().getDrawable(R.drawable.selected_tag));
            genderChild.setTextColor(getResources().getColor(R.color.white));

        }
        if (!selected_goal.equals("") && fitness_goals.indexOf(selected_goal) != -1) {

            for (int j = 0; j < fitness_goals.size(); j++) {

                TextView genderChild = tg_goals.getChildAt(j).findViewById(R.id.tagTextView);
                genderChild.setBackground(getResources().getDrawable(R.drawable.unselected_tag));
                genderChild.setTextColor(getResources().getColor(R.color.unselected_tag_color));

            }

            tg_goals.setTag(fitness_goals.indexOf(selected_goal));
            TextView genderChild = tg_goals.getChildAt((Integer) tg_goals.getTag()).findViewById(R.id.tagTextView);
            genderChild.setBackground(getResources().getDrawable(R.drawable.selected_tag));
            genderChild.setTextColor(getResources().getColor(R.color.white));

        }

    }
    
    void init(){
        layoutInflater = getLayoutInflater();
        String tag;

        myDb = new DatabaseHelper(this);


        actionBar = findViewById(R.id.action_bar);

        sharedPreferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        submit = findViewById(R.id.submit);

        String name =sharedPreferences.getString(CommonConstants.USER_NAME,"");

        if(TextUtils.isEmpty(name)){
            actionBar.setLeftTitle("Tell us about yourself");
        }else{
            actionBar.setLeftTitle("Welcome "+ sharedPreferences.getString(CommonConstants.USER_NAME,""));

        }

        user_code = sharedPreferences.getString(CommonConstants.USER_CODE,"");
        tg_gender = findViewById(R.id.tl_gender);
        tg_activity = findViewById(R.id.tl_activity);
        tg_food = findViewById(R.id.tl_food);
        tg_goals = findViewById(R.id.tl_goals);

        et_age = findViewById(R.id.et_age);
        et_height = findViewById(R.id.et_height);
        et_weight = findViewById(R.id.et_weight);

        idGender = tg_gender.getId();
        idActivity = tg_activity.getId();
        idFood = tg_food.getId();
        idGoals = tg_goals.getId();

        int age =sharedPreferences.getInt(CommonConstants.USER_AGE,0);

        if(age>1){
            et_age.setText(age+"");
        }

        gender = new ArrayList<>();
        activity_level = new ArrayList<>();
        food_pref = new ArrayList<>();
        fitness_goals = new ArrayList<>();

        activity_level.add(getResources().getString(R.string.tag_sedentory));
        activity_level.add(getResources().getString(R.string.tag_light));
        activity_level.add(getResources().getString(R.string.tag_moderate));
        activity_level.add(getResources().getString(R.string.tag_heavy));

        gender.add(getResources().getString(R.string.tag_male));
        gender.add(getResources().getString(R.string.tag_female));

        food_pref.add(getResources().getString(R.string.tag_veg));
        food_pref.add(getResources().getString(R.string.tag_nonveg));
        food_pref.add(getResources().getString(R.string.tag_both));

        fitness_goals.add(getResources().getString(R.string.tag_fat_loss));
        fitness_goals.add(getResources().getString(R.string.tag_muscle_gain));





        for (int i = 0; i < gender.size(); i++) {

            View tagView = layoutInflater.inflate(R.layout.tag_item, null, false);

            TextView tagTextView = tagView.findViewById(R.id.tagTextView);

            tag = gender.get(i);
            tagTextView.setText(tag);
            tg_gender.addView(tagView);
        }
        for (int i = 0; i < activity_level.size(); i++) {

            View tagView = layoutInflater.inflate(R.layout.tag_item, null, false);

            TextView tagTextView = tagView.findViewById(R.id.tagTextView);

            tag = activity_level.get(i);
            tagTextView.setText(tag);
            tg_activity.addView(tagView);
        }
        for (int i = 0; i < food_pref.size(); i++) {

            View tagView = layoutInflater.inflate(R.layout.tag_item, null, false);

            TextView tagTextView = tagView.findViewById(R.id.tagTextView);

            tag = food_pref.get(i);
            tagTextView.setText(tag);
            tg_food.addView(tagView);
        }
        for (int i = 0; i < fitness_goals.size(); i++) {

            View tagView = layoutInflater.inflate(R.layout.tag_item, null, false);

            TextView tagTextView = tagView.findViewById(R.id.tagTextView);

            tag = fitness_goals.get(i);
            tagTextView.setText(tag);
            tg_goals.addView(tagView);
        }

        TagLayout.initClickListener(listener);
    }

    View.OnClickListener listener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {

            Util.closeKeyBoard(DataCollectionActivity.this);

            int id = ((ViewGroup) v.getParent()).getId();
            int val = tg_gender.getChildCount();
            int val2 = tg_activity.getChildCount();
            int val3 = tg_food.getChildCount();
            int val4 = tg_goals.getChildCount();

            String selected = "";
            TextView tv;



            if (id == idGender) {
                for (int j = 0; j < val; j++) {

                    if (v.getTag() != null) {
                        if (j != (Integer) v.getTag()) {
                            tv = tg_gender.getChildAt(j).findViewById(R.id.tagTextView);
                            tv.setBackground(getResources().getDrawable(R.drawable.unselected_tag));
                            tv.setTextColor(getResources().getColor(R.color.unselected_tag_color));

                        } else {

                            tv = tg_gender.getChildAt((Integer) v.getTag()).findViewById(R.id.tagTextView);
                            tv.setBackground(getResources().getDrawable(R.drawable.selected_tag));
                            selected = tv.getText().toString();
                            tv.setTextColor(getResources().getColor(R.color.white));
                            selected_gender = selected;

                        }
                        et_age.clearFocus();

                    }

                }

            }
            else if(id == idActivity){
                for (int j = 0; j < val2; j++) {

                    if (v.getTag() != null) {
                        if (j != (Integer) v.getTag()) {
                            tv = tg_activity.getChildAt(j).findViewById(R.id.tagTextView);
                            tv.setBackground(getResources().getDrawable(R.drawable.unselected_tag));
                            tv.setTextColor(getResources().getColor(R.color.unselected_tag_color));

                        } else {

                            tv = tg_activity.getChildAt((Integer) v.getTag()).findViewById(R.id.tagTextView);
                            tv.setBackground(getResources().getDrawable(R.drawable.selected_tag));
                            selected = tv.getText().toString();
                            tv.setTextColor(getResources().getColor(R.color.white));
                            selected_activity = selected;

                        }
                    }

                }
            }
            else if(id == idFood){
                for (int j = 0; j < val3; j++) {

                    if (v.getTag() != null) {
                        if (j != (Integer) v.getTag()) {
                            tv = tg_food.getChildAt(j).findViewById(R.id.tagTextView);
                            tv.setBackground(getResources().getDrawable(R.drawable.unselected_tag));
                            tv.setTextColor(getResources().getColor(R.color.unselected_tag_color));

                        } else {

                            tv = tg_food.getChildAt((Integer) v.getTag()).findViewById(R.id.tagTextView);
                            tv.setBackground(getResources().getDrawable(R.drawable.selected_tag));
                            selected = tv.getText().toString();
                            tv.setTextColor(getResources().getColor(R.color.white));
                            selected_food = selected;

                        }
                    }

                }
            }
            else if(id == idGoals){
                for (int j = 0; j < val4; j++) {

                    if (v.getTag() != null) {
                        if (j != (Integer) v.getTag()) {
                            tv = tg_goals.getChildAt(j).findViewById(R.id.tagTextView);
                            tv.setBackground(getResources().getDrawable(R.drawable.unselected_tag));
                            tv.setTextColor(getResources().getColor(R.color.unselected_tag_color));

                        } else {

                            tv = tg_goals.getChildAt((Integer) v.getTag()).findViewById(R.id.tagTextView);
                            tv.setBackground(getResources().getDrawable(R.drawable.selected_tag));
                            selected = tv.getText().toString();
                            tv.setTextColor(getResources().getColor(R.color.white));
                            selected_goal = selected;

                        }
                    }

                }
            }



        }



    };

}
