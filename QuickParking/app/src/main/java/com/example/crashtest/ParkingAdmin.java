package com.example.crashtest;

import android.os.Parcel;
import android.os.Parcelable;

public class ParkingAdmin implements Parcelable {

    private String parkingName;
    private String parkingCapacity;
    private String parkingArea;
    private String parkingProfile;
    private String parkingEmail;
    private String phoneNumber;
    private String cnic;
    private String uid;
    private String address;

    public ParkingAdmin() {
    }

    protected ParkingAdmin(Parcel in) {
        parkingName = in.readString();
        parkingCapacity = in.readString();
        parkingArea = in.readString();
        parkingProfile = in.readString();
        parkingEmail = in.readString();
        phoneNumber = in.readString();
        cnic = in.readString();
        uid = in.readString();
        address = in.readString();
    }

    public static final Creator<ParkingAdmin> CREATOR = new Creator<ParkingAdmin>() {
        @Override
        public ParkingAdmin createFromParcel(Parcel in) {
            return new ParkingAdmin(in);
        }

        @Override
        public ParkingAdmin[] newArray(int size) {
            return new ParkingAdmin[size];
        }
    };

    public String getCnic() {
        return cnic;
    }

    public void setCnic(String cnic) {
        this.cnic = cnic;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getParkingName() {
        return parkingName;
    }

    public void setParkingName(String parkingName) {
        this.parkingName = parkingName;
    }

    public String getParkingCapacity() {
        return parkingCapacity;
    }

    public void setParkingCapacity(String parkingCapacity) {
        this.parkingCapacity = parkingCapacity;
    }

    public String getParkingArea() {
        return parkingArea;
    }

    public void setParkingArea(String parkingArea) {
        this.parkingArea = parkingArea;
    }

    public String getParkingProfile() {
        return parkingProfile;
    }

    public void setParkingProfile(String parkingProfile) {
        this.parkingProfile = parkingProfile;
    }

    public String getParkingEmail() {
        return parkingEmail;
    }

    public void setParkingEmail(String parkingEmail) {
        this.parkingEmail = parkingEmail;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(parkingName);
        dest.writeString(parkingCapacity);
        dest.writeString(parkingArea);
        dest.writeString(parkingProfile);
        dest.writeString(parkingEmail);
        dest.writeString(phoneNumber);
        dest.writeString(cnic);
        dest.writeString(uid);
        dest.writeString(address);
    }
}
