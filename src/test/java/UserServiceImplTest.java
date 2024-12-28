import com.wallet.wallet_api.entities.*;
import com.wallet.wallet_api.entities.enums.CurrencyCode;
import com.wallet.wallet_api.entities.enums.EntryOperationType;
import com.wallet.wallet_api.entities.enums.EntryType;
import com.wallet.wallet_api.exceptions.CustomException;
import com.wallet.wallet_api.exceptions.InsufficientFundsException;
import com.wallet.wallet_api.repositories.UserRepository;
import com.wallet.wallet_api.entities.EntriesSummary;
import com.wallet.wallet_api.services.UserServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static com.wallet.wallet_api.services.UserServiceImpl.WALLET_NOT_FOUND;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.wallet.wallet_api.entities.dto.*;

class UserServiceImplTest {

    private static final Long USER_ID = 1L;
    private static final Long WALLET_ID = 2L;
    private static final Long RECEIVER_ID = 3L;
    private static final Long RECEIVER_WALLET_ID = 4L;
    private static final BigDecimal INITIAL_BALANCE = new BigDecimal("200.00");
    private static final BigDecimal DEPOSIT_AMOUNT = new BigDecimal("100.00");
    private static final BigDecimal WITHDRAWAL_AMOUNT = new BigDecimal("100.00");
    private static final BigDecimal TRANSFER_AMOUNT = new BigDecimal("50.00");
    private static final String EMAIL = "test.mail@com";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String DATE_OF_BIRTH = "2020-12-12";
    private static final String FIRST_NAME = "john";
    private static final String LAST_NAME = "doe";
    public static final String VAL_OF_100 = "100.00";

    public static final String VAL_OF_200 = "200.00";
    public static final String VAL_OF_300 = "300.00";
    public static final String VAL_OF_400 = "400.00";

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    private AutoCloseable closeable;

    @Mock
    private Wallet wallet;

    @Mock
    private Entry entry1;

