package com.wallet.wallet_api.services;

import com.wallet.wallet_api.entities.*;
import com.wallet.wallet_api.entities.dto.DepositWithdrawalDTO;
import com.wallet.wallet_api.entities.dto.TransferDTO;
import com.wallet.wallet_api.entities.dto.UserDTO;
import com.wallet.wallet_api.entities.enums.EntryOperationType;
import com.wallet.wallet_api.entities.enums.EntryType;
import com.wallet.wallet_api.exceptions.CustomException;
import com.wallet.wallet_api.exceptions.InsufficientFundsException;
import com.wallet.wallet_api.repositories.UserRepository;
import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private static final String USER_NOT_FOUND = "User not found with ID: ";
    private static final String FAILED_TO_FETCH_USER = "Failed to fetch user with ID ";
    private static final String FAILED_TO_FETCH_USERS = "Failed to fetch users";
    private static final String FAILED_TO_CREATE_USER = "Failed to create user";
    private static final String FAILED_TO_FETCH_WALLETS = "Failed to fetch wallets for user ID ";
    private static final String FAILED_TO_ADD_WALLET = "Failed to add wallet to user with ID ";
    private static final String NO_WALLETS_FOUND = "No wallets found for userId ";
    public static final String WALLET_NOT_FOUND = "Wallet not found for userId %d and walletId %d";
    private static final String ERROR_CALCULATING_ENTRY_SUMMARY = "Error calculating entry summary: null value encountered";
    private static final String FAILED_TO_CALCULATE_ENTRY_SUMMARY = "Failed to calculate entry summary";
    private static final String WALLET_CANNOT_BE_NULL = "Wallet cannot be null";
    private static final String START_END_DATE_CANNOT_BE_NULL = "Start date and end date cannot be null";
    private static final String START_DATE_AFTER_END_DATE = "Start date cannot be after end date";
    private static final String FAILED_TO_RETRIEVE_ENTRIES = "Failed to retrieve entries for CSV";
    private static final String FAILED_TO_CREATE_ENTRY = "Failed to create entry";
    private static final String CANNOT_TRANSFER_SAME_WALLET = "Cannot transfer to the same wallet!";
    private static final String INVALID_ENTRY_AMOUNT = "Invalid entry amount";
    private static final String RECEIVER_NOT_FOUND = "Receiver not found";
    private static final String RECEIVER_WALLET_NOT_FOUND = "Receiver's wallet not found";
    private static final String WITHDRAWAL_AMOUNT_EXCEEDS_BALANCE = "The withdrawal amount exceeds the current balance";
    private static final String UNEXPECTED_ERROR_TRANSFER = "An unexpected error occurred during transfer";
    private static final String AMOUNT_MUST_BE_POSITIVE = "Amount must be positive";
    private static final String FAILED_TO_HANDLE_DEPOSIT = "Failed to handle deposit";
    private static final String FAILED_TO_HANDLE_WITHDRAWAL = "Failed to handle withdrawal";
    private static final BigDecimal ZERO_AMOUNT = BigDecimal.ZERO;

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id the ID of the user to retrieve
     * @return the user with the specified ID
     * @throws ResourceNotFoundException if the user is not found
     * @throws CustomException if an unexpected error occurs
     */
    @Override
    public User getUserById(Long id) {
        try {
            return userRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND + id));
        } catch (ResourceNotFoundException ex) {
            logger.warn("User not found with ID {}: {}", id, ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            logger.error("Error fetching user by ID {}: {}", id, ex.getMessage(), ex);
            throw new CustomException(FAILED_TO_FETCH_USER + id);
        }
    }

    /**
     * Retrieves all users.
     *
     * @return a list of all users
     * @throws CustomException if an unexpected error occurs
     */
    @Override
    public List<User> getAllUsers() {
        try {
            return userRepository.findAll();
        } catch (Exception ex) {
            logger.error("Error fetching all users: {}", ex.getMessage(), ex);
            throw new CustomException(FAILED_TO_FETCH_USERS);
        }
    }

    /**
     * Creates a new user.
     *
     * @param userDTO the data transfer object containing user details
     * @throws CustomException if an unexpected error occurs
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createUser(UserDTO userDTO) {
        try {
            User user = new User();
            user.setWallets(userDTO.getWallets());
            user.setEmail(userDTO.getEmail());
            user.setUsername(userDTO.getUsername());
            user.setFirstName(userDTO.getFirstName());
            user.setLastName(userDTO.getLastName());
            user.setPassword(userDTO.getPassword());
            user.setDateOfBirth(userDTO.getDateOfBirth());

            userRepository.save(user);
        } catch (Exception ex) {
            logger.error("Error creating user: {}", ex.getMessage(), ex);
            throw new CustomException(FAILED_TO_CREATE_USER);
        }
    }

    /**
     * Retrieves the wallets of a user by their ID.
     *
     * @param id the ID of the user
     * @return a list of wallets belonging to the user
     * @throws ResourceNotFoundException if the user is not found
     * @throws CustomException if an unexpected error occurs
     */
    @Override
    public List<Wallet> getWallets(Long id) {
        try {
            User user = getUserById(id);
            return user.getWallets();
        } catch (ResourceNotFoundException ex) {
            logger.warn("User with ID {} not found when fetching wallets: {}", id, ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            logger.error("Error fetching wallets for user ID {}: {}", id, ex.getMessage(), ex);
            throw new CustomException(FAILED_TO_FETCH_WALLETS + id);
        }
    }

    /**
     * Adds a wallet to a user.
     *
     * @param userId the ID of the user
     * @param wallet the wallet to add
     * @return the added wallet
     * @throws ResourceNotFoundException if the user is not found
     * @throws CustomException if an unexpected error occurs
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Wallet addWalletToUser(Long userId, Wallet wallet) {
        try {
            User user = getUserById(userId);

            boolean walletExists = user.getWallets().stream()
                    .anyMatch(existingWallet -> existingWallet.getId().equals(wallet.getId()));
            if (walletExists) {
                throw new CustomException(FAILED_TO_ADD_WALLET + userId);
            }

            wallet.setUser(user);
            user.getWallets().add(wallet);

            userRepository.save(user);

            return wallet;
        } catch (ResourceNotFoundException ex) {
            logger.warn("User with ID {} not found: {}", userId, ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            logger.error("Error adding wallet to user with ID {}: {}", userId, ex.getMessage(), ex);
            throw new CustomException(FAILED_TO_ADD_WALLET + userId);
        }
    }

    /**
     * Retrieves a wallet by user ID and wallet ID.
     *
     * @param userId the ID of the user
     * @param walletId the ID of the wallet
     * @return the wallet with the specified IDs
     * @throws ResourceNotFoundException if the wallet is not found
     * @throws CustomException if an unexpected error occurs
     */
    @Override
    public Wallet getWalletByUserIdAndWalletId(Long userId, Long walletId) {
        try {
            List<Wallet> wallets = userRepository.findWalletsByUserId(userId);

            if (wallets == null || wallets.isEmpty()) {
                throw new ResourceNotFoundException(NO_WALLETS_FOUND + userId);
            }

            return wallets.stream()
                    .filter(wallet -> wallet.getId().equals(walletId))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException(
                            String.format(WALLET_NOT_FOUND, userId, walletId)
                    ));
        } catch (ResourceNotFoundException ex) {
            logger.error("Resource not found: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            logger.error("Unexpected error occurred while fetching wallet for userId {} and walletId {}", userId, walletId, ex);
            throw new CustomException("An unexpected error occurred while fetching the wallet");
        }
    }

    /**
     * Calculates the entry summary for a wallet within a date range.
     *
     * @param wallet the wallet to calculate the summary for
     * @param startDate the start date of the range
     * @param endDate the end date of the range
     * @return the entry summary
     * @throws CustomException if an unexpected error occurs
     */
    @Override
    public EntriesSummary calculateEntrySummary(Wallet wallet, LocalDateTime startDate, LocalDateTime endDate) {
        try {
            List<Entry> filteredEntries = wallet.getEntries().stream()
                    .filter(entry -> !entry.getDate().isBefore(startDate) && !entry.getDate().isAfter(endDate))
                    .collect(Collectors.toList());

            BigDecimal totalDebit = filteredEntries.stream()
                    .filter(entry -> entry.getOperationType() == EntryOperationType.DEBIT)
                    .map(Entry::getAmount)
                    .reduce(ZERO_AMOUNT, BigDecimal::add);

            BigDecimal totalCredit = filteredEntries.stream()
                    .filter(entry -> entry.getOperationType() == EntryOperationType.CREDIT)
                    .map(Entry::getAmount)
                    .reduce(ZERO_AMOUNT, BigDecimal::add);

            return new EntriesSummary(totalDebit, totalCredit, filteredEntries);
        } catch (NullPointerException ex) {
            logger.error("NullPointerException occurred while calculating entry summary: {}", ex.getMessage(), ex);
            throw new CustomException(ERROR_CALCULATING_ENTRY_SUMMARY);
        } catch (Exception ex) {
            logger.error("Unexpected error occurred while calculating entry summary: {}", ex.getMessage(), ex);
            throw new CustomException(FAILED_TO_CALCULATE_ENTRY_SUMMARY);
        }
    }

    /**
     * Retrieves entries for a wallet within a date range for CSV export.
     *
     * @param wallet the wallet to retrieve entries for
     * @param startDate the start date of the range
     * @param endDate the end date of the range
     * @return a list of entries
     * @throws CustomException if an unexpected error occurs
     */
    @Override
    public List<Entry> getEntriesForCSV(Wallet wallet, LocalDateTime startDate, LocalDateTime endDate) {
        try {
            if (wallet == null) {
                throw new CustomException(WALLET_CANNOT_BE_NULL);
            }
            if (startDate == null || endDate == null) {
                throw new CustomException(START_END_DATE_CANNOT_BE_NULL);
            }
            if (startDate.isAfter(endDate)) {
                throw new CustomException(START_DATE_AFTER_END_DATE);
            }

            return wallet.getEntries().stream()
                    .filter(entry -> !entry.getDate().isBefore(startDate) && !entry.getDate().isAfter(endDate))
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            logger.error("Error retrieving entries for CSV: {}", ex.getMessage(), ex);
            throw new CustomException(FAILED_TO_RETRIEVE_ENTRIES);
        }
    }

    /**
     * Handles deposit or withdrawal operations.
     *
     * @param depositWithdrawalDTO the data transfer object containing deposit or withdrawal details
     * @throws ResourceNotFoundException if the user or wallet is not found
     * @throws CustomException if an unexpected error occurs
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void depositOrWithdrawal(DepositWithdrawalDTO depositWithdrawalDTO) {
        try {
            Entry entry = new Entry();
            User user = getUserById(depositWithdrawalDTO.getUserId());
            Wallet wallet = user.getWallets().stream()
                    .filter(w -> w.getId().equals(depositWithdrawalDTO.getWalletId()))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException(WALLET_NOT_FOUND + depositWithdrawalDTO.getWalletId()));

            entry.setAmount(depositWithdrawalDTO.getAmount());
            entry.setFromCurrency(wallet.getCurrency().toString());
            entry.setToCurrency(wallet.getCurrency().toString());
            entry.setType(EntryType.valueOf(depositWithdrawalDTO.getOperationType()));
            entry.setDate(LocalDateTime.now());
            entry.setWallet(wallet);
            if (depositWithdrawalDTO.getOperationType().equals(EntryType.DEPOSIT.toString())) {
                entry.setOperationType(EntryOperationType.DEBIT);
            } else if (depositWithdrawalDTO.getOperationType().equals(EntryType.WITHDRAWAL.toString())) {
                entry.setOperationType(EntryOperationType.CREDIT);
            }
            verifyAmountOfEntryIsPositive(entry);

            if (entry.getType() == EntryType.DEPOSIT) {
                entry.setOperationType(EntryOperationType.DEBIT);
                handleDeposit(wallet, entry);
            } else if (entry.getType() == EntryType.WITHDRAWAL) {
                entry.setOperationType(EntryOperationType.CREDIT);
                handleWithdrawal(wallet, entry);
            }
            userRepository.save(user);
        } catch (ResourceNotFoundException ex) {
            logger.warn("Resource not found: {}", ex.getMessage());
            throw ex;
        } catch (CustomException ex) {
            logger.warn("Business rule violation: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            logger.error("Error creating entry for user ID {} and wallet ID {}: {}",
                    depositWithdrawalDTO.getUserId(), depositWithdrawalDTO.getWalletId(), ex.getMessage(), ex);
            throw new CustomException(FAILED_TO_CREATE_ENTRY);
        }
    }

    /**
     * Transfers an amount from one wallet to another.
     *
     * @param transferDTO the data transfer object containing transfer details
     * @throws CustomException if a business rule violation occurs
     * @throws InsufficientFundsException if there are insufficient funds for the transfer
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void transfer(TransferDTO transferDTO) {
        if (transferDTO.getWalletId().equals(transferDTO.getReceiverWalletId())) {
            throw new CustomException(CANNOT_TRANSFER_SAME_WALLET);
        }
        try {
            if (transferDTO.getAmount().compareTo(ZERO_AMOUNT) <= 0) {
                throw new CustomException(INVALID_ENTRY_AMOUNT);
            }

            User user = userRepository.findById(transferDTO.getUserId())
                    .orElseThrow(() -> new CustomException(RECEIVER_NOT_FOUND));
            Wallet wallet = user.getWallets().stream()
                    .filter(w -> w.getId().equals(transferDTO.getWalletId()))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException(WALLET_NOT_FOUND + transferDTO.getWalletId()));

            if (isEntryAmountBiggerThanTheCurrentBalance1(transferDTO.getAmount(), wallet.getBalance())) {
                throw new InsufficientFundsException(WITHDRAWAL_AMOUNT_EXCEEDS_BALANCE);
            }
            User receiver = userRepository.findById(transferDTO.getReceiverId())
                    .orElseThrow(() -> new CustomException(RECEIVER_NOT_FOUND));
            Wallet receiverWallet = receiver.getWallets().stream()
                    .filter(w -> w.getId().equals(transferDTO.getReceiverWalletId()))
                    .findFirst()
                    .orElseThrow(() -> new CustomException(RECEIVER_WALLET_NOT_FOUND));

            BigDecimal amountFrom = transferDTO.getAmount();

            BigDecimal amountTo = CurrencyConverter.convert(amountFrom,
                    wallet.getCurrency().toString(),
                    receiverWallet.getCurrency().toString());

            Entry entry = new Entry();
            entry.setAmount(amountFrom);
            entry.setDate(LocalDateTime.now());
            entry.setType(EntryType.TRANSFER);
            entry.setOperationType(EntryOperationType.CREDIT);
            entry.setWallet(wallet);
            entry.setFromCurrency(wallet.getCurrency().toString());
            entry.setToCurrency(receiverWallet.getCurrency().toString());

            Entry receivingEntry = new Entry();
            receivingEntry.setAmount(amountTo);
            receivingEntry.setDate(LocalDateTime.now());
            receivingEntry.setType(EntryType.TRANSFER);
            receivingEntry.setOperationType(EntryOperationType.DEBIT);
            receivingEntry.setWallet(receiverWallet);
            receivingEntry.setFromCurrency(wallet.getCurrency().toString());
            receivingEntry.setToCurrency(receiverWallet.getCurrency().toString());

            wallet.getEntries().add(entry);
            receiverWallet.getEntries().add(receivingEntry);
            wallet.setBalance(wallet.getBalance().subtract(amountFrom));
            receiverWallet.setBalance(receiverWallet.getBalance().add(amountTo));

            userRepository.save(user);
            userRepository.save(receiver);
        } catch (EntityNotFoundException | InsufficientFundsException ex) {
            logger.error("Error processing transfer: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            logger.error("Unexpected error occurred during transfer: {}", ex.getMessage());
            throw new CustomException(UNEXPECTED_ERROR_TRANSFER);
        }
    }

    private static void verifyAmountOfEntryIsPositive(Entry entry) {
        if (entry.getAmount().compareTo(ZERO_AMOUNT) <= 0) {
            throw new CustomException(AMOUNT_MUST_BE_POSITIVE);
        }
    }

    private void handleDeposit(Wallet wallet, Entry entry) {
        try {
            BigDecimal updatedBalance = wallet.getBalance().add(entry.getAmount());
            updateWallet(wallet, entry, updatedBalance);
        } catch (Exception ex) {
            logger.error("Error handling deposit: {}", ex.getMessage(), ex);
            throw new CustomException(FAILED_TO_HANDLE_DEPOSIT);
        }
    }

    private void handleWithdrawal(Wallet wallet, Entry entry) {
        try {
            if (isEntryAmountBiggerThanTheCurrentBalance1(entry.getAmount(), wallet.getBalance())) {
                throw new CustomException(WITHDRAWAL_AMOUNT_EXCEEDS_BALANCE);
            }
            BigDecimal updatedBalance = wallet.getBalance().subtract(entry.getAmount());
            updateWallet(wallet, entry, updatedBalance);
        } catch (CustomException ex) {
            logger.warn("Withdrawal rule violation: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            logger.error("Error handling withdrawal: {}", ex.getMessage(), ex);
            throw new CustomException(FAILED_TO_HANDLE_WITHDRAWAL);
        }
    }

    private static void updateWallet(Wallet wallet, Entry entry, BigDecimal updatedBalance) {
        wallet.setBalance(updatedBalance);
        entry.setWallet(wallet);
        wallet.getEntries().add(entry);
    }

    private static boolean isEntryAmountBiggerThanTheCurrentBalance1(BigDecimal entryAmount, BigDecimal currentBalance) {
        return currentBalance.compareTo(entryAmount) < 0;
    }

}
