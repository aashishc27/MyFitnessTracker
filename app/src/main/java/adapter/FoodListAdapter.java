package adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.myfitnesstracker.R;

import java.util.ArrayList;

import models.FoodModel;

public class FoodListAdapter extends BaseAdapter implements Filterable {

    private Context mContext;
    private TextView tv_suggestion;
    private ListView lv_list;
    private ArrayList<FoodModel> allProductList;
    private ArrayList<FoodModel> listToDisplay;
    private ArrayList<String> uncheckList;
    private productFilter mFilter = new productFilter();

    public FoodListAdapter(Context context, ArrayList<FoodModel> productList, ArrayList<String> uncheckList, TextView suggestion, ListView list) {

        this.mContext = context;
        this.allProductList = productList;
        this.uncheckList = uncheckList;
        this.listToDisplay = productList;
        this.tv_suggestion = suggestion;
        this.lv_list = list;

    }

    @Override
    public int getCount() {
        return listToDisplay.size();
    }

    @Override
    public FoodModel getItem(int position) {
        return listToDisplay.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View productView, ViewGroup parent) {

        if (productView == null) {
            productView = LayoutInflater.from(mContext).inflate(mContext.getResources().getLayout(R.layout.adapter_food_item), parent, false);
        }

        FoodModel currentProduct = getItem(position);

        TextView productName = productView.findViewById(R.id.tv_name);
        CheckBox productPicked = productView.findViewById(R.id.checkBox);
        RelativeLayout ll_main = productView.findViewById(R.id.ll_main);

        productName.setText(currentProduct.getName());

        productPicked.setChecked(currentProduct.isSelected());

        if (currentProduct.isSelected()) {
            ll_main.setBackgroundColor(mContext.getResources().getColor(R.color.http_color));
            productName.setTextColor(mContext.getResources().getColor(R.color.white));
        } else {
            ll_main.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            productName.setTextColor(mContext.getResources().getColor(R.color.default_partner_color));

        }

        if (uncheckList.size() > 0) {
            for (int i = 0; i < listToDisplay.size(); i++) {
                for (int j = 0; j < uncheckList.size(); j++) {
                    if (listToDisplay.get(i).getName().contains(uncheckList.get(j))) {
                        if (listToDisplay.get(i).isSelected()) {
                            listToDisplay.get(i).setSelected(false);
                        }

                    }
                }

            }
        }

        return productView;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    public void setSelectedIndex(int ind, boolean selected) {
        allProductList.get(ind).setSelected(selected);
        listToDisplay.get(ind).setSelected(selected);
        //notifyDataSetChanged();
    }

    public FoodModel getFilteredList(int position) {
        return listToDisplay.get(position);
    }

    private class productFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults();

            if (TextUtils.isEmpty(constraint)) {

                results.values = allProductList;
                results.count = allProductList.size() + 1;
                return results;

            } else {

                ArrayList<FoodModel> nListToDisplay = new ArrayList<>();
                for (FoodModel p : allProductList) {
                    if (p.getName().toUpperCase().startsWith(constraint.toString().toUpperCase()))
                        nListToDisplay.add(p);
                }

                results.values = nListToDisplay;
                results.count = nListToDisplay.size();

            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {


            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) lv_list.getLayoutParams();

            if (results.count == allProductList.size() + 1) {

                listToDisplay = (ArrayList<FoodModel>) results.values;

                tv_suggestion.setVisibility(View.GONE);

            } else if (results.count == 0) {
                tv_suggestion.setVisibility(View.GONE);
                listToDisplay.clear();
            } else {
                listToDisplay = (ArrayList<FoodModel>) results.values;
                tv_suggestion.setVisibility(View.VISIBLE);
            }

            notifyDataSetChanged();


        }
    }


}
