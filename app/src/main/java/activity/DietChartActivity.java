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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

import com.example.myfitnesstracker.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import adapter.MealAdapter;
import helper.CommonConstants;
import helper.ExpandableCardView;
import helper.Util;
import models.Calories;
import models.FoodApiModel;
import models.FoodList;
import models.MealModel;
import models.Results;
import networking.NetworkHelper;
import test.FoodDataJson;

public class DietChartActivity extends AppCompatActivity {

    ActionBar actionBar;
    ArrayList<MealModel> break_list, lunch_list, dinner_list;
    MealAdapter ad_break, ad_lunch, ad_dinner;
    TextView total_cal, total_bmi;
    Button submit;
    LottieAnimationView animation_view;
    ImageView edit_details;
    RelativeLayout mainView;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
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

    TextView food_name_protein_break,food_name_protein_lunch,food_name_protein_dinner;
    TextView food_name_carb_break,food_name_carb_lunch,food_name_carb_dinner;
    TextView food_name_fat_break,food_name_fat_lunch,food_name_fat_dinner;

    TextView calories_protein_break,calories_protein_lunch,calories_protein_dinner;
    TextView calories_carb_break,calories_carb_lunch,calories_carb_dinner;
    TextView calories_fat_break,calories_fat_lunch,calories_fat_dinner;

    ArrayList<Results> moderateCarbMeal,highCarbMeal,lowCarbMeal;

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

                startActivity(getIntent());
                finish();
            }

        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet_chart);
        init();

        listener();

        Intent i = getIntent();
        if(i.getBooleanExtra("fromSplash",false)){
            animation_view.playAnimation();
        }

        openCollapse();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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

        edit_details = findViewById(R.id.edit_details);

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

        Collections.sort(selected_protein, new Comparator< FoodList >() {
            @Override public int compare(FoodList p1, FoodList p2) {
                return p1.getCalories()- p2.getCalories(); // Ascending
            }
        });
        Collections.sort(selected_carbs, new Comparator< FoodList >() {
            @Override public int compare(FoodList p1, FoodList p2) {
                return p1.getCalories()- p2.getCalories(); // Ascending
            }
        });
        Collections.sort(selected_fats, new Comparator< FoodList >() {
            @Override public int compare(FoodList p1, FoodList p2) {
                return p1.getCalories()- p2.getCalories(); // Ascending
            }
        });


        //for meal 1 (Moderate Carb)
        moderateCarbMeal =  getMeal(selected_protein.size()/2,selected_carbs.size()/2,selected_fats.size()/2);

        //for meal 2 (High Carb)
        highCarbMeal = getMeal(selected_protein.size()/2,selected_carbs.size()-2,1);

        //for meal 3 (Low Carb)
        lowCarbMeal = getMeal(selected_protein.size()-2,1,selected_fats.size()-2);


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
        float total_calories =  Math.round(calories.getTotalcalories());



        break_list = new ArrayList<>();
        lunch_list = new ArrayList<>();
        dinner_list = new ArrayList<>();

        mainView = findViewById(R.id.mainView);

        // calculate calories for meal 1
        // Protein 30 % //Carbs 35 % //Fats 35 %

//        double protein_meal1 = (0.30*total_calories);
//        double carbs_meal1 = (0.35*total_calories);
//        double fats_meal1= (0.35*total_calories);
//
//
//
//        for(int i = 0;i<=selected_protein.size();i++){
//            MealModel mealModel1 = new MealModel();
//            if(selected_protein.get(i).getCalories()>100&&selected_protein.get(i).getCalories()<200){
//                mealModel1.setName(selected_protein.get(i).getVal());
//                mealModel1.setCalories(String.valueOf(selected_protein.get(i).getCalories()));
//            }
//        }
//        for(int i = 0;i<=selected_carbs.size();i++){
//            MealModel mealModel1 = new MealModel();
//            if(selected_carbs.get(i).getCalories()>250&&selected_carbs.get(i).getCalories()<350){
//                mealModel1.setName(selected_carbs.get(i).getVal());
//                mealModel1.setCalories(String.valueOf(selected_carbs.get(i).getCalories()));
//            }
//        }
//        for(int i = 0;i<=selected_fats.size();i++){
//            MealModel mealModel1 = new MealModel();
//            if(selected_fats.get(i).getCalories()>100&&selected_fats.get(i).getCalories()<250){
//                mealModel1.setName(selected_fats.get(i).getVal());
//                mealModel1.setCalories(String.valueOf(selected_fats.get(i).getCalories()));
//            }
//        }



