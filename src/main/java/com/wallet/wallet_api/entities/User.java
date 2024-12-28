package com.wallet.wallet_api.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
public class
User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NonNull
    private Long id;

    @NonNull
    @Column(unique = true)
    private String username;

    @NonNull
    @Column(unique = true)
    private String password;

    @NonNull
    @Column(unique = true)
    private String email;

    @NonNull
    @Column(name="first_name")
    private String firstName;

    @NonNull
    @Column(name="last_name")
    private String lastName;

    @NonNull
    @Column(name="date_of_birth")
    private String dateOfBirth;


    @ManyToMany
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Wallet> wallets = new ArrayList<>();

}
