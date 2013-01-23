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
  host varchar(30) not null,
  requestPort integer not null,
  replyPort integer not null
);