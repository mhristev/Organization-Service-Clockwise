CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- V1__create_companies_table.sql

CREATE TABLE companies (
   id VARCHAR(50) DEFAULT uuid_generate_v4()::text PRIMARY KEY ,
   name VARCHAR(255) NOT NULL,
   description TEXT
);

-- V2__create_restaurants_table.sql

CREATE TABLE business_unit (
    id VARCHAR(50) DEFAULT uuid_generate_v4()::text PRIMARY KEY,
     name VARCHAR(255) NOT NULL,
     location VARCHAR(255) NOT NULL,
     description TEXT,
     company_id VARCHAR(50) NOT NULL,
     CONSTRAINT fk_company FOREIGN KEY (company_id) REFERENCES companies(id)
);