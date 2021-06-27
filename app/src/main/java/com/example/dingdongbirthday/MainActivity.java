package com.example.dingdongbirthday;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;

import java.util.ArrayList;
import java.util.Calendar;

// 메인 화면 Activity
public class MainActivity extends AppCompatActivity implements TextWatcher, AutoPermissionsListener {
    public static BirthdayDatabase database = null; // 데이터베이스 인스턴스
    public static CategoryAdapter categoryAdapter;
    public static RecyclerView recyclerViewCategory;
    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this.getApplicationContext();

        AutoPermissions.Companion.loadAllPermissions(this, 101);

        openDatabase();

        // 생일 등록 화면으로 이동하는 버튼
        ImageButton buttonGoAddBirthday = (ImageButton) findViewById(R.id.buttonGoAddBirthday);
        buttonGoAddBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddBirthdayActivity.class);
                startActivity(intent);
            }
        });

        // 검색창 - 이름 기준
        EditText editTextSearch = (EditText) findViewById(R.id.editTextSearch);
        editTextSearch.addTextChangedListener(this);

        // 데이터베이스에서 생일 정보 받아오기
        ArrayList<BirthdayItem> loadBirthdayItems = getLoadBirthdayItems();

        // 생일 리스트의 범주 리사이클러뷰 - 중첩 리사이클러뷰의 바깥 리사이클러뷰
        recyclerViewCategory = (RecyclerView) findViewById(R.id.recyclerViewCategory);
        categoryAdapter = new CategoryAdapter(loadBirthdayItems, this);
        recyclerViewCategory.setAdapter(categoryAdapter);
        LinearLayoutManager categoryLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewCategory.setLayoutManager(categoryLayoutManager);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(database != null) {
            database.close();
            database = null;
        }
    }

    // 데이터베이스 열기 (데이터베이스가 없을 때는 만들기)
    public  void openDatabase() {
        if(database != null) {
            database.close();
            database = null;
        }
        database = BirthdayDatabase.getInstance(this);
        database.open();
    }

    public static ArrayList<BirthdayItem> getLoadBirthdayItems() {
        String sql = "select _id, IMAGE, NAME, BIRTHDAY_DATE, GROUP_NAME, ALARM_14, ALARM_7, ALARM_3, ALARM_1, ALARM_0, MEMO, BOOKMARK"
                    + " from " + BirthdayDatabase.TABLE_BIRTHDAY;
        int recordCount = -1;
        BirthdayDatabase database = BirthdayDatabase.getInstance(context);
        ArrayList<BirthdayItem> loadBirthdayItems = new ArrayList<BirthdayItem>();
        if(database != null) {
            Cursor outCursor = database.rawQuery(sql);
            recordCount = outCursor.getCount();
            for(int i = 0; i < recordCount; i++) {
                outCursor.moveToNext();
                int id = outCursor.getInt(0);
                Uri imageUri = Uri.parse(outCursor.getString(1));
                if(outCursor.getString(1).equals("null"))
                    imageUri = null;
                String name = outCursor.getString(2);
                String birthday = outCursor.getString(3);
                String group = outCursor.getString(4);
                int[] alarms = new int[5];
                for(int j = 0; j < 5; j++) {
                    alarms[j] = outCursor.getInt(j + 5);
                }
                String memo = outCursor.getString(10);
                int bookmark = outCursor.getInt(11);
                loadBirthdayItems.add(new BirthdayItem(id, imageUri, name, birthday, group, alarms, memo, bookmark));
            }
            outCursor.close();
        }
        return loadBirthdayItems;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        AutoPermissions.Companion.parsePermissions(this, requestCode, permissions, this);
    }

    @Override
    public void onDenied(int requestCode, String[] permissions) {
        //Toast.makeText(this, "permissions denied : " + permissions.length, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onGranted(int requestCode, String[] permissions) {
        //Toast.makeText(this, "permissions granted : " + permissions.length, Toast.LENGTH_LONG).show();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        categoryAdapter.getFilter().filter(s);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}