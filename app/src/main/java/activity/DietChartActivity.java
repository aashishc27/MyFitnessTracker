package activity;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myfitnesstracker.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import adapter.MealAdapter;
import helper.CommonConstants;
import helper.ExpandableCardView;
import helper.Util;
import models.Calories;
import models.FoodApiModel;
import models.FoodList;
import models.MealModel;
import networking.NetworkHelper;
import test.FoodDataJson;
import test.Food_MyRecyclerViewAdapter;
import test.Food_RecyclerView_Main;

public class DietChartActivity extends AppCompatActivity {

    ActionBar actionBar;
    ListView lv_break, lv_lunch, lv_dinner;
    ArrayList<MealModel> break_list, lunch_list, dinner_list;
    MealAdapter ad_break, ad_lunch, ad_dinner;
    TextView total_cal, total_bmi;
    Button submit;
    LottieAnimationView animation_view;
    RelativeLayout mainView;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private SensorManager mSensorManager;
    private float mAccel; // acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravity
    Calories calories;
    static FoodDataJson foodData;
    ArrayList<FoodList> selected_protein, selected_carbs, selected_fats;
    ExpandableCardView meal_1,meal_2,meal_3;
    List<Integer> meals;
    ArrayList<String> break_food, lunch_food, dinner_food;
    String goals;

    private final SensorEventListener mSensorListener = new SensorEventListener() {

        public void onSensorChanged(SensorEvent se) {
            float x = se.values[0];
            float y = se.values[1];
            float z = se.values[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta; // perform low-cut filter


            if (mAccel > 12) {
                finish();
                startActivity(getIntent());
            }

        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mSensorListener);
        super.onPause();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet_chart);


        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;

        init();

        listener();

        //getFood();


        animation_view.playAnimation();

        openCollapse();


    }


//    If your BMI is less than 18.5, it falls within the underweight range.
//    If your BMI is 18.5 to <25, it falls within the normal.
//    If your BMI is 25.0 to <30, it falls within the overweight range.
//    If your BMI is 30.0 or higher, it falls within the obese range.

    void init() {

        sharedPreferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        actionBar = findViewById(R.id.action_bar);
        actionBar.setLeftTitle("Calculated Values");

        total_cal = findViewById(R.id.cal_calories);
        total_bmi = findViewById(R.id.cal_bmi);

        animation_view = findViewById(R.id.animationView);

        selected_protein = new ArrayList<>();
        selected_carbs = new ArrayList<>();
        selected_fats = new ArrayList<>();

        break_food = new ArrayList<>();
        lunch_food = new ArrayList<>();
        dinner_food = new ArrayList<>();

        Gson gson = new Gson();

        meals = new ArrayList<>();
        meals.add(1);
        meals.add(2);
        meals.add(3);


        meal_1 = findViewById(R.id.main_meal_1);
        meal_2 = findViewById(R.id.main_meal_2);
        meal_3 = findViewById(R.id.main_meal_3);



        TypeToken<ArrayList<FoodList>> token = new TypeToken<ArrayList<FoodList>>() {
        };
        selected_protein = gson.fromJson(sharedPreferences.getString(CommonConstants.SELCTED_PROTEIN, ""), token.getType());
        selected_carbs = gson.fromJson(sharedPreferences.getString(CommonConstants.SELCTED_CARBS, ""), token.getType());
        selected_fats = gson.fromJson(sharedPreferences.getString(CommonConstants.SELCTED_FATS, ""), token.getType());


        splitFoodItem(selected_protein);
        splitFoodItem(selected_carbs);
        splitFoodItem(selected_fats);


        getAllMeals();


        foodData = new FoodDataJson();

        calories = Util.create(sharedPreferences.getString(CommonConstants.TOTAL_CAL, ""));

        float bmi = Float.parseFloat(sharedPreferences.getString(CommonConstants.BMI, ""));

        String range = "";

        if (bmi < 18.5) {
            range = "Under weight";
        } else if (bmi > 18.5 && bmi <= 25.0) {
            range = "Normal weight";
        } else if (bmi > 25.0 && bmi <= 30.0) {
            range = "Over weight";
        } else if (bmi > 30.0) {
            range = "Obese";
        }


        total_cal.setText(String.format("%.2f", calories.getTotalcalories()) + " Cal");
        total_bmi.setText(String.format("%.2f", bmi) + " (" + range + ")");

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


        ad_break = new MealAdapter(this, break_list);
        lv_break.setAdapter(ad_break);

        ad_lunch = new MealAdapter(this, lunch_list);
        lv_lunch.setAdapter(ad_lunch);

        ad_dinner = new MealAdapter(this, dinner_list);
        lv_dinner.setAdapter(ad_dinner);

        submit = findViewById(R.id.go_dashboard);


    }


    void splitFoodItem(ArrayList<FoodList> foodList) {

        for (int i = 0; i < foodList.size(); i++) {
            if (foodList.get(i).getPref1() != null && foodList.get(i).getPref1().equalsIgnoreCase("Breakfast")) {
                break_food.add(foodList.get(i).getVal());
            }
            if (foodList.get(i).getPref1() != null && foodList.get(i).getPref1().equalsIgnoreCase("Lunch")) {
                lunch_food.add(foodList.get(i).getVal());
            }
            if (foodList.get(i).getPref2() != null && foodList.get(i).getPref2().equalsIgnoreCase("Dinner")) {
                dinner_food.add(foodList.get(i).getVal());
            }
        }

    }

