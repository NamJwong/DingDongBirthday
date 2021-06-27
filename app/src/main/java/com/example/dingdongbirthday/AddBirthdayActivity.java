package com.example.dingdongbirthday;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

// 생일 추가 화면 Activity

public class AddBirthdayActivity extends AppCompatActivity implements TextWatcher {
    String photoPath;
    Uri photoUri;
    ImageButton buttonSetPhoto;
    boolean isSetPhoto;
    Bitmap photoBitmap;

    String name;
    EditText editTextSetName;

    String birthday;
    boolean isShowDatePicker;

    String group;

    int[] alarms = new int[5];
    String alarmsText;

    String memo;
    EditText editTextSetMemo;
    TextView textViewMemoLength;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_birthday);

        context = this;

        // 메인 화면으로 이동하는 버튼
        ImageButton buttonGoMain = (ImageButton) findViewById(R.id.buttonGoMain);
        buttonGoMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 저장
        ImageButton buttonSaveBirthday = (ImageButton) findViewById(R.id.buttonSaveBirthday);
        buttonSaveBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = editTextSetName.getText().toString();
                memo = editTextSetMemo.getText().toString();
                saveBirthday();
                MainActivity.categoryAdapter = new CategoryAdapter(MainActivity.getLoadBirthdayItems(), MainActivity.context);
                MainActivity.recyclerViewCategory.setAdapter(MainActivity.categoryAdapter);
