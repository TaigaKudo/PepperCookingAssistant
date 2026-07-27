CREATE TABLE users (
	"id" bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	"name" varchar(50) NOT NULL,
	"email" varchar(255) NOT NULL UNIQUE,
	"password_hash" varchar(255) NOT NULL,
	"created_at" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	"deleted_at" TIMESTAMP,
	"updated_at" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE ingredient_categories (
	"id" bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	"category_name" varchar(50) NOT NULL UNIQUE,
	"reading" varchar(50),
	"created_at" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	"deleted_at" TIMESTAMP,
	"updated_at" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE ingredients (
	"id" bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	"name" varchar(50) NOT NULL,
	"reading" varchar(50),
	"default_unit" varchar(20),
	"category_id" bigint REFERENCES ingredient_categories(id),
	"created_at" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	"deleted_at" TIMESTAMP,
	"updated_at" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE stocks (
	"id" bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	"ingredient_id" bigint NOT NULL REFERENCES ingredients(id),
	"user_id" bigint NOT NULL REFERENCES users(id),
	"quantity" numeric(10,2) NOT NULL,
	"expiration_date" date NOT NULL,
	"created_at" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	"deleted_at" TIMESTAMP,
	"updated_at" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);