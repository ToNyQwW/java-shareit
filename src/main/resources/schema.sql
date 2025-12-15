CREATE TABLE IF NOT EXISTS users
(
    user_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name    VARCHAR(255)        NOT NULL,
    email   VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS items
(
    item_id      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name         VARCHAR(255) NOT NULL,
    description  VARCHAR(255) NOT NULL,
    is_available BOOLEAN      NOT NULL,
    owner_id     BIGINT       NOT NULL,
    CONSTRAINT fk_owner_id FOREIGN KEY (owner_id) REFERENCES users (user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS bookings
(
    booking_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    start_date TIMESTAMP   NOT NULL,
    end_date   TIMESTAMP   NOT NULL,
    item_id    BIGINT      NOT NULL,
    booker_id  BIGINT      NOT NULL,
    status     VARCHAR(15) NOT NULL,
    CONSTRAINT fk_item_id FOREIGN KEY (item_id) REFERENCES items (item_id) ON DELETE CASCADE,
    CONSTRAINT fk_booker_id FOREIGN KEY (booker_id) REFERENCES users (user_id) ON DELETE CASCADE,
    CHECK ( status IN ('WAITING', 'APPROVED', 'REJECTED', 'CANCELLED') ),
    CHECK (start_date < end_date)
);

CREATE TABLE IF NOT EXISTS comments
(
    comment_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    text       VARCHAR(255) NOT NULL,
    item_id    BIGINT       NOT NULL,
    author_id  BIGINT       NOT NULL,
    created    TIMESTAMP    NOT NULL,
    CONSTRAINT fk_item_id FOREIGN KEY (item_id) REFERENCES items (item_id) ON DELETE CASCADE,
    CONSTRAINT fk_author_id FOREIGN KEY (author_id) REFERENCES users (user_id) ON DELETE CASCADE
)