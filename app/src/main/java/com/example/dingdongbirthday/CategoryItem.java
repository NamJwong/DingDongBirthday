package com.example.dingdongbirthday;

import java.util.ArrayList;

public class CategoryItem {
    private String category;
    private ArrayList<BirthdayItem> birthdayItems;
    private boolean isVisible;

    public CategoryItem(String category, ArrayList<BirthdayItem> birthdayItems) {
        this.category = category;
        this.birthdayItems = birthdayItems;
        this.isVisible = true;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public ArrayList<BirthdayItem> getBirthdayItems() {
        return birthdayItems;
    }

    public void setBirthdayItems(ArrayList<BirthdayItem> birthdayItems) {
        this.birthdayItems = birthdayItems;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }
}
