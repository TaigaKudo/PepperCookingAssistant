CREATE TABLE refresh_tokens (
	"id" BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	"user_id" BIGINT NOT NULL REFERENCES users(id),
	"token_hash" VARCHAR(255) NOT NULL,
	"expires_at" TIMESTAMP NOT NULL,
	"revoked_at" TIMESTAMP,
	"created_at" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	"updated_at" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);