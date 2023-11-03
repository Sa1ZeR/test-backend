create table author (
    id serial primary key,
    name varchar(96) not null,
    created timestamp without time zone DEFAULT now()
);