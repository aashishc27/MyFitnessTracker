package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.lifecycle.ViewModel;

import com.example.myfitnesstracker.R;

import java.util.ArrayList;

import models.MealModel;

public class MealAdapter extends BaseAdapter {
    ArrayList<MealModel> mealModelList;
    Context context;

    public MealAdapter(Context context, ArrayList<MealModel> mealModelList) {

        this.mealModelList  = mealModelList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return mealModelList.size();
    }

    @Override
    public Object getItem(int position) {
        return mealModelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder  viewHolder ;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_meal_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        MealModel currentItem = (MealModel) getItem(position);

        viewHolder.food_name.setText(currentItem.getName());
        viewHolder.calories.setText(currentItem.getCalories());
        viewHolder.portion_size.setText(currentItem.getPotion_size());

        return convertView;
    }



    private class ViewHolder {
        TextView food_name,calories,portion_size;

        public ViewHolder(View convertView) {
            food_name = convertView.findViewById(R.id.food_name);
            calories = convertView.findViewById(R.id.calories);
            portion_size = convertView.findViewById(R.id.portion_size);
        }
    }
}
