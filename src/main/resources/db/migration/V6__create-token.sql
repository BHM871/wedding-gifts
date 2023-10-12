CREATE TABLE tb_token(
    id UUID UNIQUE NOT NULL PRIMARY KEY,
    token TEXT UNIQUE NOT NULL,
    limit_hour TIMESTAMP NOT NULL,
    account_id UUID UNIQUE NOT NULL,
    CONSTRAINT fk_token_account FOREIGN KEY(account_id) REFERENCES tb_account(id)
);