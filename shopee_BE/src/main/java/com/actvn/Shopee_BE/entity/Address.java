package com.actvn.Shopee_BE.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "addresses")
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    @Id
    @Column(name = "address_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String addressId;
    @Column(name = "building_name")
    private String buildingName;
    private String city;
    private String country;
    @Column(name = "pin_code")
    private String pinCode;
    private String state;
    private String street;

    @ToString.Exclude
    @ManyToMany(mappedBy = "addresses")
    private List<User> uesrs = new ArrayList<>();

    public Address(String addressId, String buildingName, String city, String country, String pinCode, String state, String street) {
        this.addressId = addressId;
        this.buildingName = buildingName;
        this.city = city;
        this.country = country;
        this.pinCode = pinCode;
        this.state = state;
        this.street = street;
    }
}
