package com.example.dingdongbirthday;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CustomViewHolder> implements Filterable {
    private Context context;
    private ArrayList<CategoryItem> unfilteredCategoryItems;
    private ArrayList<CategoryItem> filteredCategoryItems;
    public static ArrayList<BirthdayItem> loadBirthdayItems;
    private LayoutInflater layoutInflater;

    public CategoryAdapter(ArrayList<BirthdayItem> loadBirthdayItems, Context context) {
        this.context = context;
        this.loadBirthdayItems = loadBirthdayItems;
        this.unfilteredCategoryItems = getCategoryItems(loadBirthdayItems);
        this.filteredCategoryItems = getCategoryItems(loadBirthdayItems);
//        Log.d("d", Integer.toString(unfilteredCategoryItems.get(1).getBirthdayItems().size()));
//        Log.d("d", unfilteredCategoryItems.get(1).getBirthdayItems().get(unfilteredCategoryItems.get(1).getBirthdayItems().size() - 1).getName());
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_recyclerview_category, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        holder.recyclerView.setAdapter(new BirthdayAdapter(context, filteredCategoryItems.get(position).getBirthdayItems()));
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        holder.textViewCategory.setText(filteredCategoryItems.get(position).getCategory());
    }

    @Override
    public int getItemCount() {
        return filteredCategoryItems.size();
    }

    public static ArrayList<CategoryItem> getCategoryItems(ArrayList<BirthdayItem> loadBirthdayItems) {
        Collections.sort(loadBirthdayItems);
        ArrayList<CategoryItem> categoryItems = new ArrayList<CategoryItem>();

        categoryItems.add(new CategoryItem("즐겨찾기", new ArrayList<BirthdayItem>()));
        for(int i = 0; i < loadBirthdayItems.size(); i++) {
            if(loadBirthdayItems.get(i).getBookmark() == 1) {
                categoryItems.get(0).getBirthdayItems().add(loadBirthdayItems.get(i));
            }
        }

        for(int i = 0; i < loadBirthdayItems.size(); i++) {
            boolean isAddCategory = false;
            for(int j = 1; j < categoryItems.size(); j++) {
                if(categoryItems.get(j).getCategory().substring(5,7).equals(loadBirthdayItems.get(i).getBirthday().substring(4,6))) {
                    categoryItems.get(j).getBirthdayItems().add(loadBirthdayItems.get(i));
                    isAddCategory = true;
                    break;
                }
            }
            if(isAddCategory == false) {
                Calendar calendar = Calendar.getInstance();
                int currentMonth = calendar.get(Calendar.MONTH) + 1;
                int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
                int comingBirthdayYear;
                int birthdayMonth = Integer.parseInt(loadBirthdayItems.get(i).getBirthday().substring(4,6));
                int birthdayDay = Integer.parseInt(loadBirthdayItems.get(i).getBirthday().substring(6,8));
                if((currentMonth > birthdayMonth) || ((currentMonth == birthdayMonth) && (currentDay > birthdayDay)))
                    comingBirthdayYear = calendar.get(Calendar.YEAR) + 1;
                else
                    comingBirthdayYear = calendar.get(Calendar.YEAR);
                String strBirthdayMonth = Integer.toString(birthdayMonth);
                if(birthdayMonth < 10)
                    strBirthdayMonth = "0" + birthdayMonth;
                CategoryItem newCategoryItem = new CategoryItem(comingBirthdayYear + "-" + strBirthdayMonth, new ArrayList<BirthdayItem>());
                newCategoryItem.getBirthdayItems().add(loadBirthdayItems.get(i));
                categoryItems.add(newCategoryItem);
            }
        }
        return categoryItems;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String searchWords = constraint.toString();
                if(searchWords.isEmpty())
                    filteredCategoryItems = unfilteredCategoryItems;
                else {
                    ArrayList<BirthdayItem> filteringBirthdayItems = new ArrayList<BirthdayItem>();
                    for(int i = 0; i < loadBirthdayItems.size(); i++) {
                        if(loadBirthdayItems.get(i).getName().contains(searchWords))
                            filteringBirthdayItems.add(loadBirthdayItems.get(i));





//                        int filteredItemCount = 0;
//                        CategoryItem temp = new CategoryItem(unfilteredCategoryItems.get(i).getCategory(), new ArrayList<BirthdayItem>());
//                        for(int j = 0; j < unfilteredCategoryItems.get(i).getBirthdayItems().size(); j++) {
//                            if(unfilteredCategoryItems.get(i).getBirthdayItems().get(j).getName().contains(searchWords)) {
//                                temp.getBirthdayItems().add(unfilteredCategoryItems.get(i).getBirthdayItems().get(j));
//                                filteredItemCount++;
//                            }
//                        }
//                        if(filteredItemCount > 0)
//                            filteringCategoryItems.add(temp);
                    }
                    filteredCategoryItems = getCategoryItems(filteringBirthdayItems);
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredCategoryItems;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredCategoryItems = (ArrayList<CategoryItem>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerView;
        TextView textViewCategory;
        ImageButton buttonExpand;

        public CustomViewHolder(View itemView) {
            super(itemView);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.recyclerViewBirthday);
            textViewCategory = (TextView) itemView.findViewById(R.id.textViewCategory);
            buttonExpand = (ImageButton) itemView.findViewById(R.id.buttonExpand);

            buttonExpand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION) {
                        CategoryItem item = filteredCategoryItems.get(position);
                        if(item.isVisible()) {
                            recyclerView.setVisibility(View.GONE);
                            item.setVisible(false);
                            buttonExpand.setRotation(0);
                        }else {
                            recyclerView.setVisibility(View.VISIBLE);
                            item.setVisible(true);
                            buttonExpand.setRotation(180);
                        }
                    }
                }
            });
        }
    }



}