//                CategoryAdapter.loadBirthdayItem = MainActivity.getLoadBirthdayItems();
//                MainActivity.categoryAdapter.notifyDataSetChanged();
                finish();
            }
        });

        // 1. 프로필 사진
        buttonSetPhoto = (ImageButton) findViewById(R.id.buttonSetPhoto);
        isSetPhoto = false;
        photoPath = "";
        buttonSetPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 갤러리 띄우기 - 선택한 사진의 처리는 onActivityResult()
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, 0);
            }
        });

        // 2. 이름
        editTextSetName = (EditText) findViewById(R.id.editTextSetName);

        // 3. 생일
        TextView textViewBirthday = (TextView) findViewById(R.id.textViewSetBirthday);
        LinearLayout linearLayoutSetBirthday = (LinearLayout) findViewById(R.id.linearLayoutSetBirthday);
        DatePicker datePickerBirthdaySetting = (DatePicker) findViewById(R.id.datePickerSetBirthday);
        isShowDatePicker = false; // DatePicker가 보이는 상태인지
        linearLayoutSetBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isShowDatePicker) { // DatePicker 닫기
                    datePickerBirthdaySetting.setVisibility(View.GONE);
                    isShowDatePicker = false;
                }else { // DatePicker 열기
                    datePickerBirthdaySetting.setVisibility(View.VISIBLE);
                    isShowDatePicker = true;
                }
            }
        });
        // DatePicker의 기본값으로 사용할 오늘 날짜 받아오기
        Calendar calendar = Calendar.getInstance();
        int todayYear = calendar.get(Calendar.YEAR);
        int todayMonth = calendar.get(Calendar.MONTH) + 1;
        int todayDay = calendar.get(Calendar.DAY_OF_MONTH);
        textViewBirthday.setText(todayYear + "년 " + todayMonth + "월 " + todayDay + "일"); // TextView에 기본값 먼저 보여주기
        String strTodayMonth = todayMonth < 10 ? ("0" + todayMonth) : Integer.toString(todayMonth);
        String strTodayDay = todayDay < 10 ? ("0" + todayDay) : Integer.toString(todayDay);
        birthday = todayYear + strTodayMonth + strTodayDay;
        // DatePicker 설정
        datePickerBirthdaySetting.init(todayYear, todayMonth - 1, todayDay, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                textViewBirthday.setText(year + "년 " + (monthOfYear + 1) + "월 " + dayOfMonth + "일");
                String strMonth = (monthOfYear + 1) < 10 ? ("0" + (monthOfYear + 1)) : Integer.toString(monthOfYear + 1);
                String strDay = dayOfMonth < 10 ? ("0" + dayOfMonth) : Integer.toString(dayOfMonth);
                birthday = year + strMonth + strDay;
            }
        });

        // 4. 그룹
        String[] groups = {"설정 안 함", "친구", "가족", "지인", "학교", "회사", "기타"};
        group = "설정 안 함";
        Spinner spinnerGroup = findViewById(R.id.spinnerSetGroup);
        ArrayAdapter<String> adapterGroup = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, groups);
        adapterGroup.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGroup.setAdapter(adapterGroup);
        spinnerGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                group = groups[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // 5. 알림
        TextView textViewAlarm = (TextView) findViewById(R.id.textViewSetAlarm);
        textViewAlarm.setText("설정 안함");
        Button buttonSetAlarm = (Button) findViewById(R.id.buttonSetAlarm);
        ArrayList<String> selectedAlarms = new ArrayList<String>();
        String[] alarms = {"2주", "1주", "3일", "1일", "당일"};
        // '변경' 버튼 누르면 알림 선택할 수 있는 체크 박스 목록의 다이얼로그 띄워 줌
        buttonSetAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddBirthdayActivity.this.alarms = new int[]{0, 0, 0, 0, 0}; // 각 알람의 선택 여부 저장하는 배열 초기화 (0-선택x 1-선택o), 각 인덱스는 alarms[]와 대치
                alarmsText = ""; // 선택 결과에 따라 TextView에 설정해줄 String 초기화
                AlertDialog.Builder builderDialogAlarm = new AlertDialog.Builder(AddBirthdayActivity.this);
                builderDialogAlarm.setMultiChoiceItems(R.array.alarms, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if(isChecked) {
                            AddBirthdayActivity.this.alarms[which] = 1;
                        }else if(AddBirthdayActivity.this.alarms[which] == 1) {
                            AddBirthdayActivity.this.alarms[which] = 0;
                        }
                    }
                });
                builderDialogAlarm.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // isCheckAlarms[]에 따라 alarmsForTextView 설정해주기
                        for(int i = 0; i < 5; i++) {
                            if(AddBirthdayActivity.this.alarms[i] == 1) {
                                alarmsText += ( alarms[i] + ", " );
                            }
                        }
                        if(alarmsText.equals("")) { // isCheckAlarms[]의 원소가 모두 0이면 alarmsForTextView가 초기화된 상태 그대로임
                            textViewAlarm.setText("설정 안함");
                        }else {
                            alarmsText = alarmsText.substring(0, alarmsText.length()-2); // 마지막 쉼표(,) 벗기기
                            textViewAlarm.setText(alarmsText);
                        }

                    }
                });
                builderDialogAlarm.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialogSetAlarm = builderDialogAlarm.create();
                alertDialogSetAlarm.show();
            }
        });

        // 6. 메모
        editTextSetMemo = (EditText) findViewById(R.id.editTextSetMemo);
        textViewMemoLength = (TextView) findViewById(R.id.textViewMemoLength); // 글자수를 나타내줄 TextView
        textViewMemoLength.setText("0/60");
        editTextSetMemo.addTextChangedListener(this); // 글자수 변화에 따라 실시간으로 textViewMemoLength를 변화시켜주기 위함 - TextWatcher 오버라이드 메소드
    }

    private void saveBirthday() {
        String sql = "insert into " + BirthdayDatabase.TABLE_BIRTHDAY +
                "(IMAGE, NAME, BIRTHDAY_DATE, GROUP_NAME, ALARM_14, ALARM_7, ALARM_3, ALARM_1, ALARM_0, MEMO) values(" +
                "'"+ photoUri + "', " +
                "'"+ name + "', " +
                "'"+ birthday + "', " +
                "'"+ group + "', " +
                "'"+ alarms[0] + "', " +
                "'"+ alarms[1] + "', " +
                "'"+ alarms[2] + "', " +
                "'"+ alarms[3] + "', " +
                "'"+ alarms[4] + "', " +
                "'"+ memo + "')";

        BirthdayDatabase database = BirthdayDatabase.getInstance(context);
        database.execSQL(sql);
    }

    // 갤러리에서 사진 선택한 결과 값을 이미지뷰에 세팅
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0){
            if(resultCode == RESULT_OK){
                if(Build.VERSION.SDK_INT < 19)
                    photoUri = data.getData();
                else {
                    photoUri = data.getData();
                    final int takeFlags = data.getFlags()
                            & (Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    try {
                        context.getContentResolver().takePersistableUriPermission(photoUri, takeFlags);
                    }catch (SecurityException e) {
                        e.printStackTrace();
                    }
                }
                ContentResolver resolver = getContentResolver();
                photoPath = createCopyAndReturnRealPath(this, photoUri);
                try{
                    InputStream inputStream = resolver.openInputStream(photoUri);
                    photoBitmap = BitmapFactory.decodeStream(inputStream);
                    buttonSetPhoto.setImageBitmap(photoBitmap);
                    inputStream.close();
                    isSetPhoto = true;
                }catch (Exception exception){
                    exception.printStackTrace();
                }
            }
        }
    }

    public static String createCopyAndReturnRealPath(Context context, Uri uri) {
        final ContentResolver contentResolver = context.getContentResolver();
        if (contentResolver == null)
            return null;
        // 파일 경로를 만듬
        String filePath = context.getApplicationInfo().dataDir + File.separator + System.currentTimeMillis();
        File file = new File(filePath);
        try {
            // 매개변수로 받은 uri를 통해 이미지에 필요한 데이터를 불러 들임
            InputStream inputStream = contentResolver.openInputStream(uri);
            if (inputStream == null)
                return null;
            // 이미지 데이터를 다시 내보내면서 file 객체에  만들었던 경로를 이용한다.
            OutputStream outputStream = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) > 0)
                outputStream.write(buf, 0, len);
            outputStream.close();
            inputStream.close();
        } catch (IOException ignore) {
                    return null;
        }
        return file.getAbsolutePath();
    }

    public byte[] getByteArrayFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String input = editTextSetMemo.getText().toString();
        textViewMemoLength.setText(input.length()+" /60");
    }

    @Override
    public void afterTextChanged(Editable s) {

    }






}
