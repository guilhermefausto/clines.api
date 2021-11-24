CREATE TABLE locations (
    id BIGINT AUTO_INCREMENT,
    country VARCHAR(2),
    state VARCHAR (2),
    city VARCHAR(100),

    CONSTRAINT pk_locations PRIMARY KEY (id),
    CONSTRAINT unq_locations UNIQUE (country, state, city)
);


