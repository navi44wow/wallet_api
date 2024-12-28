package com.wallet.wallet_api.entities.dto;

import com.wallet.wallet_api.entities.Role;
import com.wallet.wallet_api.entities.Wallet;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class ResponseUserDTO {

    private Long id;

    private String username;

    private String email;

    private String firstName;

    private String lastName;

    private String dateOfBirth;

    private Set<Role> roles = new HashSet<>();

    private List<Wallet> wallets = new ArrayList<>();

}
