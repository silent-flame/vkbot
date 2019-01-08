create table users
(
id int not null
constraint users_pk
primary key,
first_name text not null,
last_name text not null,
subscriptions TEXT
);

create unique index users_id_uindex
on users (id);