    void listener() {
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editor.putBoolean(CommonConstants.PLAN_GENERATED, true);
                editor.apply();

                Intent intent = new Intent(DietChartActivity.this, DashboardActivity.class);
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
                switch (getRandomElement(meals)){

                    case 1:
                        new Handler().postDelayed(() -> meal_1.expand(), 100);
                        break;
                    case 2:
                        new Handler().postDelayed(() -> meal_2.expand(), 100);
                        break;
                    case 3:
                        new Handler().postDelayed(() -> meal_3.expand(), 100);
                        break;

                }
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

    void getFood() {
        String food;

        food = "chicken";
        //String f_url = "https://api.nutritionix.com/v1_1/search/"+food+"?results=0%3A20&cal_min=0&cal_max=50000&fields=item_name%2Cbrand_name%2Citem_id%2Cbrand_id&appId=42e8cbe9&appKey=a4e373fe0f10ab1de40cffbffb9db544";
        String f_url = "https://api.nutritionix.com/v1_1/search/" + food + "?results=0%3A5&cal_min=0&cal_max=50000&fields=item_name%2Cbrand_name%2Citem_id%2Cbrand_id%2Citem_description%2Cnf_protein%2Cnf_calories%2Cnf_total_carbohydrate%2Cnf_total_fat&appId=42e8cbe9&appKey=a4e373fe0f10ab1de40cffbffb9db544";
        MyDownloadJsonAsyncTask downloadJson = new MyDownloadJsonAsyncTask();
        Log.d("RahulMaddineni", f_url);
        downloadJson.execute(f_url);

    }


    private static class MyDownloadJsonAsyncTask extends AsyncTask<String, Void, FoodDataJson> {


        @Override
        protected FoodDataJson doInBackground(String... urls) {
            FoodDataJson threadMovieData = new FoodDataJson();
            for (String url : urls) {
                try {
                    threadMovieData.downloadFoodDataJson(url);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return threadMovieData;
        }

        @Override
        protected void onPostExecute(FoodDataJson threadMovieData) {
            foodData.foodList.clear();
            for (int i = 0; i < threadMovieData.getSize(); i++)
                foodData.foodList.clear();
            for (int k = 0; k < threadMovieData.getSize(); k++)
                foodData.foodList.add(threadMovieData.foodList.get(k));
        }
    }

    void getAllMeals() {


        String url = "https://api.nal.usda.gov/fdc/v1/foods/list?minItems=1&maxItems=3&dataType=SR Legacy&pageSize=1&pageNumber=1&sortOrder=asc&api_key=QF9qUeCLGCcn1NkCTEgNGXaKxQ1QA6QdageJqYKi";

        Type responseType = new TypeToken<ArrayList<FoodApiModel>>() {
        }.getType();
        JSONObject request = new JSONObject();
        try {
            request.put("pageNumber",1);
            request.put("generalSearchInput" , "egg mushroom butter apple");
            request.put(	"ingredients", "+egg  +butter ");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        NetworkHelper.callPostJSONRequest(
                url, responseType, request, new FoodApiListener(), DietChartActivity.this, false);

    }

    class FoodApiListener implements  Response.Listener<ArrayList<FoodApiModel>>, Response.ErrorListener{

        @Override
        public void onErrorResponse(VolleyError error) {

        }

        @Override
        public void onResponse(ArrayList<FoodApiModel> response) {
            for(int i = 0;i<response.size();i++){
                MealModel mealModel = new MealModel();
                for(int j=0;j<response.get(i).getFoodNutrients().size();j++){

                    mealModel.setName(response.get(i).getDescription());
                    if(response.get(i).getFoodNutrients().get(j).getNumber().equalsIgnoreCase("203")){
                        mealModel.setProtein(response.get(i).getFoodNutrients().get(j).getAmount());
                    }
                    if(response.get(i).getFoodNutrients().get(j).getNumber().equalsIgnoreCase("205")){
                        mealModel.setCarbs(response.get(i).getFoodNutrients().get(j).getAmount());
                    }
                    if(response.get(i).getFoodNutrients().get(j).getNumber().equalsIgnoreCase("204")){
                        mealModel.setFats(response.get(i).getFoodNutrients().get(j).getAmount());
                    }
                    if(response.get(i).getFoodNutrients().get(j).getNumber().equalsIgnoreCase("208")){
                        mealModel.setTotal_cal(response.get(i).getFoodNutrients().get(j).getAmount());
                    }
                }
                break_list.add(mealModel);
            }


            Collections.sort(break_list, new Comparator<MealModel>() {
                @Override
                public int compare(MealModel o1, MealModel o2) {
                    return Float.compare(o2.getProtein(), o1.getProtein());
                }
            });

        }
    }

    void openCollapse(){
        try {

            meal_1.setOnExpandedListener((v, isExpanded) -> {

                if (isExpanded) {
                    new Handler().postDelayed(() -> {
                        if(meal_2.isExpanded()){
                            meal_2.collapse();
                        }
                        if(meal_3.isExpanded()){
                            meal_3.collapse();
                        }
                    }, 100);
                }
            });


            meal_2.setOnExpandedListener((v, isExpanded) -> {

                if (isExpanded) {
                    new Handler().postDelayed(() -> {
                        if(meal_1.isExpanded()){
                            meal_1.collapse();
                        }
                        if(meal_3.isExpanded()){
                            meal_3.collapse();
                        }
                    }, 100);
                }
            });

            meal_3.setOnExpandedListener((v, isExpanded) -> {

                if (isExpanded) {
                    new Handler().postDelayed(() -> {
                        if(meal_2.isExpanded()){
                            meal_2.collapse();
                        }
                        if(meal_1.isExpanded()){
                            meal_1.collapse();
                        }
                    }, 100);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getRandomElement(List<Integer> list)
    {
        Random rand = new Random();
        return list.get(rand.nextInt(list.size()));
    }

}
