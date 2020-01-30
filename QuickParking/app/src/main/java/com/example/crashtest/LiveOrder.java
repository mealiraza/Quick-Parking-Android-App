package com.example.crashtest;


import android.os.Parcel;
import android.os.Parcelable;

public class LiveOrder implements Parcelable {
    private String orderUserName;
    private String orderPhoneNumber;
    private Double lat;
    private Double lan;
    private String orderStatus;
    private String driverUID;
    private String driverLat;
    private String driverLng;
    private String userUid;


    public LiveOrder() {


    }


    protected LiveOrder(Parcel in) {
        orderUserName = in.readString();
        orderPhoneNumber = in.readString();
        if (in.readByte() == 0) {
            lat = null;
        } else {
            lat = in.readDouble();
        }
        if (in.readByte() == 0) {
            lan = null;
        } else {
            lan = in.readDouble();
        }
        orderStatus = in.readString();
        driverUID = in.readString();
        driverLat = in.readString();
        driverLng = in.readString();
        userUid = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(orderUserName);
        dest.writeString(orderPhoneNumber);
        if (lat == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(lat);
        }
        if (lan == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(lan);
        }
        dest.writeString(orderStatus);
        dest.writeString(driverUID);
        dest.writeString(driverLat);
        dest.writeString(driverLng);
        dest.writeString(userUid);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<LiveOrder> CREATOR = new Creator<LiveOrder>() {
        @Override
        public LiveOrder createFromParcel(Parcel in) {
            return new LiveOrder(in);
        }

        @Override
        public LiveOrder[] newArray(int size) {
            return new LiveOrder[size];
        }
    };

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }
    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLan() {
        return lan;
    }

    public void setLan(Double lan) {
        this.lan = lan;
    }

    public String getOrderUserName() {
        return orderUserName;
    }

    public void setOrderUserName(String orderUserName) {
        this.orderUserName = orderUserName;
    }

    public String getOrderPhoneNumber() {
        return orderPhoneNumber;
    }

    public void setOrderPhoneNumber(String orderPhoneNumber) {
        this.orderPhoneNumber = orderPhoneNumber;
    }

    public String getDriverUID() {
        return driverUID;
    }

    public void setDriverUID(String driverUID) {
        this.driverUID = driverUID;
    }

    public String getDriverLat() {
        return driverLat;
    }

    public void setDriverLat(String driverLat) {
        this.driverLat = driverLat;
    }

    public String getDriverLng() {
        return driverLng;
    }

    public void setDriverLng(String driverLng) {
        this.driverLng = driverLng;
    }




}