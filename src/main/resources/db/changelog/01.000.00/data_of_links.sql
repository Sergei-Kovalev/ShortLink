--liquibase formatted sql
--changeset Siarhei_Kavaleu:db localFilePath:01.000.00/data_of_links.sql
CREATE TABLE data_of_links
(
    id                  UUID                            NOT NULL,
    alias               VARCHAR(10)                     NOT NULL,
    full_link           VARCHAR                         NOT NULL,
    CONSTRAINT pk_data_of_links PRIMARY KEY (id)
);