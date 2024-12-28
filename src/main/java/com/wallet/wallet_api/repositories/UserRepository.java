package com.wallet.wallet_api.repositories;

import com.wallet.wallet_api.entities.User;
import com.wallet.wallet_api.entities.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u.wallets FROM User u WHERE u.id = :userId")
    List<Wallet> findWalletsByUserId(@Param("userId") Long userId);
}

