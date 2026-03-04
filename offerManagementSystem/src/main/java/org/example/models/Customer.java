package org.example.models;

import org.example.enums.Gender;
import java.util.Date;

public class Customer {
    private String id;
    private int age;
    private Date dob;
    private Gender gender;

    public Customer(String id, int age, Date dob, Gender gender) {
        this.id = id;
        this.age = age;
        this.dob = dob;
        this.gender = gender;
    }

    public String getId() { return id; }
    public int getAge() { return age; }
    public Date getDob() { return dob; }
    public Gender getGender() { return gender; }
}
