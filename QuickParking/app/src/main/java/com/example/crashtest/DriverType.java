package com.example.crashtest;

import android.os.Parcel;
import android.os.Parcelable;

public class DriverType implements Parcelable {

    private String firstName;
    private String lastName;
    private String CNIC;
    private String licenseNumber;
    private String dateofBirth;
    private String licenseType;
    private String gender;
    private String address;
    private String phoneNumber;
    private String profileLink;
    private String email;
    private String uid;
    private String parkingName;

    public String getParkingName() {
        return parkingName;
    }

    public void setParkingName(String parkingName) {
        this.parkingName = parkingName;
    }

    protected DriverType(Parcel in) {
        firstName = in.readString();
        lastName = in.readString();
        CNIC = in.readString();
        licenseNumber = in.readString();
        dateofBirth = in.readString();
        licenseType = in.readString();
        gender = in.readString();
        address = in.readString();
        phoneNumber = in.readString();
        profileLink = in.readString();
        email = in.readString();
        uid = in.readString();
    }

    public static final Creator<DriverType> CREATOR = new Creator<DriverType>() {
        @Override
        public DriverType createFromParcel(Parcel in) {
            return new DriverType(in);
        }

        @Override
        public DriverType[] newArray(int size) {
            return new DriverType[size];
        }
    };

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfileLink() {
        return profileLink;
    }

    public void setProfileLink(String profileLink) {
        this.profileLink = profileLink;
    }

    public DriverType() {

    }

    public DriverType(String firstName, String lastName, String CNIC, String licenseNumber, String dateofBirth, String licenseType, String gender, String address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.CNIC = CNIC;
        this.licenseNumber = licenseNumber;
        this.dateofBirth = dateofBirth;
        this.licenseType = licenseType;
        this.gender = gender;
        this.address = address;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCNIC() {
        return CNIC;
    }

    public void setCNIC(String CNIC) {
        this.CNIC = CNIC;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getDateofBirth() {
        return dateofBirth;
    }

    public void setDateofBirth(String dateofBirth) {
        this.dateofBirth = dateofBirth;
    }

    public String getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(String licenseType) {
        this.licenseType = licenseType;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(CNIC);
        dest.writeString(licenseNumber);
        dest.writeString(dateofBirth);
        dest.writeString(licenseType);
        dest.writeString(gender);
        dest.writeString(address);
        dest.writeString(phoneNumber);
        dest.writeString(profileLink);
        dest.writeString(email);
        dest.writeString(uid);
    }
}
