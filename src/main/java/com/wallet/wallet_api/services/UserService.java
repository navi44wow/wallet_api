package com.wallet.wallet_api.services;

import com.wallet.wallet_api.entities.EntriesSummary;
import com.wallet.wallet_api.entities.Entry;
import com.wallet.wallet_api.entities.User;
import com.wallet.wallet_api.entities.Wallet;
import com.wallet.wallet_api.entities.dto.DepositWithdrawalDTO;
import com.wallet.wallet_api.entities.dto.TransferDTO;
import com.wallet.wallet_api.entities.dto.UserDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface UserService {

    void createUser(UserDTO userDTO);

    User getUserById(Long id);

    List<User> getAllUsers();

    List<Wallet> getWallets(Long id);

    Wallet addWalletToUser(Long userId, Wallet wallet);

    Wallet getWalletByUserIdAndWalletId(Long userId, Long walletId);

    EntriesSummary calculateEntrySummary(Wallet wallet, LocalDateTime startDate, LocalDateTime endDate);

    List<Entry> getEntriesForCSV(Wallet wallet, LocalDateTime startDate, LocalDateTime endDate);

    void transfer(TransferDTO transferDTO);

    void depositOrWithdrawal(DepositWithdrawalDTO depositWithdrawalDTO);
}
