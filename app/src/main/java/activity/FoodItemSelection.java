package activity;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.myfitnesstracker.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import fragments.FoodListFragment;
import helper.DatabaseHelper;
import models.FoodItemModel;
import models.FoodList;
import models.FoodModel;
import test.Food_RecyclerView_Main;

public class FoodItemSelection extends AppCompatActivity {

    Fragment mFragment ;
    ArrayList<String> carbs,fats,protein;
    ArrayList<FoodList> carb_1,fats_1,protein_1;
    ArrayList<FoodModel> food_list;
    ActionBar actionBar;
    String food_pref;
    int screen;
    DatabaseHelper myDb;


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);
        food_list = new ArrayList<>();
        Intent intent = getIntent();
        screen = intent.getIntExtra("screen",1);
        food_pref = intent.getStringExtra("food_pref");

        actionBar = findViewById(R.id.action_bar);

        actionBar.setLeftTitle("Food Selector");

        protein = new ArrayList<>();

        carb_1 = new ArrayList<>();
        fats_1 = new ArrayList<>();
        protein_1 = new ArrayList<>();

        carbs = new ArrayList<>();
        fats = new ArrayList<>();

        myDb = new DatabaseHelper(this);



        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                FoodItemModel post = dataSnapshot.getValue(FoodItemModel.class);

                carb_1 = post.getCarbs();
                fats_1  = post.getFats();



                if(food_pref!=null&&food_pref.equalsIgnoreCase(getResources().getString(R.string.tag_both))){
                    for(int i = 0;i<post.getProtein().size();i++){
                        FoodList item = post.getProtein().get(i);
                        protein_1.add(item);
                    }
                }else {
                    for(int j = 0;j<post.getProtein().size();j++){
                        if(!TextUtils.isEmpty(food_pref)&&food_pref.equalsIgnoreCase(post.getProtein().get(j).getType())){
                            FoodList item = post.getProtein().get(j);
                            protein_1.add(item);
                        }
                    }
                }



                System.out.println(post);

                if(screen == 1){
                    startFragment(protein_1,"Protein");
                }else if(screen == 2){
                    startFragment(carb_1,"Carbs");
                }else if(screen == 3){
                    startFragment(fats_1,"Fats");
                }  else{
                    finish();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }



    void startFragment(ArrayList<FoodList> product,String type){
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.fl_list, Food_RecyclerView_Main.newInstance()).commit();

        mFragment = new FoodListFragment(product,type,screen);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fl_list, mFragment).commit();



    }
}
