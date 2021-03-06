-- 源表
drop table T_PCK;
create table T_PCK
(
  groupId varchar(30) not null,
  id varchar(30) not null,
  name varchar(30),
  converterId varchar(30) not null,
  trim integer,
  primary key (groupId, id)
);
-- 源表数据变化表
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
-- 数据变化中间表
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
-- 数据同步记录表
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
-- 目的表（测试用）
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
drop table T_CONVERTER;
create table T_CONVERTER
(
  id varchar(30) not null,
  name varchar(30)
);
create unique index T_CONVERTER_UK on T_CONVERTER (id);
