# --- Create tables

# --- !Ups

create table token (
  uuid VARCHAR NOT NULL PRIMARY KEY ,
  email VARCHAR NOT NULL,
  creation_time TIMESTAMP NOT NULL,
  expiration_time TIMESTAMP NOT NULL,
  is_sign_up BOOLEAN NOT NULL
);

create table basic_user (
  id SERIAL PRIMARY KEY
);

create table basic_profile (
  user_id VARCHAR NOT NULL PRIMARY KEY,
  provider_id VARCHAR NOT NULL,
  first_name VARCHAR NOT NULL,
  last_name VARCHAR NOT NULL,
  full_name VARCHAR NOT NULL,
  email VARCHAR,
  avatar_url VARCHAR,
  auth_method VARCHAR NOT NULL,
  token VARCHAR,
  secret VARCHAR,
  access_token VARCHAR,
  token_type VARCHAR,
  expires_in INTEGER,
  refresh_token VARCHAR,
  hasher VARCHAR,
  password VARCHAR,
  basic_user_id INTEGER REFERENCES basic_user(id) NOT NULL
);


# --- !Downs

drop table basic_profile;
drop table basic_user;
drop table token;