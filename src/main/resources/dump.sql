create table if not exists box
(
    id integer not null
        constraint box_pkey
            primary key
);

create table if not exists item
(
    id    integer not null
        constraint item_pkey
            primary key,
    color varchar(100) default NULL
);

create table if not exists box_item
(
    id      serial  not null,
    box_id  integer not null
        constraint fk_box
            references box,
    item_id integer not null
        constraint fk_item
            references item,
    constraint box_item_pkey
        primary key (box_id, item_id)
);