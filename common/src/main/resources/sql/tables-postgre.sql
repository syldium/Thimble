CREATE TABLE IF NOT EXISTS thimble_players
(
    uuid     UUID,
    name     VARCHAR(16) NOT NULL,
    wins     INTEGER     NOT NULL,
    losses   INTEGER     NOT NULL,
    jumps    INTEGER     NOT NULL,
    fails    INTEGER     NOT NULL,
    thimbles INTEGER     NOT NULL,
    PRIMARY KEY (uuid)
);
