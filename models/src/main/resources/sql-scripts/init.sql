INSERT INTO cash (user_id, current_cash) VALUES (1, 1500);
INSERT INTO cash (user_id, current_cash) VALUES (2, 2500);
INSERT INTO cash (user_id, current_cash) VALUES (3, 300);
INSERT INTO transactions (cash, timestamp, from_id, to_id) VALUES (120, TIMESTAMP '2020-01-01 15:36:38', 1, 2);