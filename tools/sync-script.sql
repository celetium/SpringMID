drop table XP_PCK;
create table XP_PCK
(
  groupId varchar(30) not null,
  id varchar(30) not null,
  name varchar(30),
  converterId varchar(30) not null,
  trim integer,
  syncSeqNo integer not null,
  syncRowType integer not null,
  extraStatus varchar(1) not null
);
create unique index XP_PCK_UK on XP_PCK (syncSeqNo);

drop table XT_PCK;
create table XT_PCK
(
  groupId varchar(30) not null,
  id varchar(30) not null,
  name varchar(30),
  converterId varchar(30) not null,
  trim integer,
  syncSeqNo integer not null,
  syncRowType integer not null
);
create unique index XT_PCK_UK on XT_PCK (syncSeqNo);

drop table XX_SYNC_REC;
create table XX_SYNC_REC
(
  syncType varchar(1) not null,
  syncId varchar(30) not null,
  syncStatus varchar(1) not null,
  lastSyncSeqNo integer not null
);
create unique index XX_SYNC_REC_UK on XX_SYNC_REC (syncType, syncId);
create index XX_SYNC_REC_DK on XX_SYNC_REC (syncStatus);
-- For testing
drop table T_PCK_DUP;
create table T_PCK_DUP
(
  groupId varchar(30) not null,
  id varchar(30) not null,
  name varchar(30),
  converterId varchar(30) not null,
  trim integer
);
create unique index PCK_DUP_UK on T_PCK_DUP (groupId, id);