//        MealModel mealModel1 = new MealModel();
//        mealModel1.setName("Cooked Oats");
//        mealModel1.setCalories("200 Cal");
//        mealModel1.setPotion_size("80 gm");
//
//        MealModel mealModel2 = new MealModel();
//        mealModel2.setName("Milk");
//        mealModel2.setCalories("40 Cal");
//        mealModel2.setPotion_size("50 ml");
//
//        MealModel mealModel3 = new MealModel();
//        mealModel3.setName("Peri Peri chicken");
//        mealModel3.setCalories("250 Cal");
//        mealModel3.setPotion_size("250gm");
//
//        MealModel mealModel4 = new MealModel();
//        mealModel4.setName("Salmon Fish Curry");
//        mealModel4.setCalories("350 Cal");
//        mealModel4.setPotion_size("150gm");
//
//        MealModel mealModel5 = new MealModel();
//        mealModel5.setName("Rice");
//        mealModel5.setCalories("150 Cal");
//        mealModel5.setPotion_size("200gm");
//
//        break_list.add(mealModel1);
//        lunch_list.add(mealModel3);
//        dinner_list.add(mealModel4);
//
//        break_list.add(mealModel2);
//        lunch_list.add(mealModel5);
//        dinner_list.add(mealModel5);


//        ad_break = new MealAdapter(this, meal_1);
//
//        ad_lunch = new MealAdapter(this, meal_2);
//
//        ad_dinner = new MealAdapter(this, meal_3);

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

        edit_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putBoolean("editClicked",true);
                editor.apply();
                Intent intent = new Intent(DietChartActivity.this,DataCollectionActivity.class);
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
                        createMeal(1,moderateCarbMeal);
                        break;
                    case 2:
                        new Handler().postDelayed(() -> meal_2.expand(), 100);
                        createMeal(2,highCarbMeal);
                        break;
                    case 3:
                        new Handler().postDelayed(() -> meal_3.expand(), 100);
                        createMeal(3,lowCarbMeal);
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

//    void getAllMeals() {
//
//
//        String url = "https://api.nal.usda.gov/fdc/v1/foods/list?minItems=1&maxItems=3&dataType=SR Legacy&pageSize=1&pageNumber=1&sortOrder=asc&api_key=QF9qUeCLGCcn1NkCTEgNGXaKxQ1QA6QdageJqYKi";
//
//        Type responseType = new TypeToken<ArrayList<FoodApiModel>>() {
//        }.getType();
//        JSONObject request = new JSONObject();
//        try {
//            request.put("pageNumber",1);
//            request.put("generalSearchInput" , "egg mushroom butter apple");
//            request.put(	"ingredients", "+egg  +butter ");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        NetworkHelper.callPostJSONRequest(
//                url, responseType, request, new FoodApiListener(), DietChartActivity.this, false);
//
//    }

