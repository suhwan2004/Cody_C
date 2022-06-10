package com.example.cody_c.data;


import android.os.Parcel;
import android.os.Parcelable;

public class DailyItem implements Parcelable {
    private String Days;
    private int Weather_photo;
    private String Low_temp;
    private String High_temp;

    public DailyItem(String days, String low_temp, String high_temp, int weather_photo) {
        Days = days;
        Weather_photo = weather_photo;
        Low_temp = low_temp;
        High_temp = high_temp;
    }
    //getter

    protected DailyItem(Parcel in) {
        Days = in.readString();
        Weather_photo = in.readInt();
        Low_temp = in.readString();
        High_temp = in.readString();
    }

    public static final Creator<DailyItem> CREATOR = new Creator<DailyItem>() {
        @Override
        public DailyItem createFromParcel(Parcel in) {
            return new DailyItem(in);
        }

        @Override
        public DailyItem[] newArray(int size) {
            return new DailyItem[size];
        }
    };

    public String getDays() {
        return Days;
    }

    public int getWeather_photo() {
        return Weather_photo;
    }

    public String getLow_temp() {
        return Low_temp;
    }

    public String getHigh_temp() {
        return High_temp;
    }

    //Setter

    public void setDays(String days) {
        Days = days;
    }

    public void setWeather_photo(int weather_photo) {
        Weather_photo= weather_photo;
    }

    public void setLow_temp(String low_temp) {
        Low_temp = low_temp;
    }

    public void setHigh_temp(String high_temp) {
        High_temp = high_temp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Days);
        dest.writeInt(Weather_photo);
        dest.writeString(Low_temp);
        dest.writeString(High_temp);
    }
}

