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
import models.FoodModel;
import test.Food_RecyclerView_Main;

public class FoodItemSelection extends AppCompatActivity {

    Fragment mFragment ;
    ArrayList<String> carbs,fats,protein;
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

        myDb = new DatabaseHelper(this);



        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                FoodItemModel post = dataSnapshot.getValue(FoodItemModel.class);

                carbs = post.getCarbs();
                fats = post.getFats();
                for(int i = 0;i<post.getProtein().size();i++){
                    if(!TextUtils.isEmpty(food_pref)&&food_pref.equalsIgnoreCase(post.getProtein().get(i).getType())){
                        protein.add(post.getProtein().get(i).getVal());
                    }

                }


                System.out.println(post);

                if(screen == 1){
                    startFragment(protein,"Protein");
                }else if(screen == 2){
                    startFragment(carbs,"Carbs");
                }else if(screen == 3){
                    startFragment(fats,"Fats");
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



    void startFragment(ArrayList<String> product,String type){
        for(int i = 0;i<product.size();i++){
            FoodModel foodModel = new FoodModel();
            foodModel.setName(product.get(i));

            food_list.add(foodModel);
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_list, Food_RecyclerView_Main.newInstance()).commit();

//        mFragment = new FoodListFragment(food_list,type,screen);
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentManager.beginTransaction()
//                .replace(R.id.fl_list, mFragment).commit();



    }
}
