use mydb;

create table if not exists employee (
empid varchar(10) not null,
empname varchar(100) not null
);

insert into employee values ('101', 'foo');
insert into employee values ('102', 'bar');