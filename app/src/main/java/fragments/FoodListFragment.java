package fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.myfitnesstracker.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import activity.DietChartActivity;
import activity.FoodItemSelection;
import adapter.FoodListAdapter;
import helper.CommonConstants;
import helper.Util;
import models.FoodModel;

public class FoodListFragment extends Fragment {

    ListView lv_food;
    TextView tv_food_header;
    FoodListAdapter foodListAdapter;
    String type;
    ArrayList<FoodModel> garbageProduct = new ArrayList<>();
    ArrayList<FoodModel> productListToSend = new ArrayList<>();
    ArrayList<String> uncheckproductList,food_name;
    ArrayList<FoodModel> productList, selectedproductList,productListSelected;
    Button submit;
    int screen ;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;


    public FoodListFragment(ArrayList<FoodModel> productList,String type,int screen) {
        this.productList = productList;
        this.type = type;
        this.screen = screen;
    }

    @Override
    public void onResume() {
        super.onResume();

        Gson gson = new Gson();
        String json = "" ;
        if(screen ==1){
            json = sharedPreferences.getString(CommonConstants.SELCTED_PROTEIN, "");
        }else if(screen ==2){
            json = sharedPreferences.getString(CommonConstants.SELCTED_CARBS, "");
        }else if(screen ==3){
            json = sharedPreferences.getString(CommonConstants.SELCTED_FATS, "");
        }
        Type type = new TypeToken<ArrayList<FoodModel>>(){}.getType();

        ArrayList<FoodModel> selectedList = gson.fromJson(json, type);

        if(selectedList != null){
            for(int i = 0;i<productList.size();i++){
                for(int j = 0;j<selectedList.size();j++){
                    if(selectedList.get(j).getName().equalsIgnoreCase(productList.get(i).getName())){
                        productList.get(i).setSelected(true);
                    }
                }
            }
        }

        for(int i = 0;i<productList.size();i++){
            if(productList.get(i).isSelected()){
                FoodModel foodModel =new FoodModel();
                foodModel.setName(productList.get(i).getName());
                foodModel.setSelected(productList.get(i).isSelected());

                selectedproductList.add(foodModel);
            }
        }



            foodListAdapter = new FoodListAdapter(getActivity(), productList, uncheckproductList, tv_food_header, lv_food);
            lv_food.setAdapter(foodListAdapter);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_food_list, container, false);


        init(view);

        listener();


        return view;
    }

    void init(View view){
        lv_food = view.findViewById(R.id.lv_food);
        tv_food_header = view.findViewById(R.id.tv_food_type);

        tv_food_header.setText("Select "+ type +" you like:");


        productListSelected = new ArrayList<>();
        uncheckproductList = new ArrayList<>();
        selectedproductList = new ArrayList<>();

        submit = view.findViewById(R.id.submit);

        sharedPreferences = getActivity().getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();



    }

    void listener(){

        lv_food.setOnItemClickListener((parent, view, position, id) -> {

            FoodModel itemObject = foodListAdapter.getItem(position);

            // Translate the selected item to DTO object.
//                UpdatedBankListModel itemDto = (UpdatedBankListModel)itemObject;

            // Get the checkbox.
            CheckBox itemCheckbox = view.findViewById(R.id.checkBox);
            RelativeLayout ll_main = view.findViewById(R.id.ll_main);
            TextView productName = view.findViewById(R.id.tv_name);

            // Reverse the checkbox and clicked item check state.
            if (itemObject.isSelected()) {
                // for click of ^ ka reverse.
//                removedProducts.add(itemObject);

                itemCheckbox.setChecked(false);
                ll_main.setBackgroundColor(this.getResources().getColor(R.color.white));
                productName.setTextColor(this.getResources().getColor(R.color.default_partner_color));
                itemObject.setSelected(false);
                foodListAdapter.setSelectedIndex(position, false);
                for (FoodModel product : selectedproductList) {
                    if (product.getName().trim().equalsIgnoreCase(foodListAdapter.getFilteredList(position).getName().trim())) {
                        garbageProduct.add(product);
                        // productListAdapter.getFilteredList(position).setIs_picked(false);
                        // productListAdapter.getFilteredList(position).setSelected(false);
                    }
                }
                selectedproductList.removeAll(garbageProduct);
                garbageProduct.clear();
//                selectedproductList.remove(productListAdapter.getFilteredList(position));

            } else {
                // for click of ^ ka reverse.
//                removedProducts.remove(itemObject);
                boolean add = true;
                itemCheckbox.setChecked(true);
                ll_main.setBackgroundColor(this.getResources().getColor(R.color.http_color));
                productName.setTextColor(this.getResources().getColor(R.color.white));
                itemObject.setSelected(true);
                foodListAdapter.setSelectedIndex(position, true);
                for (FoodModel product : selectedproductList) {
                    if (product.getName().trim().equalsIgnoreCase(itemObject.getName()))
                        add = false;
                }
                if (add) selectedproductList.add(foodListAdapter.getFilteredList(position));
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(selectedproductList.size()>0){
                    Gson gson = new Gson();
                    if(screen == 1){
                        String json = gson.toJson(selectedproductList);
                        editor.putString(CommonConstants.SELCTED_PROTEIN,json);
                    }else if(screen == 2){
                        String json = gson.toJson(selectedproductList);
                        editor.putString(CommonConstants.SELCTED_CARBS,json);
                    }else if(screen == 3){
                        String json = gson.toJson(selectedproductList);
                        editor.putString(CommonConstants.SELCTED_FATS,json);
                    }
                    editor.apply();
                    screen = screen+1;
                    if(screen <4){
                        Intent intent = new Intent(getActivity(), FoodItemSelection.class);
                        intent.putExtra("screen",screen);
                        startActivity(intent);
                    }else{
                        Intent intent = new Intent(getActivity(), DietChartActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }else{
                    Util.showToast("No Items Selected",getActivity());
                }


            }
        });
    }
}
