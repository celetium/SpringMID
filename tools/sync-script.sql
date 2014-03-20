drop table XP_PCK;
create table XP_PCK
(
  groupId varchar(30) not null,
  id varchar(30) not null,
  name varchar(30),
  converterId varchar(30) not null,
  trim integer,
  syncseqno integer not null,
  extrastatus varchar(1) not null
);
create unique index XP_PCK_UK on XP_PCK (syncseqno);

drop table XT_PCK;
create table XT_PCK
(
  groupId varchar(30) not null,
  id varchar(30) not null,
  name varchar(30),
  converterId varchar(30) not null,
  trim integer,
  syncseqno integer not null
);
create unique index XT_PCK_UK on XT_PCK (syncseqno);
