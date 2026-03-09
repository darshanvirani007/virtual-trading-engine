create table refresh_tokens (
                                id bigserial primary key,
                                user_id bigint not null references users(id),
                                token_hash varchar(64) not null unique,
                                created_at timestamp not null,
                                expires_at timestamp not null,
                                revoked_at timestamp null
);

create index idx_refresh_tokens_user_id on refresh_tokens(user_id);