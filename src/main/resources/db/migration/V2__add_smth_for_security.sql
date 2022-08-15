create table if not exists role
(
    id serial,
    name varchar(100) not null ,
    primary key (id)
);

create table if not exists customer_role
(
    customer_id BIGINT unsigned NOT NULL ,
    role_id BIGINT unsigned NOT NULL ,

    foreign key (customer_id) references customer(id),
    foreign key (role_id) references role(id)
);