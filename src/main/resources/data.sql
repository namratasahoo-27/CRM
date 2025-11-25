INSERT INTO role (role_id, role) VALUES (1, 'ROLE_ADMIN'), (2, 'ROLE_USER'), (3, 'ROLE_MANAGER'), (4, 'ROLE_OWNER')
ON DUPLICATE KEY UPDATE role=VALUES(role);

INSERT INTO users (id, email, enabled, first_name, last_name, password, username, role_role_id)
VALUES ('1', 'a@u', '1', 'AFN', 'ALN', '$2a$10$iPgnenFIoM67cYL9let/iOLBphbDaEkAz3BmiXOCmWq5A4M2TkXAG', 'admin', '1'),
  ('2', 'u@m', '1', 'UFN', 'ULN', '$2a$10$Ad.n7DA3e9QT.a8hXymxI.JKnAYTLR4nD4stJtfMiCLcr7FiZ/st.', 'user', '2'),
  ('3', 'm@m', '1', 'MFN', 'MLN', '$2a$10$iQy1MYc97kkXBwrCJ5I9gO/QcRT.rdY6UDKriBvG.iyX29miDaKDe', 'manager', '3'),
  ('4', 'o@m', '1', 'OFN', 'OLN', '$2a$10$VVH6bnOWLMczmH12BY99c.T6JMzMErt/gZKRCPfYlXcq7JMFoqkWW', 'owner', '4')
ON DUPLICATE KEY UPDATE email=VALUES(email), enabled=VALUES(enabled), first_name=VALUES(first_name),
  last_name=VALUES(last_name), password=VALUES(password), username=VALUES(username), role_role_id=VALUES(role_role_id);
# admin - pass = admin
# user - pass = user
# manager - pass = manager
# owner - pass = owner
SET FOREIGN_KEY_CHECKS=0;
INSERT INTO category (category_id, category)
VALUES ('1', 'small'), ('2', 'medium'), ('3', 'big')
ON DUPLICATE KEY UPDATE category=VALUES(category);

INSERT INTO customer (id, address, city, email, enabled, first_name, last_name, name, phone)
VALUES ('1', 'Small Street', 'Smallville', 'smallmail@mail.com', '1', 'SmallFN', 'SmallLN', 'Small INC', '123'),
  ('2', 'Medium Street', 'Midtown', 'midmail@mail.com', '1', 'MidFN', 'MidLN', 'Mid INC', '456'),
  ('3', 'Big Street', 'Big City', 'bigmail@mail.com', '1', 'BigFN', 'BigLN', 'Big INC', '789')
ON DUPLICATE KEY UPDATE address=VALUES(address), city=VALUES(city), email=VALUES(email),
  enabled=VALUES(enabled), first_name=VALUES(first_name), last_name=VALUES(last_name),
  name=VALUES(name), phone=VALUES(phone);

INSERT INTO customer_category (customer_id, category_id)
VALUES (1, 1), (2, 2), (3, 3)
ON DUPLICATE KEY UPDATE category_id=VALUES(category_id);

INSERT INTO contract (id, begin_date, content, end_date, name, status, value, customer_id, user_id)
VALUES ('1', '2018-02-24 00:00:00', 'contract content', '2018-02-25 00:00:00', 'ContractName', 'PROPOSED', '100000.00', '2', '2')
ON DUPLICATE KEY UPDATE begin_date=VALUES(begin_date), content=VALUES(content), end_date=VALUES(end_date),
  name=VALUES(name), status=VALUES(status), value=VALUES(value), customer_id=VALUES(customer_id), user_id=VALUES(user_id);

SET FOREIGN_KEY_CHECKS=1;