//    class FoodApiListener implements  Response.Listener<ArrayList<FoodApiModel>>, Response.ErrorListener{
//
//        @Override
//        public void onErrorResponse(VolleyError error) {
//
//        }
//
//        @Override
//        public void onResponse(ArrayList<FoodApiModel> response) {
//            for(int i = 0;i<response.size();i++){
//                MealModel mealModel = new MealModel();
//                for(int j=0;j<response.get(i).getFoodNutrients().size();j++){
//
//                    mealModel.setName(response.get(i).getDescription());
//                    if(response.get(i).getFoodNutrients().get(j).getNumber().equalsIgnoreCase("203")){
//                        mealModel.setProtein(response.get(i).getFoodNutrients().get(j).getAmount());
//                    }
//                    if(response.get(i).getFoodNutrients().get(j).getNumber().equalsIgnoreCase("205")){
//                        mealModel.setCarbs(response.get(i).getFoodNutrients().get(j).getAmount());
//                    }
//                    if(response.get(i).getFoodNutrients().get(j).getNumber().equalsIgnoreCase("204")){
//                        mealModel.setFats(response.get(i).getFoodNutrients().get(j).getAmount());
//                    }
//                    if(response.get(i).getFoodNutrients().get(j).getNumber().equalsIgnoreCase("208")){
//                        mealModel.setTotal_cal(response.get(i).getFoodNutrients().get(j).getAmount());
//                    }
//                }
//                break_list.add(mealModel);
//            }
//
//
//            Collections.sort(break_list, new Comparator<MealModel>() {
//                @Override
//                public int compare(MealModel o1, MealModel o2) {
//                    return Float.compare(o2.getProtein(), o1.getProtein());
//                }
//            });
//
//        }
//    }

    void openCollapse(){
        try {

            meal_1.setOnExpandedListener((v, isExpanded) -> {
                createMeal(1,moderateCarbMeal);
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
                createMeal(2,highCarbMeal);
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
                createMeal(3,lowCarbMeal);
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




    //Meal 1 = Moderate Carbs: p-moderate c-moderate f-moderate
    ArrayList<Results> getMeal(int p, int c, int f){

        ArrayList<Results> results = new ArrayList<>();
        Results breakfast = new Results(selected_protein.get(p),
                selected_carbs.get(c),
                selected_fats.get(f));

        Results lunch = new Results(selected_protein.get(p-1),
                selected_carbs.get(c-1),
                selected_fats.get(f-1));

        Results dinner = new Results(selected_protein.get(p+1),
                selected_carbs.get(c+1),
                selected_fats.get(f+1));

        results.add(breakfast);
        results.add(lunch);
        results.add(dinner);

        return results;
    }

    void createMeal(int mealNo,ArrayList<Results> meal){
        View v =meal_1;
        if(mealNo == 1){
            v=meal_1;
        }else if (mealNo == 2){
            v=meal_2;
        }
        else if (mealNo ==3){
            v=meal_3;
        }
        food_name_protein_break = v.findViewById(R.id.food_name_protein_break);
        food_name_protein_lunch = v.findViewById(R.id.food_name_protein_lunch);
        food_name_protein_dinner = v.findViewById(R.id.food_name_protein_dinner);

        food_name_carb_break = v.findViewById(R.id.food_name_carb_break);
        food_name_carb_lunch = v.findViewById(R.id.food_name_carb_lunch);
        food_name_carb_dinner = v.findViewById(R.id.food_name_carb_dinner);

        food_name_fat_break = v.findViewById(R.id.food_name_fat_break);
        food_name_fat_lunch = v.findViewById(R.id.food_name_fat_lunch);
        food_name_fat_dinner = v.findViewById(R.id.food_name_fat_dinner);

        calories_protein_break = v.findViewById(R.id.calories_protein_break);
        calories_protein_lunch = v.findViewById(R.id.calories_protein_lunch);
        calories_protein_dinner = v.findViewById(R.id.calories_protein_dinner);

        calories_carb_break = v.findViewById(R.id.calories_carb_break);
        calories_carb_lunch = v.findViewById(R.id.calories_carb_lunch);
        calories_carb_dinner = v.findViewById(R.id.calories_carb_dinner);

        calories_fat_break = v.findViewById(R.id.calories_fat_break);
        calories_fat_lunch = v.findViewById(R.id.calories_fat_lunch);
        calories_fat_dinner = v.findViewById(R.id.calories_fat_dinner);

            FoodList protein_break =  meal.get(0).protienFood;
            FoodList carb_break =  meal.get(0).carbsFood;
            FoodList fat_break =  meal.get(0).fatFood;

            food_name_protein_break.setText(protein_break.getVal());
            calories_protein_break.setText(protein_break.getCalories()+" "+"cal");

            food_name_carb_break.setText(carb_break.getVal());
            calories_carb_break.setText(carb_break.getCalories()+" "+"cal");

            food_name_fat_break.setText(fat_break.getVal());
            calories_fat_break.setText(fat_break.getCalories()+" "+"cal");

            FoodList protein_lunch =  meal.get(1).protienFood;
            FoodList carb_lunch =  meal.get(1).carbsFood;
            FoodList fat_lunch =  meal.get(1).fatFood;

            food_name_protein_lunch.setText(protein_lunch.getVal());
            calories_protein_break.setText(protein_lunch.getCalories()+" "+"cal");

            food_name_carb_lunch.setText(carb_lunch.getVal());
            calories_carb_lunch.setText(carb_lunch.getCalories()+" "+"cal");

            food_name_fat_lunch.setText(fat_lunch.getVal());
            calories_fat_lunch.setText(fat_lunch.getCalories()+" "+"cal");

            FoodList protein_dinner =  meal.get(2).protienFood;
            FoodList carb_dinner =  meal.get(2).carbsFood;
            FoodList fat_dinner =  meal.get(2).fatFood;

            food_name_protein_dinner.setText(protein_dinner.getVal());
            calories_protein_dinner.setText(protein_dinner.getCalories()+" "+"cal");

            food_name_carb_dinner.setText(carb_dinner.getVal());
            calories_carb_dinner.setText(carb_dinner.getCalories()+" "+"cal");

            food_name_fat_dinner.setText(fat_dinner.getVal());
            calories_fat_dinner.setText(fat_dinner.getCalories()+" "+"cal");

    }

}
