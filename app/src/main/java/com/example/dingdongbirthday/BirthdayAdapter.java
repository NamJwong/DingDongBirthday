package com.example.dingdongbirthday;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

public class BirthdayAdapter extends RecyclerView.Adapter<BirthdayAdapter.CustomViewHolder> {
    private Context context;
    private ArrayList<BirthdayItem> birthdayItems;
    private LayoutInflater inflater;

    public BirthdayAdapter(Context context, ArrayList<BirthdayItem> birthdayItems) {
        this.context = context;
        this.birthdayItems = birthdayItems;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_recyclerview_birthday, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        BirthdayItem item = birthdayItems.get(position);
        holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return birthdayItems.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewPhoto;
        TextView textViewName;
        TextView textViewAge;
        CardView cardViewGroup;
        TextView textViewGroup;
        TextView textViewBirthday;
        TextView textViewMemo;
        ImageButton buttonBookmark;
        TextView textViewDDay;

        public CustomViewHolder(View itemView) {
            super(itemView);
            imageViewPhoto = itemView.findViewById(R.id.imageViewPhoto);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewAge = itemView.findViewById(R.id.textViewAge);
            cardViewGroup = itemView.findViewById(R.id.cardViewGroup);
            textViewGroup = itemView.findViewById(R.id.textViewGroup);
            textViewBirthday = itemView.findViewById(R.id.textViewBirthday);
            textViewMemo = itemView.findViewById(R.id.textViewMemo);
            buttonBookmark = itemView.findViewById(R.id.buttonBookmark);
            textViewDDay = itemView.findViewById(R.id.textViewDDay);

            buttonBookmark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION) {
                        BirthdayItem item = birthdayItems.get(position);
                        if(item.getBookmark() == 1) {
                            buttonBookmark.setImageResource(R.drawable.bookmark_off);
                            String sql = "update " + BirthdayDatabase.TABLE_BIRTHDAY
                                    + " set BOOKMARK = " + 0
                                    + " where _id = " + item.getId();
                            MainActivity.database.execSQL(sql);
                            MainActivity.categoryAdapter = new CategoryAdapter(MainActivity.getLoadBirthdayItems(), MainActivity.context);
                            MainActivity.recyclerViewCategory.setAdapter(MainActivity.categoryAdapter);
                        }else {
                            buttonBookmark.setImageResource(R.drawable.bookmark_on);
                            String sql = "update " + BirthdayDatabase.TABLE_BIRTHDAY
                                    + " set BOOKMARK = " + 1
                                    + " where _id = " + item.getId();
                            MainActivity.database.execSQL(sql);
                            MainActivity.categoryAdapter = new CategoryAdapter(MainActivity.getLoadBirthdayItems(), MainActivity.context);
                            MainActivity.recyclerViewCategory.setAdapter(MainActivity.categoryAdapter);
                        }
                    }
                }
            });
        }

        public void setItem(BirthdayItem item) {
            Calendar calendar = Calendar.getInstance();
            int currentYear = calendar.get(Calendar.YEAR);
            int currentMonth = calendar.get(Calendar.MONTH) + 1;
            int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
            int birthdayYear = Integer.parseInt(item.getBirthday().substring(0,4));
            int birthdayMonth = Integer.parseInt(item.getBirthday().substring(4,6));
            int birthdayDay = Integer.parseInt(item.getBirthday().substring(6,8));

            if(item.getUriPhoto() == null)
                imageViewPhoto.setImageResource(R.drawable.basic_user_image);
            else {
                imageViewPhoto.setImageURI(item.getUriPhoto());
            }

            textViewName.setText(item.getName());

            int age = currentYear - birthdayYear + 1;
            textViewAge.setText("(" + age + ")");

            if(item.getGroup().equals("설정 안 함"))
                cardViewGroup.setVisibility(View.GONE);
            else {
                textViewGroup.setText(item.getGroup());
                if(item.getGroup().equals("친구"))
                    cardViewGroup.setCardBackgroundColor(Color.parseColor("#9FF781"));
                else if(item.getGroup().equals("가족"))
                    cardViewGroup.setCardBackgroundColor(Color.parseColor("#F5A9F2"));
                else if(item.getGroup().equals("지인"))
                    cardViewGroup.setCardBackgroundColor(Color.parseColor("#F7BE81"));
                else if(item.getGroup().equals("회사"))
                    cardViewGroup.setCardBackgroundColor(Color.parseColor("#81DAF5"));
                else if(item.getGroup().equals("학교"))
                    cardViewGroup.setCardBackgroundColor(Color.parseColor("#BE81F7"));
                else if(item.getGroup().equals("지인"))
                    cardViewGroup.setCardBackgroundColor(Color.parseColor("#D8D8D8"));
            }

            textViewBirthday.setText(birthdayYear + "." + item.getBirthday().substring(4,6) + "." + item.getBirthday().substring(6,8));

            textViewMemo.setText(item.getMemo());

            if(item.getBookmark() == 1)
                buttonBookmark.setImageResource(R.drawable.bookmark_on);
            else
                buttonBookmark.setImageResource(R.drawable.bookmark_off);

            int comingBirthdayYear = calendar.get(Calendar.YEAR);
            if((currentMonth > birthdayMonth) || ((currentMonth == birthdayMonth) && (currentDay > birthdayDay)))
                comingBirthdayYear = calendar.get(Calendar.YEAR) + 1;
            Calendar today = Calendar.getInstance();
            today.set(currentYear, currentMonth, currentDay);
            Calendar birthday = Calendar.getInstance();
            birthday.set(comingBirthdayYear, birthdayMonth, birthdayDay);
            int dDay = (int) (today.getTimeInMillis()/(24*60*60*1000) - birthday.getTimeInMillis()/(24*60*60*1000));
            if(dDay == 0)
                textViewDDay.setText("D-DAY");
            else
                textViewDDay.setText("D" + dDay);
        }

    }
}
