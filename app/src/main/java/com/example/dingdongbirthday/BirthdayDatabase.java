package com.example.dingdongbirthday;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BirthdayDatabase {

    private static BirthdayDatabase birthdayDatabase; // 싱글톤 인스턴스
    public static String DATABASE_NAME = "birthday.db";
    public static String TABLE_BIRTHDAY = "BIRTHDAY"; // 생일 테이블 명
    public static int DATABASE_VERSION = 2;
    private DataBaseHelper dataBaseHelper;
    private SQLiteDatabase database;
    private Context context;

    private BirthdayDatabase(Context context) {
        this.context = context;
    }

    public static BirthdayDatabase getInstance(Context context) {
        if(birthdayDatabase == null) {
            birthdayDatabase = new BirthdayDatabase(context);
        }
        return birthdayDatabase;
    }

    // 데이터베이스 열기
    public void open() {
        dataBaseHelper = new DataBaseHelper(context);
        database = dataBaseHelper.getWritableDatabase();
    }

    // 데이터베이스 닫기
    public void close() {
        database.close();
        birthdayDatabase = null;
    }

    public Cursor rawQuery(String SQL) {
        Cursor c1 = null;
        c1 = database.rawQuery(SQL, null);

        return c1;
    }

    public boolean execSQL(String SQL) {
        try {
            database.execSQL(SQL);
        } catch(Exception ex) {
            return false;
        }
        return true;
    }

    private class DataBaseHelper extends SQLiteOpenHelper {
        public DataBaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase database) {
            // 생일 테이블과 같은 이름의 테이블이 존재할 시 삭제
            String DROP_SQL = "drop table if exists " + TABLE_BIRTHDAY;
            database.execSQL(DROP_SQL);

            // 테이블 생성
            String CREATE_BIRTHDAY_SQL = "create table " + TABLE_BIRTHDAY + "("
                                                        + "  _id INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT, "
                                                        + " IMAGE TEXT DEFAULT '', "
                                                        + " NAME TEXT DEFAULT '', "
                                                        + " BIRTHDAY_DATE TEXT DEFAULT '', "
                                                        + " GROUP_NAME TEXT DEFAULT '', "
                                                        + " ALARM_14 INTEGER DEFAULT 0, "
                                                        + " ALARM_7 INTEGER DEFAULT 0, "
                                                        + " ALARM_3 INTEGER DEFAULT 0, "
                                                        + " ALARM_1 INTEGER DEFAULT 0, "
                                                        + " ALARM_0 INTEGER DEFAULT 0, "
                                                        + " MEMO TEXT DEFAULT '',"
                                                        + " BOOKMARK INTEGER DEFAULT 0"
                                                        + ")";
            database.execSQL(CREATE_BIRTHDAY_SQL);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.d("BirthdayDatabase", "Upgrading database from version " + oldVersion + " to" + newVersion + ".");
        }
    }




}
