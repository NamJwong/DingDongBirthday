package com.example.dingdongbirthday;

import android.net.Uri;

import java.util.Calendar;

public class BirthdayItem implements Comparable<BirthdayItem> {
    private int id;
    private Uri uriPhoto;
    private String name;
    private String birthday;
    private String group;
    private int[] alarms;
    private String memo;
    private int bookmark;

    public BirthdayItem(int id, Uri uriPhoto, String name, String birthday, String group, int[] alarms, String memo, int bookmark) {
        this.id = id;
        this.uriPhoto = uriPhoto;
        this.name = name;
        this.birthday = birthday;
        this.group = group;
        this.alarms = alarms;
        this.memo = memo;
        this.bookmark = bookmark;
    }

    @Override
    public int compareTo(BirthdayItem birthdayItem) {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int thisBirthdayYear = currentYear;
        int thisBirthdayMonth = Integer.parseInt(this.getBirthday().substring(4,6));
        if(thisBirthdayMonth < currentMonth)
            thisBirthdayYear = currentYear + 1;
        int thisBirthdayDay = Integer.parseInt(this.getBirthday().substring(6,8));
        int birthdayYear = currentYear;
        int birthdayMonth = Integer.parseInt(birthdayItem.getBirthday().substring(4,6));
        if(birthdayMonth < currentMonth)
            birthdayYear = currentYear + 1;
        int birthdayDay = Integer.parseInt(birthdayItem.getBirthday().substring(6,8));

        if((thisBirthdayMonth == birthdayMonth) && (thisBirthdayDay == birthdayDay))
            return 0;
        if(thisBirthdayYear < birthdayYear)
            return -1;
        if((thisBirthdayYear == birthdayYear) && (thisBirthdayMonth < birthdayMonth))
            return -1;
        if((thisBirthdayMonth == birthdayMonth) && (thisBirthdayDay < birthdayDay))
            return -1;
        return 1;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Uri getUriPhoto() {
        return uriPhoto;
    }

    public void setUriPhoto(Uri uriPhoto) {
        this.uriPhoto = uriPhoto;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public int[] getAlarms() {
        return alarms;
    }

    public void setAlarms(int[] alarms) {
        this.alarms = alarms;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public int getBookmark() {
        return bookmark;
    }

    public void setBookmark(int bookmark) {
        this.bookmark = bookmark;
    }
}
