create table buildings
(
    id serial not null
        constraint buildings_pkey
            primary key,
    geolocation varchar(255),
    name varchar(35) not null
);

create table employees
(
    id serial not null
        constraint employees_pkey
            primary key,
    email varchar(40)
        constraint uk_j9xgmd0ya5jmus09o0b8pqrpb
            unique,
    name varchar(35) not null,
    patronymic varchar(35) not null,
    position_name varchar(35) not null,
    salary bigint,
    surname varchar(35) not null,
    time_type varchar(255),
    building_id bigint
        constraint fkcn3eqktgq9h0sm6lrwgf5t3dh
            references buildings
);

create table equipments
(
    id serial not null
        constraint equipments_pkey
            primary key,
    name varchar(35) not null,
    price bigint,
    quantity bigint,
    building_id bigint not null
        constraint fkcg110843e2yjp5l8dsjeeq4u4
            references buildings,
    employee_id bigint
        constraint fkocd869ruac1gh4mn48xm5jlx8
            references employees
);

create table ledgers
(
    id serial not null
        constraint ledgers_pkey
            primary key,
    bookkeeping varchar(255),
    due_time timestamp,
    name varchar(35) not null,
    price bigint,
    procurement_type varchar(255),
    quantity bigint,
    unit_of_measurement varchar(255),
    building_id bigint not null
        constraint fkh9pf0ifm43qrf0cdyhssld8gu
            references buildings
);

create table warehouses
(
    id serial not null
        constraint warehouses_pkey
            primary key,
    name varchar(35) not null,
    quantity bigint,
    unit_of_measurement varchar(255),
    building_id bigint not null
        constraint fkl8181ccrg6ydrp1xwbh06vtqu
            references buildings
);
