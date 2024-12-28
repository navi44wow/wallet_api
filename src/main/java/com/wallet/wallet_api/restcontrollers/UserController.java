package com.wallet.wallet_api.restcontrollers;

import com.opencsv.CSVWriter;
import com.wallet.wallet_api.entities.Entry;
import com.wallet.wallet_api.entities.User;
import com.wallet.wallet_api.entities.Wallet;
import com.wallet.wallet_api.entities.dto.DepositWithdrawalDTO;
import com.wallet.wallet_api.entities.dto.ResponseUserDTO;
import com.wallet.wallet_api.entities.dto.TransferDTO;
import com.wallet.wallet_api.entities.dto.UserDTO;
import com.wallet.wallet_api.exceptions.CustomException;
import com.wallet.wallet_api.exceptions.ResourceNotFoundException;
import com.wallet.wallet_api.entities.EntriesSummary;
import com.wallet.wallet_api.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    public static final String AN_UNEXPECTED_ERROR_OCCURRED = "An unexpected error occurred";

    public static final String T_00_00_00 = "T00:00:00";
    public static final String T_23_59_59 = "T23:59:59";

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id the ID of the user to retrieve
     * @return the response entity containing the user details
     * @throws ResourceNotFoundException if the user is not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResponseUserDTO> getUserById(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);

            ResponseUserDTO userDTO = mapUserToResponseUserDTO(user);

            return new ResponseEntity<>(userDTO, HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Retrieves all users.
     *
     * @return the response entity containing the list of users
     */
    @GetMapping
    public ResponseEntity<List<ResponseUserDTO>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<ResponseUserDTO> responseUserDTOS = users.stream()
                .map(this::mapUserToResponseUserDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(responseUserDTOS, HttpStatus.OK);
    }

    /**
     * Creates a new user.
     *
     * @param userDTO the data transfer object containing user details
     * @return the response entity indicating the result of the operation
     * @throws IllegalArgumentException if the user data is invalid
     */
    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody UserDTO userDTO) {
        try {
            userService.createUser(userDTO);
            return ResponseEntity.ok("User created successfully");
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Retrieves the wallets of a user by their ID.
     *
     * @param userId the ID of the user
     * @return the response entity containing the list of wallets
     * @throws ResourceNotFoundException if the user or wallets are not found
     */
    @GetMapping("/{userId}/wallets")
    public ResponseEntity<List<Wallet>> getWalletsByUserId(@PathVariable Long userId) {
        try {
            List<Wallet> wallets = userService.getWallets(userId);
            return ResponseEntity.ok(wallets);
        } catch (ResourceNotFoundException ex) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Retrieves a wallet by user ID and wallet ID.
     *
     * @param userId   the ID of the user
     * @param walletId the ID of the wallet
     * @return the response entity containing the wallet details
     * @throws ResourceNotFoundException if the user or wallet is not found
     */
    @GetMapping("/{userId}/wallets/{walletId}")
    public ResponseEntity<Wallet> getWalletByUserIdAndWalletId(@PathVariable Long userId, @PathVariable Long walletId) {
        try {
            Wallet wallet = userService.getWalletByUserIdAndWalletId(userId, walletId);
            return ResponseEntity.ok(wallet);
        } catch (ResourceNotFoundException ex) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Retrieves entries of a wallet by user ID and wallet ID.
     *
     * @param userId   the ID of the user
     * @param walletId the ID of the wallet
     * @return the response entity containing the list of entries
     * @throws ResourceNotFoundException if the user or wallet is not found
     */
    @GetMapping("/{userId}/wallets/{walletId}/entries")
    public ResponseEntity<List<Entry>> getEntriesByUserIdAndWalletId(@PathVariable Long userId, @PathVariable Long walletId) {
        try {
            Wallet wallet = userService.getWalletByUserIdAndWalletId(userId, walletId);
            List<Entry> entries = wallet.getEntries();
            return ResponseEntity.ok(entries);
        } catch (ResourceNotFoundException ex) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Adds a wallet to a user.
     *
     * @param userId the ID of the user
     * @param wallet the wallet to add
     * @return the response entity containing the added wallet
     * @throws ResourceNotFoundException if the user is not found
     */
    @PostMapping("/{userId}/wallets")
    public ResponseEntity<Wallet> addWalletToUser(@PathVariable Long userId, @RequestBody Wallet wallet) {
        try {
            Wallet savedWallet = userService.addWalletToUser(userId, wallet);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedWallet);
        } catch (ResourceNotFoundException ex) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Retrieves the entry summary for a wallet within a date range.
     *
     * @param userId    the ID of the user
     * @param walletId  the ID of the wallet
     * @param startDate the start date of the range
     * @param endDate   the end date of the range
     * @return the response entity containing the entry summary
     * @throws ResourceNotFoundException if the user or wallet is not found
     * @throws IllegalArgumentException  if the date range is invalid
     */
    @GetMapping("/{userId}/wallets/{walletId}/entries-summary")
    public ResponseEntity<EntriesSummary> getEntriesSummary(
            @PathVariable Long userId,
            @PathVariable Long walletId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            LocalDateTime y = convertStringToDate(startDate, T_00_00_00);
            LocalDateTime b = convertStringToDate(endDate, T_23_59_59);

            Wallet wallet = userService.getWalletByUserIdAndWalletId(userId, walletId);
            EntriesSummary summary = userService.calculateEntrySummary(wallet, y, b);
            return ResponseEntity.ok(summary);
        } catch (ResourceNotFoundException ex) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Retrieves entries for a wallet within a date range as a CSV file.
     *
     * @param userId    the ID of the user
     * @param walletId  the ID of the wallet
     * @param startDate the start date of the range
     * @param endDate   the end date of the range
     * @return the response entity containing the CSV data
     * @throws IOException               if an I/O error occurs during CSV generation
     * @throws ResourceNotFoundException if the user or wallet is not found
     * @throws CustomException           if an unexpected error occurs
     */
    @GetMapping("/entries/csv")
    public ResponseEntity<String> getEntriesAsCsv(@RequestParam Long userId,
                                                  @RequestParam Long walletId,
                                                  @RequestParam String startDate,
                                                  @RequestParam String endDate) {
        try {
            LocalDateTime start = convertStringToDate(startDate, T_00_00_00);
            LocalDateTime end = convertStringToDate(endDate, T_23_59_59);

            Wallet wallet = userService.getWalletByUserIdAndWalletId(userId, walletId);
            List<Entry> entries = userService.getEntriesForCSV(wallet, start, end);

            StringWriter writer = new StringWriter();
            CSVWriter csvWriter = new CSVWriter(writer);
            csvWriter.writeNext(new String[]{"ID", "Amount", "Type", "Operation Type", "Date", "Wallet ID", "From Currency", "To Currency"});

            for (Entry entry : entries) {
                csvWriter.writeNext(new String[]{
                        entry.getId().toString(),
                        entry.getAmount().toString(),
                        entry.getType().toString(),
                        entry.getOperationType().toString(),
                        entry.getDate().toString(),
                        entry.getWallet().getId().toString(),
                        entry.getFromCurrency(),
                        entry.getToCurrency()
                });
            }

            csvWriter.close();

            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=entries.csv");
            headers.setContentType(MediaType.TEXT_PLAIN);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(writer.toString());
        } catch (IOException ex) {
            throw new CustomException("Failed to generate CSV file");
        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new CustomException(AN_UNEXPECTED_ERROR_OCCURRED);
        }
    }

    /**
     * Transfers an amount from one wallet to another.
     *
     * @param transferDTO the data transfer object containing transfer details
     * @return the response entity indicating the result of the operation
     * @throws IllegalArgumentException if the transfer data is invalid
     * @throws CustomException          if an unexpected error occurs
     */
    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@RequestBody TransferDTO transferDTO) {
        try {
            userService.transfer(transferDTO);
            return ResponseEntity.ok("Transfer successful");
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return new ResponseEntity<>(AN_UNEXPECTED_ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Handles deposit or withdrawal operations.
     *
     * @param depositWithdrawalDTO the data transfer object containing deposit or withdrawal details
     * @return the response entity indicating the result of the operation
     * @throws IllegalArgumentException if the deposit or withdrawal data is invalid
     * @throws CustomException          if an unexpected error occurs
     */
    @PostMapping("/entry")
    public ResponseEntity<String> depositOrWithdrawal(@RequestBody DepositWithdrawalDTO depositWithdrawalDTO) {
        try {
            userService.depositOrWithdrawal(depositWithdrawalDTO);
            return ResponseEntity.ok("Entry is successful");
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return new ResponseEntity<>(AN_UNEXPECTED_ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseUserDTO mapUserToResponseUserDTO(User user) {
        ResponseUserDTO userDTO = new ResponseUserDTO();
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setUsername(user.getUsername());
        userDTO.setDateOfBirth(user.getDateOfBirth());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setWallets(user.getWallets());
        userDTO.setRoles(user.getRoles());
        return userDTO;
    }

    private static LocalDateTime convertStringToDate(String startDate, String exactTime) {
        String y = startDate + exactTime;
        return LocalDateTime.parse(y);
    }
}