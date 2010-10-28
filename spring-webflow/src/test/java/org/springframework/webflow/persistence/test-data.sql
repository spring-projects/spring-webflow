drop table T_ADDRESS if exists;
drop table T_BEAN if exists;
create table T_BEAN (ID integer primary key, NAME varchar(50) not null, COUNTER integer);
create table T_ADDRESS (ID integer primary key, BEAN_ID integer, VALUE varchar(50) not null);
alter table T_ADDRESS add constraint FK_BEAN_ADDRESS foreign key (BEAN_ID) references T_BEAN(ID) on delete cascade;
insert into T_BEAN (ID, NAME, COUNTER) values (0, 'Ben Hale',0);
insert into T_ADDRESS (ID, BEAN_ID, VALUE) values (0, 0, 'Melbourne');