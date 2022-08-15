create table if not exists customer
(
    id         serial,
    first_name varchar(50)  not null,
    last_name  varchar(50)  not null,
    login      varchar(255) not null,
    password   varchar(255) not null,
    email      varchar(255) not null,
    status     varchar(50)  not null,
    primary key (id)
);

create table if not exists file
(
    id        serial,
    file_name varchar(255) not null,
    location  varchar(255) not null,
    status    varchar(50)  not null,
    primary key (id)
);

create table if not exists event
(
    id          serial,
    customer_id bigint unsigned NOT NULL,
    file_id     BIGINT unsigned NOT NULL,
    status      varchar(50)     not null,
    date        timestamp       not null,
    primary key (id),
    foreign key (customer_id) references customer (id),
    foreign key (file_id) references file (id)
);

