--liquibase formatted sql
--changeset creator:20240519-1539-init-creator.sql

CREATE TABLE IF NOT EXISTS creator (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,

    domain VARCHAR(255) NOT NULL,
    keycloak_id VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    phone_number VARCHAR(255),
    email VARCHAR(255) NOT NULL,
    contact_email VARCHAR(255),
    date_of_birth DATE,
    password_token VARCHAR(5000),
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,

    version BIGINT NOT NULL,
    created_date_time DATETIME(6) NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    last_modified_date_time DATETIME(6) NOT NULL,
    last_modified_by VARCHAR(255) NOT NULL,

    CONSTRAINT pk_creator PRIMARY KEY (id)
) ENGINE=InnoDB;