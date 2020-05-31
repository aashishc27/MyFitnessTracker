package activity;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
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
import models.FoodItemModel;
import models.FoodModel;

public class FoodItemSelection extends AppCompatActivity {

    Fragment mFragment ;
    ArrayList<String> carbs,fats,protein;
    ArrayList<FoodModel> food_list;
    ActionBar actionBar;
    int screen;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);
        food_list = new ArrayList<>();
        Intent intent = getIntent();
        screen = intent.getIntExtra("screen",1);
        actionBar = findViewById(R.id.action_bar);

        actionBar.setLeftTitle("Food Selector");

        protein = new ArrayList<>();

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
                    protein.add(post.getProtein().get(i).getVal());
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



        mFragment = new FoodListFragment(food_list,type,screen);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fl_list, mFragment).commit();



    }
}
