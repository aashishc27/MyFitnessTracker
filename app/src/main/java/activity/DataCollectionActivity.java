package activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myfitnesstracker.R;
import com.facebook.appevents.suggestedevents.ViewOnClickListener;

import java.util.ArrayList;

import helper.TagLayout;

public class DataCollectionActivity extends AppCompatActivity {
    
    TagLayout tg_gender,tg_activity,tg_food;
    EditText et_weight,et_height,et_age;
    int idGender,idActivity,idFood;
    ArrayList<String> gender,activity_level,food_pref;
    LayoutInflater layoutInflater;
    String selected_gender,selected_activity,selected_food;

    ActionBar actionBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_collection);
        
        init();
    }
    
    void init(){
        layoutInflater = getLayoutInflater();
        String tag;

        actionBar = findViewById(R.id.action_bar);

        actionBar.setLeftTitle("Tell us about yourself");
        actionBar.setBackButtonDrawable();


        tg_gender = findViewById(R.id.tl_gender);
        tg_activity = findViewById(R.id.tl_activity);
        tg_food = findViewById(R.id.tl_food);

        et_age = findViewById(R.id.et_age);
        et_height = findViewById(R.id.et_height);
        et_weight = findViewById(R.id.et_weight);

        idGender = tg_gender.getId();
        idActivity = tg_activity.getId();
        idFood = tg_food.getId();



        gender = new ArrayList<>();
        activity_level = new ArrayList<>();
        food_pref = new ArrayList<>();

        activity_level.add(getResources().getString(R.string.tag_sedentory));
        activity_level.add(getResources().getString(R.string.tag_light));
        activity_level.add(getResources().getString(R.string.tag_moderate));
        activity_level.add(getResources().getString(R.string.tag_heavy));
        activity_level.add(getResources().getString(R.string.tag_heavy));
        
        gender.add(getResources().getString(R.string.tag_male));
        gender.add(getResources().getString(R.string.tag_female));
        gender.add(getResources().getString(R.string.tag_other));

        food_pref.add(getResources().getString(R.string.tag_veg));
        food_pref.add(getResources().getString(R.string.tag_nonveg));
        food_pref.add(getResources().getString(R.string.tag_egg));

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
        TagLayout.initClickListener(listener);
    }

    View.OnClickListener listener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {

            int id = ((ViewGroup) v.getParent()).getId();
            int val = tg_gender.getChildCount();
            int val2 = tg_activity.getChildCount();
            int val3 = tg_food.getChildCount();


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


        }



    };

}
