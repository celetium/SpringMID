drop table T_DEPLOYED_ROUTED;
create table T_DEPLOYED_ROUTED
(
  domainId varchar(30) not null,
  serverId varchar(30) not null,
  processId integer not null,
  beanId varchar(30) not null
);
create unique index DEPLOYED_ROUTED_UK on T_DEPLOYED_ROUTED (domainId, processId, beanId);
create index DEPLOYED_ROUTED_DK on T_DEPLOYED_ROUTED (beanId);

drop table T_ROUTE;
create table T_ROUTE
(
  routeKey varchar(30) not null primary key,
  beanId varchar(30) not null,
  routeToPolicy integer,
);

drop table T_DOMAIN;
create table T_DOMAIN
(
  domainId varchar(30) not null primary key,
  properties varchar(99) not null
);

drop table T_PCK;
create table T_PCK
(
  groupId varchar(30) not null,
  id varchar(30) not null,
  name varchar(30),
  converterId varchar(30) not null,
  trim integer
);
create unique index PCK_UK on T_PCK (groupId, id);

drop table T_PCK_ITEM;
create table T_PCK_ITEM
(
  pckId varchar(30) not null,
  no integer not null,
  id varchar(30) not null,
  repeat integer,
  binary integer,
  option integer,
  trim integer,
  ref varchar(30),
  refAs integer,
  defaultValue varchar(99),
  overwriteValue varchar(99)
);
create unique index PCK_ITEM_UK on T_PCK_ITEM (pckId, no);
