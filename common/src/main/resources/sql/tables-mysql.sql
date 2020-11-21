CREATE TABLE IF NOT EXISTS dac
(
    uuid   CHAR(36) NOT NULL,
    name   CHAR(16) NOT NULL,
    wins   INTEGER  NOT NULL,
    losses INTEGER  NOT NULL,
    jumps  INTEGER  NOT NULL,
    dacs   INTEGER  NOT NULL,
    PRIMARY KEY (uuid)
);
