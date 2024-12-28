-- Insert dummy data into role
INSERT INTO role (name) VALUES ('ADMIN'), ('USER');

-- Insert dummy data into user
INSERT INTO user (username, password, email, first_name, last_name, date_of_birth) VALUES
('john_doe', 'password123', 'john.doe@example.com', 'John', 'Doe', '1990-01-01'),
('jane_doe', 'password456', 'jane.doe@example.com', 'Jane', 'Doe', '1992-01-01'),
    ('alice_smith', 'password789', 'alice.smith@example.com', 'Alice', 'Smith', '1985-05-15'),
    ('bob_jones', 'password101', 'bob.jones@example.com', 'Bob', 'Jones', '1995-09-09'),
    ('carol_white', 'password202', 'carol.white@example.com', 'Carol', 'White', '1980-12-20'),
    ('dave_black', 'password303', 'dave.black@example.com', 'Dave', 'Black', '1993-03-03');


-- Link users to roles
INSERT INTO user_roles (user_id, role_id) VALUES
(1, 1),
(2, 1),
(3, 1),
(4, 1),
(5, 1),
(6, 2);


-- Insert dummy data into wallet with currencies
INSERT INTO wallet (balance, user_id, currency) VALUES
(1000000.00, 1, 'BGN'), -- Wallet for john_doe
(500000.00, 2, 'EUR'),  -- Wallet for jane_doe
(150000.00, 3, 'USD'),  -- Wallet for alice_smith
(250000.00, 4, 'GBP'),  -- Wallet for bob_jones
(1200000.00, 5, 'EUR'), -- Wallet for carol_white
(800000.00, 6, 'USD');  -- Wallet for dave_black


-- Insert entries into the entry table with currencies

