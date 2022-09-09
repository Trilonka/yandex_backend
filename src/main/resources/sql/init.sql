create table system_item(
    id varchar primary key,
    url varchar(255),
    date varchar(255) not null,
    type int not null,
    size int,
    parent_id varchar(255) references system_item(id) on delete cascade
);