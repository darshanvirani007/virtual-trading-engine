create table users (
                       id bigserial primary key,
                       username varchar(255) not null unique,
                       email varchar(255) not null,
                       password varchar(255) not null,
                       balance numeric(19,2) not null
);

create table stocks (
                        id bigserial primary key,
                        symbol varchar(32) not null unique,
                        name varchar(255) not null,
                        current_price numeric(19,4) not null
);

create table portfolio_items (
                                 id bigserial primary key,
                                 user_id bigint not null references users(id),
                                 stock_id bigint not null references stocks(id),
                                 quantity int not null,
                                 average_price numeric(19,4) not null,
                                 constraint uq_portfolio_user_stock unique (user_id, stock_id)
);

create table transactions (
                              id bigserial primary key,
                              user_id bigint not null references users(id),
                              stock_id bigint not null references stocks(id),
                              quantity int not null,
                              price numeric(19,4) not null,
                              created_at timestamp not null,
                              type varchar(16) not null
);

create index idx_transactions_user_created_at on transactions(user_id, created_at desc);