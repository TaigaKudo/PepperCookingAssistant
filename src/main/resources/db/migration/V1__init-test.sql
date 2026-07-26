CREATE TABLE "users" (
	"id" SERIAL PRIMARY KEY,
	"name" varchar(15) NOT NULL,
	"deleted" smallint DEFAULT 0
)