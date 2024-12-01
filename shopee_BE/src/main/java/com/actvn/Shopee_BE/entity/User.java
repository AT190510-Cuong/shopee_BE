package com.actvn.Shopee_BE.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    private String userId;
    @Column(name = "username")
    private String userName;
    @Column(name = "email")
    private String email;
    @Column(name = "password")
    private String password;

    @Setter
    @Getter
    @ManyToMany(cascade =
            {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
    joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles  = new HashSet<>();

    @Getter
    @Setter
    @ManyToMany(
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            }
    )
    @JoinTable(name = "user_address",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "user_address")
    )
    private List<Address> addresses = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "user",
    cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    },orphanRemoval = true
    )
    private Set<Product> products;


    @ToString.Exclude
    @OneToOne(mappedBy = "user",
    cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    }, orphanRemoval = true
    )
    private Cart cart;
    public User(String userName, String email, String password) {
        this.userName = userName;
        this.email = email;
        this.password = password;
    }


}
