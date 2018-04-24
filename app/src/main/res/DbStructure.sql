DROP TABLE IF EXISTS item_history_details;
DROP TABLE IF EXISTS item_histories;
DROP TABLE IF EXISTS items;
DROP TABLE IF EXISTS units;
DROP TABLE IF EXISTS accounts;
CREATE TABLE accounts(
    _id INTEGER PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL UNIQUE,
    password CHAR(64) NOT NULL,
    salt CHAR(5) NOT NULL,
    name VARCHAR(255) NOT NULL,
    role VARCHAR(100) NOT NULL,
    dob DATE NOT NULL,
    reg_date DATETIME NOT NULL
);


CREATE TABLE units(
    _id INTEGER PRIMARY KEY AUTO_INCREMENT,
    measure VARCHAR(100) NOT NULL,
    unit_name VARCHAR(100) NOT NULL,
    val DOUBLE NOT NULL,
    increment DOUBLE NOT NULL,
    unit_thumbnail VARCHAR(100)
);

CREATE TABLE items(
    _id INTEGER PRIMARY KEY AUTO_INCREMENT,
    item_name VARCHAR(255) NOT NULL UNIQUE,
    stock DOUBLE NOT NULL,
    safety_stock DOUBLE,
    unit_id INTEGER NOT NULL,
    FOREIGN KEY(unit_id) REFERENCES units(_id),
    location VARCHAR(100) NOT NULL,
    item_thumbnail VARCHAR(100)
);

CREATE TABLE item_histories(
    _id INTEGER PRIMARY KEY AUTO_INCREMENT,
    account_id INTEGER NOT NULL,
    FOREIGN KEY(account_id) REFERENCES accounts(_id),
    operation VARCHAR(100) NOT NULL,
    reason TEXT,
    occurence DATETIME NOT NULL
);

CREATE TABLE item_history_details(
    _id INTEGER PRIMARY KEY AUTO_INCREMENT,
    item_history_id INTEGER NOT NULL,
    FOREIGN KEY(item_history_id) REFERENCES item_histories(_id),
    item_id INTEGER NOT NULL,
    FOREIGN KEY(item_id) REFERENCES items(_id),
    stock_change DOUBLE NOT NULL
);