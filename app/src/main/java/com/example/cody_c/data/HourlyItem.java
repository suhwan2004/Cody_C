package com.example.cody_c.data;

import android.os.Parcel;
import android.os.Parcelable;

public class HourlyItem implements Parcelable {
    private String Days;
    private int Weather_photo;
    private String Temp_hourly;

    public HourlyItem() {
    }

    public HourlyItem(String days, int weather_photo, String temp_hourly) {
        Days = days;
        Weather_photo = weather_photo;
        Temp_hourly = temp_hourly;
    }
    //getter

    protected HourlyItem(Parcel in) {
        Days = in.readString();
        Weather_photo = in.readInt();
        Temp_hourly = in.readString();
    }

    public static final Creator<HourlyItem> CREATOR = new Creator<HourlyItem>() {
        @Override
        public HourlyItem createFromParcel(Parcel in) {
            return new HourlyItem(in);
        }

        @Override
        public HourlyItem[] newArray(int size) {
            return new HourlyItem[size];
        }
    };

    public String getDays() {
        return Days;
    }

    public int getWeather_photo() {
        return Weather_photo;
    }

    public String getTemp_hourly() {
        return Temp_hourly;
    }

    //setter

    public void setDays(String days) {
        Days = days;
    }

    public void setWeather_photo(int weather_photo) {
        Weather_photo = weather_photo;
    }

    public void setTemp_hourly(String temp_hourly) {
        Temp_hourly = temp_hourly;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Days);
        dest.writeInt(Weather_photo);
        dest.writeString(Temp_hourly);
    }
}