INSERT INTO entry (amount, type, operation_type, date, wallet_id, from_currency, to_currency)
VALUES
(9814.06, 'DEPOSIT', 'DEBIT', '2020-04-20 16:20:00', 3, 'USD', 'USD'),
(6832.37, 'DEPOSIT', 'DEBIT', '2022-06-10 14:30:00', 1,'BGN','BGN'),
(7399.15, 'DEPOSIT', 'DEBIT', '2020-06-10 14:30:00', 1,'BGN','BGN'),
(4208.59, 'DEPOSIT', 'DEBIT', '2022-09-05 12:10:00', 4, 'GBP', 'GBP'),
(1298.88, 'DEPOSIT', 'DEBIT', '2023-12-01 14:30:00', 1,'BGN','BGN'),
(452.29, 'DEPOSIT', 'DEBIT', '2020-12-01 14:30:00', 1,'BGN','BGN'),
(2063.77, 'DEPOSIT', 'DEBIT', '2020-02-25 08:55:00', 5,'EUR','EUR'),
(138123, 'DEPOSIT', 'DEBIT', '2021-04-20 16:20:00', 3, 'USD', 'USD'),
(6874.67, 'DEPOSIT', 'DEBIT', '2020-01-30 18:40:00', 6, 'USD', 'USD'),
(6580.37, 'DEPOSIT', 'DEBIT', '2020-02-25 08:55:00', 5,'EUR','EUR'),
(1257.94, 'DEPOSIT', 'DEBIT', '2020-05-15 09:45:00', 2,'EUR','EUR'),
(3097.80, 'DEPOSIT', 'DEBIT', '2020-09-05 12:10:00', 4, 'GBP', 'GBP'),
(5024.95, 'DEPOSIT', 'DEBIT', '2024-08-25 08:55:00', 5,'EUR','EUR'),
(8462.53, 'DEPOSIT', 'DEBIT', '2020-05-15 09:45:00', 2,'EUR','EUR'),
(2467.58, 'DEPOSIT', 'DEBIT', '2024-03-05 12:10:00', 4, 'GBP', 'GBP'),
(869.13, 'DEPOSIT', 'DEBIT', '2023-09-05 12:10:00', 4, 'GBP', 'GBP'),
(7907.98, 'DEPOSIT', 'DEBIT', '2024-07-30 18:40:00', 6, 'USD', 'USD'),
(7157.52, 'DEPOSIT', 'DEBIT', '2022-12-01 14:30:00', 1,'BGN','BGN'),
(6214.18, 'DEPOSIT', 'DEBIT', '2023-02-25 08:55:00', 5,'EUR','EUR'),
(2654.78, 'DEPOSIT', 'DEBIT', '2023-04-20 16:20:00', 3, 'USD', 'USD'),
(1598.19, 'WITHDRAWAL', 'CREDIT', '2022-03-05 12:10:00', 4, 'GBP', 'GBP'),
(6791.32, 'WITHDRAWAL', 'CREDIT', '2024-02-25 08:55:00', 5,'EUR','EUR'),
(6058.52, 'WITHDRAWAL', 'CREDIT', '2024-11-15 09:45:00', 2,'EUR','EUR'),
(90.63, 'WITHDRAWAL', 'CREDIT', '2024-12-01 14:30:00', 1,'BGN','BGN'),
(4273.88, 'WITHDRAWAL', 'CREDIT', '2020-03-05 12:10:00', 4, 'GBP', 'GBP'),
(6575.39, 'WITHDRAWAL', 'CREDIT', '2024-05-15 09:45:00', 2,'EUR','EUR'),
(4059.27, 'WITHDRAWAL', 'CREDIT', '2021-03-05 12:10:00', 4, 'GBP', 'GBP'),
(8539.89, 'WITHDRAWAL', 'CREDIT', '2022-10-20 16:20:00', 3, 'USD', 'USD'),
(2604789, 'WITHDRAWAL', 'CREDIT', '2023-06-10 14:30:00', 1,'BGN','BGN'),
(1769.18, 'WITHDRAWAL', 'CREDIT', '2020-06-10 14:30:00', 1,'BGN','BGN'),
(2668.40, 'WITHDRAWAL', 'CREDIT', '2022-02-25 08:55:00', 5,'EUR','EUR'),
(54.97, 'WITHDRAWAL', 'CREDIT', '2024-10-20 16:20:00', 3, 'USD', 'USD'),
(4185.41, 'WITHDRAWAL', 'CREDIT', '2020-08-25 08:55:00', 5,'EUR','EUR'),
(9839.58, 'WITHDRAWAL', 'CREDIT', '2022-05-15 09:45:00', 2,'EUR','EUR'),
(6632.79, 'WITHDRAWAL', 'CREDIT', '2021-02-25 08:55:00', 5,'EUR','EUR'),
(3240.60, 'WITHDRAWAL', 'CREDIT', '2023-01-30 18:40:00', 6, 'USD', 'USD'),
(5417.47, 'WITHDRAWAL', 'CREDIT', '2023-10-20 16:20:00', 3, 'USD', 'USD'),
(9954.91, 'WITHDRAWAL', 'CREDIT', '2021-05-15 09:45:00', 2,'EUR','EUR'),
(1372.33, 'WITHDRAWAL', 'CREDIT', '2021-10-20 16:20:00', 3, 'USD', 'USD'),
(2652.85, 'WITHDRAWAL', 'CREDIT', '2023-03-05 12:10:00', 4, 'GBP', 'GBP'),
(924.37, 'TRANSFER', 'DEBIT', '2021-08-25 08:55:00', 5,'EUR','EUR'),
(3648.27, 'TRANSFER', 'DEBIT', '2021-07-30 18:40:00', 6, 'USD','BGN'),
(7517.28, 'TRANSFER', 'DEBIT', '2022-07-30 18:40:00', 6, 'USD', 'GBP'),
(300321, 'TRANSFER', 'DEBIT', '2021-06-10 14:30:00', 1,'BGN','EUR'),
(2858.91, 'TRANSFER', 'DEBIT', '2021-09-05 12:10:00', 4, 'GBP', 'GBP'),
(1994.59, 'TRANSFER', 'DEBIT', '2022-08-25 08:55:00', 5,'EUR','BGN'),
(4813.20, 'TRANSFER', 'DEBIT', '2021-05-15 09:45:00', 2,'EUR', 'USD'),
(6353.65, 'TRANSFER', 'DEBIT', '2024-09-05 12:10:00', 4, 'GBP','EUR'),
(5769.48, 'TRANSFER', 'DEBIT', '2023-07-30 18:40:00', 6, 'USD', 'USD'),
(9968.44, 'TRANSFER', 'DEBIT', '2023-05-15 09:45:00', 2,'EUR','BGN'),
(2439.75, 'TRANSFER', 'DEBIT', '2023-11-15 09:45:00', 2,'EUR', 'USD'),
(1621738, 'TRANSFER', 'DEBIT', '2021-01-30 18:40:00', 6, 'USD', 'USD'),
(5498.21, 'TRANSFER', 'DEBIT', '2020-07-30 18:40:00', 6, 'USD','BGN'),
(1783.97, 'TRANSFER', 'DEBIT', '2021-03-05 12:10:00', 4, 'GBP','EUR'),
(6939.88, 'TRANSFER', 'DEBIT', '2022-04-20 16:20:00', 3, 'USD', 'GBP'),
(8506.31, 'TRANSFER', 'DEBIT', '2020-09-05 12:10:00', 4, 'GBP', 'GBP'),
(51.17, 'TRANSFER', 'DEBIT', '2021-06-10 14:30:00', 1,'BGN', 'GBP'),
(24139, 'TRANSFER', 'DEBIT', '2020-03-05 12:10:00', 4, 'GBP','EUR'),
(3418.86, 'TRANSFER', 'DEBIT', '2022-11-15 09:45:00', 2,'EUR','EUR'),
(9117.89, 'TRANSFER', 'DEBIT', '2020-08-25 08:55:00', 5,'EUR', 'GBP'),
(7323.92, 'TRANSFER', 'CREDIT', '2021-08-25 08:55:00', 5,'EUR', 'USD'),
(5660.76, 'TRANSFER', 'CREDIT', '2021-01-30 18:40:00', 6, 'USD', 'USD'),
(448.18, 'TRANSFER', 'CREDIT', '2021-04-20 16:20:00', 3, 'USD','EUR'),
(5492.51, 'TRANSFER', 'CREDIT', '2021-12-01 14:30:00', 1,'BGN', 'GBP'),
(3246.95, 'TRANSFER', 'CREDIT', '2020-11-15 09:45:00', 2,'EUR','EUR'),
(8266.95, 'TRANSFER', 'CREDIT', '2024-06-10 14:30:00', 1,'BGN', 'USD'),
(4196.88, 'TRANSFER', 'CREDIT', '2021-02-25 08:55:00', 5,'EUR','EUR'),
(7988.74, 'TRANSFER', 'CREDIT', '2021-07-30 18:40:00', 6, 'USD','BGN'),
(7910.14, 'TRANSFER', 'CREDIT', '2020-07-30 18:40:00', 6, 'USD','EUR'),
(8853.37, 'TRANSFER', 'CREDIT', '2023-08-25 08:55:00', 5,'EUR', 'USD'),
(2713.63, 'TRANSFER', 'CREDIT', '2020-10-20 16:20:00', 3, 'USD','EUR'),
(3583.49, 'TRANSFER', 'CREDIT', '2024-01-30 18:40:00', 6, 'USD', 'GBP'),
(6531.57, 'TRANSFER', 'CREDIT', '2020-04-20 16:20:00', 3, 'USD', 'GBP'),
(4025.29, 'TRANSFER', 'CREDIT', '2024-04-20 16:20:00', 3, 'USD','EUR'),
(6995.55, 'TRANSFER', 'CREDIT', '2021-11-15 09:45:00', 2,'EUR','BGN'),
(6104.84, 'TRANSFER', 'CREDIT', '2022-01-30 18:40:00', 6, 'USD', 'GBP'),
(6755.52, 'TRANSFER', 'CREDIT', '2020-10-20 16:20:00', 3, 'USD','BGN'),
(8912.72, 'TRANSFER', 'CREDIT', '2020-12-01 14:30:00', 1,'BGN', 'USD'),
(1505.15, 'TRANSFER', 'CREDIT', '2021-09-05 12:10:00', 4, 'GBP','BGN'),
(5852.30, 'TRANSFER', 'CREDIT', '2020-11-15 09:45:00', 2,'EUR','EUR');