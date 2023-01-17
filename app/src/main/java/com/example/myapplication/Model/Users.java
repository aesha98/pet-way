package com.example.myapplication.Model;

public class Users {


    private String name;
    private String phone;
    private String password;
    private String image;
    private String email;
    private String fName;
    private String lname;
    private String pNumber;
    private String birth;
    private String gender;

    public Users(){}


    public Users(String name, String phone, String password, String image, String email) {
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.image = image;
        this.email = email;
    }

    public Users(String fName, String lname, String pNumber, String birth, String gender, String image) {
        this.fName = fName;
        this.lname = lname;
        this.pNumber = pNumber;
        this.birth = birth;
        this.gender = gender;
        this.image = image;
    }

    public String getfName() {
        return fName;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getpNumber() {
        return pNumber;
    }

    public void setpNumber(String pNumber) {
        this.pNumber = pNumber;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