    @Mock
    private Entry entry2;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }


    @Test
    void testCreateUser_Success() {
        User user = new User();
        user.setId(USER_ID);
        user.setEmail(EMAIL);
        user.setUsername(USERNAME);
        user.setPassword(PASSWORD);
        user.setDateOfBirth(DATE_OF_BIRTH);
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);

        UserDTO userDTO1 = new UserDTO();
        userDTO1.setEmail(user.getEmail());
        userDTO1.setUsername(user.getUsername());
        userDTO1.setPassword(user.getPassword());
        userDTO1.setDateOfBirth(DATE_OF_BIRTH);
        userDTO1.setFirstName(FIRST_NAME);
        userDTO1.setLastName(LAST_NAME);

        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.createUser(userDTO1);

        assertEquals(user.getEmail(), userDTO1.getEmail());
        assertEquals(user.getUsername(), userDTO1.getUsername());
        assertEquals(user.getPassword(), userDTO1.getPassword());
        assertEquals(user.getDateOfBirth(), userDTO1.getDateOfBirth());
        assertEquals(user.getFirstName(), userDTO1.getFirstName());
        assertEquals(user.getLastName(), userDTO1.getLastName());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testGetUserById_Success() {
        User user = new User();
        user.setId(USER_ID);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        User result = userService.getUserById(USER_ID);

        assertNotNull(result);
        assertEquals(USER_ID, result.getId());
        verify(userRepository, times(1)).findById(USER_ID);
    }

    @Test
    void testGetUserById_NotFound() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(USER_ID));

        assertTrue(exception.getMessage().contains("User not found with ID: " + USER_ID));
        verify(userRepository, times(1)).findById(USER_ID);
    }

    @Test
    void testGetAllUsers_Success() {
        List<User> users = new ArrayList<>();
        users.add(new User());
        users.add(new User());
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testAddWalletToUser_Success() {
        User user = new User();
        user.setId(USER_ID);
        Wallet wallet = new Wallet();
        wallet.setId(WALLET_ID);
        wallet.setBalance(BigDecimal.ZERO);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        Wallet result = userService.addWalletToUser(USER_ID, wallet);

        assertNotNull(result);
        assertEquals(WALLET_ID, result.getId());
        assertEquals(user, result.getUser());
        verify(userRepository, times(1)).findById(USER_ID);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testAddWalletToUser_NotFound() {
        Wallet wallet = new Wallet();
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> userService.addWalletToUser(USER_ID, wallet));

        assertTrue(exception.getMessage().contains("User not found with ID: " + USER_ID));
        verify(userRepository, times(1)).findById(USER_ID);
        verify(userRepository, never()).save(any(User.class));
    }


    @Test
    void testCreateEntry_DepositSuccess() {
        // Подготовка на данните
        User user = new User();
        user.setId(USER_ID);
        Wallet wallet = new Wallet();
        wallet.setId(WALLET_ID);
        wallet.setBalance(INITIAL_BALANCE);
        wallet.setCurrency(CurrencyCode.USD);
        user.getWallets().add(wallet);

        DepositWithdrawalDTO depositDTO = new DepositWithdrawalDTO();
        depositDTO.setUserId(USER_ID);
        depositDTO.setWalletId(WALLET_ID);
        depositDTO.setAmount(DEPOSIT_AMOUNT);
        depositDTO.setOperationType(EntryType.DEPOSIT.toString());

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        userService.depositOrWithdrawal(depositDTO);

        assertEquals(INITIAL_BALANCE.add(DEPOSIT_AMOUNT), wallet.getBalance());

        verify(userRepository, times(1)).findById(USER_ID);
        verify(userRepository, times(1)).save(user);
    }


    @Test
    void testCreateEntry_WithdrawalSuccess() {
        User user = new User();
        user.setId(USER_ID);
        Wallet wallet = new Wallet();
        wallet.setId(WALLET_ID);
        wallet.setBalance(INITIAL_BALANCE);
        wallet.setCurrency(CurrencyCode.BGN);
        user.getWallets().add(wallet);

        DepositWithdrawalDTO withdrawalDTO = new DepositWithdrawalDTO();
        withdrawalDTO.setUserId(USER_ID);
        withdrawalDTO.setWalletId(WALLET_ID);
        withdrawalDTO.setAmount(WITHDRAWAL_AMOUNT);
        withdrawalDTO.setOperationType(EntryType.WITHDRAWAL.toString());

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        userService.depositOrWithdrawal(withdrawalDTO);

        assertEquals(INITIAL_BALANCE.subtract(WITHDRAWAL_AMOUNT), wallet.getBalance());

        verify(userRepository, times(1)).findById(USER_ID);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testCreateEntry_WalletNotFound() {
        User user = new User();
        user.setId(USER_ID);

        DepositWithdrawalDTO depositDTO = new DepositWithdrawalDTO();
        depositDTO.setUserId(USER_ID);
        depositDTO.setWalletId(WALLET_ID);
        depositDTO.setOperationType(EntryOperationType.DEBIT.toString());
        depositDTO.setAmount(BigDecimal.valueOf(200));

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        Throwable thrown = catchThrowable(() -> userService.depositOrWithdrawal(depositDTO));

        assertThat(thrown).isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(WALLET_NOT_FOUND + depositDTO.getWalletId());
        verify(userRepository, never()).save(user);
    }


    @Test
    void testCreateTransfer_Success() {
        User user = new User();
        user.setId(USER_ID);
        Wallet wallet = new Wallet();
        wallet.setId(WALLET_ID);
        wallet.setCurrency(CurrencyCode.USD);
        wallet.setBalance(INITIAL_BALANCE);
        user.getWallets().add(wallet);

        User receiver = new User();
        receiver.setId(RECEIVER_ID);
        Wallet receiverWallet = new Wallet();
        receiverWallet.setId(RECEIVER_WALLET_ID);
        receiverWallet.setCurrency(CurrencyCode.USD);
        receiverWallet.setBalance(new BigDecimal("100.00"));
        receiver.getWallets().add(receiverWallet);

        TransferDTO transferDTO = new TransferDTO();
        transferDTO.setUserId(USER_ID);
        transferDTO.setWalletId(WALLET_ID);
        transferDTO.setReceiverId(RECEIVER_ID);
        transferDTO.setReceiverWalletId(RECEIVER_WALLET_ID);
        transferDTO.setAmount(TRANSFER_AMOUNT);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(userRepository.findById(RECEIVER_ID)).thenReturn(Optional.of(receiver));

        userService.transfer(transferDTO);

        assertEquals(INITIAL_BALANCE.subtract(TRANSFER_AMOUNT), wallet.getBalance());
        assertEquals(new BigDecimal("150.00"), receiverWallet.getBalance());

        verify(userRepository, times(1)).findById(USER_ID);
        verify(userRepository, times(1)).findById(RECEIVER_ID);
        verify(userRepository, times(1)).save(user);
        verify(userRepository, times(1)).save(receiver);
    }

    @Test
    void testCreateTransfer_SuccessDifferentCurrencies() {
        User user = new User();
        user.setId(USER_ID);
        Wallet wallet = new Wallet();
        wallet.setId(WALLET_ID);
        wallet.setCurrency(CurrencyCode.BGN);
        wallet.setBalance(INITIAL_BALANCE);
        user.getWallets().add(wallet);

        User receiver = new User();
        receiver.setId(RECEIVER_ID);
        Wallet receiverWallet = new Wallet();
        receiverWallet.setId(RECEIVER_WALLET_ID);
        receiverWallet.setCurrency(CurrencyCode.USD);
        receiverWallet.setBalance(new BigDecimal(VAL_OF_100));
        receiver.getWallets().add(receiverWallet);

        TransferDTO transferDTO = new TransferDTO();
        transferDTO.setUserId(USER_ID);
        transferDTO.setWalletId(WALLET_ID);
        transferDTO.setReceiverId(RECEIVER_ID);
        transferDTO.setReceiverWalletId(RECEIVER_WALLET_ID);
        transferDTO.setAmount(TRANSFER_AMOUNT);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(userRepository.findById(RECEIVER_ID)).thenReturn(Optional.of(receiver));

        userService.transfer(transferDTO);

        assertEquals(INITIAL_BALANCE.subtract(TRANSFER_AMOUNT), wallet.getBalance());
        assertEquals(new BigDecimal("128.5000"), receiverWallet.getBalance());

        verify(userRepository, times(1)).findById(USER_ID);
        verify(userRepository, times(1)).findById(RECEIVER_ID);
        verify(userRepository, times(1)).save(user);
        verify(userRepository, times(1)).save(receiver);
    }


    @Test
    void testCreateTransfer_InsufficientFunds() {
        User user = new User();
        user.setId(USER_ID);
        Wallet wallet = new Wallet();
        wallet.setId(WALLET_ID);
        wallet.setCurrency(CurrencyCode.USD);
        wallet.setBalance(new BigDecimal("50.00"));
        user.getWallets().add(wallet);

        User receiver = new User();
        receiver.setId(RECEIVER_ID);
        Wallet receiverWallet = new Wallet();
        receiverWallet.setId(RECEIVER_WALLET_ID);
        receiverWallet.setCurrency(CurrencyCode.USD);
        receiverWallet.setBalance(new BigDecimal(VAL_OF_100));
        receiver.getWallets().add(receiverWallet);

        TransferDTO transferDTO = new TransferDTO();
        transferDTO.setUserId(USER_ID);
        transferDTO.setWalletId(WALLET_ID);
        transferDTO.setReceiverId(RECEIVER_ID);
        transferDTO.setReceiverWalletId(RECEIVER_WALLET_ID);
        transferDTO.setAmount(new BigDecimal(VAL_OF_100));

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(userRepository.findById(RECEIVER_ID)).thenReturn(Optional.of(receiver));

        Throwable thrown = catchThrowable(() -> userService.transfer(transferDTO));

        assertThat(thrown).isInstanceOf(InsufficientFundsException.class)
                .hasMessageContaining("The withdrawal amount exceeds the current balance");

        verify(userRepository, times(1)).findById(USER_ID);
        verify(userRepository, never()).save(any(User.class));
    }


    @Test
    void testGetWallets_Success() {
        Long userId = USER_ID;
        Wallet wallet1 = new Wallet();
        wallet1.setId(1L);
        Wallet wallet2 = new Wallet();
        wallet2.setId(2L);

        User user = new User();
        user.setId(userId);
        user.setWallets(Arrays.asList(wallet1, wallet2));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        List<Wallet> wallets = userService.getWallets(userId);

        assertNotNull(wallets);
        assertEquals(2, wallets.size());
        assertEquals(1L, wallets.get(0).getId());
        assertEquals(2L, wallets.get(1).getId());

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testGetWallets_UserNotFound() {
        Long userId = USER_ID;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> userService.getWallets(userId));
        assertEquals("User not found with ID: " + userId, exception.getMessage());

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testGetWallets_UnexpectedException() {
        Long userId = USER_ID;
        when(userRepository.findById(userId)).thenThrow(new RuntimeException("Unexpected error"));

        CustomException exception = assertThrows(CustomException.class, () -> userService.getWallets(userId));
        assertEquals("Failed to fetch wallets for user ID " + userId, exception.getMessage());

        verify(userRepository, times(1)).findById(userId);
    }


    @Test
    void testGetWalletByUserIdAndWalletId_Success() {
        Long userId = USER_ID;
        Long walletId = WALLET_ID;

        Wallet wallet = new Wallet();
        wallet.setId(walletId);

        List<Wallet> wallets = List.of(wallet);

        Mockito.when(userRepository.findWalletsByUserId(userId)).thenReturn(wallets);

        Wallet result = userService.getWalletByUserIdAndWalletId(userId, walletId);

        assertNotNull(result);
        assertEquals(walletId, result.getId());
        Mockito.verify(userRepository, Mockito.times(1)).findWalletsByUserId(userId);
    }

    @Test
    void testGetWalletByUserIdAndWalletId_NoWalletsFound() {
        Long userId = USER_ID;

        Mockito.when(userRepository.findWalletsByUserId(userId)).thenReturn(Collections.emptyList());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> userService.getWalletByUserIdAndWalletId(userId, WALLET_ID));

        assertEquals("No wallets found for userId " + userId, exception.getMessage());
        Mockito.verify(userRepository, Mockito.times(1)).findWalletsByUserId(userId);
    }

    @Test
    void testGetWalletByUserIdAndWalletId_WalletNotFound() {
        Long userId = USER_ID;
        Long walletId = WALLET_ID;

        Wallet wallet = new Wallet();
        wallet.setId(20L);

        List<Wallet> wallets = List.of(wallet);

        Mockito.when(userRepository.findWalletsByUserId(userId)).thenReturn(wallets);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> userService.getWalletByUserIdAndWalletId(userId, walletId));

        assertEquals(String.format(WALLET_NOT_FOUND, userId, walletId), exception.getMessage());
        Mockito.verify(userRepository, Mockito.times(1)).findWalletsByUserId(userId);
    }

    @Test
    void testGetWalletByUserIdAndWalletId_UnexpectedException() {
        Long userId = USER_ID;

        Mockito.when(userRepository.findWalletsByUserId(userId)).thenThrow(new RuntimeException("Database error"));

        CustomException exception = assertThrows(CustomException.class,
                () -> userService.getWalletByUserIdAndWalletId(userId, WALLET_ID));

        assertEquals("An unexpected error occurred while fetching the wallet", exception.getMessage());
        Mockito.verify(userRepository, Mockito.times(1)).findWalletsByUserId(userId);
    }

    ///
    @Test
    public void testCalculateEntrySummary_ValidData() {
        LocalDateTime startDate = LocalDateTime.of(2024, 12, 1, 0, 0, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 12, 31, 23, 59, 59, 999999);

        when(wallet.getEntries()).thenReturn(Arrays.asList(entry1, entry2));

        when(entry1.getDate()).thenReturn(LocalDateTime.of(2024, 12, 5, 10, 0, 0, 0));
        when(entry1.getOperationType()).thenReturn(EntryOperationType.DEBIT);
        when(entry1.getAmount()).thenReturn(BigDecimal.valueOf(100));

        when(entry2.getDate()).thenReturn(LocalDateTime.of(2024, 12, 10, 12, 0, 0, 0));
        when(entry2.getOperationType()).thenReturn(EntryOperationType.CREDIT);
        when(entry2.getAmount()).thenReturn(BigDecimal.valueOf(50));

        EntriesSummary summary = userService.calculateEntrySummary(wallet, startDate, endDate);

        assertNotNull(summary);
        assertEquals(BigDecimal.valueOf(100), summary.getTotalDebit());
        assertEquals(BigDecimal.valueOf(50), summary.getTotalCredit());
    }

    @Test
    public void testCalculateEntrySummary_NullPointerException() {
        when(wallet.getEntries()).thenReturn(null);

        CustomException exception = assertThrows(CustomException.class,
                () -> userService.calculateEntrySummary(wallet, LocalDateTime.now(), LocalDateTime.now()));

        assertEquals("Error calculating entry summary: null value encountered", exception.getMessage());
    }

    @Test
    public void testCalculateEntrySummary_UnexpectedError() {
        when(wallet.getEntries()).thenThrow(new RuntimeException("Unexpected error"));

        CustomException exception = assertThrows(CustomException.class,
                () -> userService.calculateEntrySummary(wallet, LocalDateTime.now(), LocalDateTime.now()));

        assertEquals("Failed to calculate entry summary", exception.getMessage());
    }


    @Test
    void testCreateEntry_InvalidAmount() {
        User user = new User();
        user.setId(USER_ID);
        Wallet wallet = new Wallet();
        wallet.setId(WALLET_ID);
        wallet.setCurrency(CurrencyCode.USD);
        wallet.setBalance(INITIAL_BALANCE);
        wallet.setCurrency(CurrencyCode.USD);
        user.getWallets().add(wallet);

        DepositWithdrawalDTO depositWithdrawalDTO = new DepositWithdrawalDTO();
        depositWithdrawalDTO.setUserId(USER_ID);
        depositWithdrawalDTO.setWalletId(WALLET_ID);
        depositWithdrawalDTO.setAmount(new BigDecimal("-50.00")); // Невалидна сума
        depositWithdrawalDTO.setOperationType("DEPOSIT");

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        Throwable thrown = catchThrowable(() -> userService.depositOrWithdrawal(depositWithdrawalDTO));

        assertThat(thrown).isInstanceOf(CustomException.class)
                .hasMessageContaining("Amount must be positive");

        verify(userRepository, times(1)).findById(USER_ID);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testCreateTransfer_SameWallet() {
        User user = new User();
        user.setId(USER_ID);
        Wallet wallet = new Wallet();
        wallet.setId(WALLET_ID);
        wallet.setCurrency(CurrencyCode.USD);
        wallet.setBalance(INITIAL_BALANCE);
        wallet.setCurrency(CurrencyCode.USD);
        user.getWallets().add(wallet);

        TransferDTO transferDTO = new TransferDTO();
        transferDTO.setUserId(USER_ID);
        transferDTO.setWalletId(WALLET_ID);
        transferDTO.setReceiverId(USER_ID);
        transferDTO.setReceiverWalletId(WALLET_ID);
        transferDTO.setAmount(TRANSFER_AMOUNT);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        Throwable thrown = catchThrowable(() -> userService.transfer(transferDTO));

        assertThat(thrown).isInstanceOf(CustomException.class)
                .hasMessageContaining("Cannot transfer to the same wallet!");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testAddWalletToUser_DuplicateWallet() {
        User user = new User();
        user.setId(USER_ID);
        Wallet wallet = new Wallet();
        wallet.setId(WALLET_ID);
        wallet.setUser(user);
        user.getWallets().add(wallet);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        Throwable thrown = catchThrowable(() -> userService.addWalletToUser(USER_ID, wallet));

        assertThat(thrown).isInstanceOf(CustomException.class)
                .hasMessageContaining("Failed to add wallet to user with ID " + user.getId());
        verify(userRepository, never()).save(user);
    }

    @Test
    void testGetEntriesForCSV_WithEntriesInDateRange() {
        Wallet wallet = new Wallet();
        List<Entry> entries = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        entries.add(createEntry(now.minusDays(1), new BigDecimal(VAL_OF_100)));
        entries.add(createEntry(now, new BigDecimal(VAL_OF_200)));
        entries.add(createEntry(now.plusDays(1), new BigDecimal(VAL_OF_300)));

        wallet.setEntries(entries);

        LocalDateTime startDate = now.minusDays(2);
        LocalDateTime endDate = now.plusDays(2);

        List<Entry> result = userService.getEntriesForCSV(wallet, startDate, endDate);

        assertThat(result.size() == 3).isTrue();
    }

    @Test
    void testGetEntriesForCSV_WithEntriesOutOfDateRange() {
        Wallet wallet = new Wallet();
        List<Entry> entries = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        entries.add(createEntry(now.minusDays(3), new BigDecimal(VAL_OF_100)));
        entries.add(createEntry(now.minusDays(2), new BigDecimal(VAL_OF_200)));
        entries.add(createEntry(now.plusDays(3), new BigDecimal(VAL_OF_300)));

        wallet.setEntries(entries);

        LocalDateTime startDate = now.minusDays(1);
        LocalDateTime endDate = now.plusDays(1);

        List<Entry> result = userService.getEntriesForCSV(wallet, startDate, endDate);

        assertThat(result.size() == 0).isTrue();
    }

    @Test
    void testGetEntriesForCSV_WithMixedEntries() {
        Wallet wallet = new Wallet();
        List<Entry> entries = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        entries.add(createEntry(now.minusDays(3), new BigDecimal(VAL_OF_100)));
        entries.add(createEntry(now.minusDays(1), new BigDecimal(VAL_OF_200)));
        entries.add(createEntry(now.plusDays(1), new BigDecimal(VAL_OF_300)));
        entries.add(createEntry(now.plusDays(3), new BigDecimal(VAL_OF_400)));

        wallet.setEntries(entries);

        LocalDateTime startDate = now.minusDays(2);
        LocalDateTime endDate = now.plusDays(2);

        List<Entry> result = userService.getEntriesForCSV(wallet, startDate, endDate);

        assertThat(result.size() == 2).isTrue();
    }

    private Entry createEntry(LocalDateTime date, BigDecimal amount) {
        Entry entry = new Entry();
        entry.setDate(date);
        entry.setAmount(amount);
        return entry;
    }
}
