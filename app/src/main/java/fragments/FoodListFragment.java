package fragments;

import android.content.Intent;
import android.os.Bundle;
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

import java.util.ArrayList;

import activity.FoodItemSelection;
import adapter.FoodListAdapter;
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


    public FoodListFragment(ArrayList<FoodModel> productList,String type,int screen) {
        this.productList = productList;
        this.type = type;
        this.screen = screen;
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


        foodListAdapter = new FoodListAdapter(getActivity(), productList, uncheckproductList, tv_food_header, lv_food);
        lv_food.setAdapter(foodListAdapter);

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
                screen = screen+1;
                Intent intent = new Intent(getActivity(), FoodItemSelection.class);
                intent.putExtra("screen",screen);
                startActivity(intent);
            }
        });
    }
}
