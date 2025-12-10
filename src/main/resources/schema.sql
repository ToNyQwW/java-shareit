DROP TABLE IF EXISTS users, items;

CREATE TABLE IF NOT EXISTS users
(
    user_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name    VARCHAR(255)        NOT NULL,
    email   VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS items
(
    item_id      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name         VARCHAR(255)                                        NOT NULL,
    description  VARCHAR(255)                                        NOT NULL,
    is_available BOOLEAN                                             NOT NULL,
    owner_id     BIGINT REFERENCES users (user_id) ON DELETE CASCADE NOT NULL
);