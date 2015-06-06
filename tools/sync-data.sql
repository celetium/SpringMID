INSERT INTO T_CONVERTER VALUES ('converter.fix', 'Fix length');
INSERT INTO T_CONVERTER VALUES ('converter.div', 'Dividing symbol');
-- 仅测试Replicar
DELETE FROM T_PCK_DUP WHERE 1=1;
DELETE FROM XT_PCK WHERE 1=1;
DELETE FROM XX_SYNC_REC WHERE 1=1;
INSERT INTO XT_PCK VALUES('test1','FIX1','','converter.fix',0,1,1);
INSERT INTO XT_PCK VALUES('test1','DIV1','','converter.div',0,2,1);
-- 验证
SELECT * FROM T_PCK_DUP;
-- 仅测试Extrar
DELETE FROM XT_PCK WHERE 1=1;
DELETE FROM XP_PCK WHERE 1=1;
INSERT INTO XP_PCK VALUES('test1','FIX1','','converter.fix',0,1,1,'N');
INSERT INTO XP_PCK VALUES('test1','DIV1','','converter.div',0,2,1,'N');
-- 验证
SELECT * FROM XT_PCK;
-- 同时测试Extrar+Replicar
DELETE FROM XP_PCK WHERE 1=1;
DELETE FROM XT_PCK WHERE 1=1;
DELETE FROM T_PCK_DUP WHERE 1=1;
DELETE FROM XX_SYNC_REC WHERE 1=1;
INSERT INTO XP_PCK VALUES('test1','FIX1','','converter.fix',0,1,1,'N');
INSERT INTO XP_PCK VALUES('test1','DIV1','','converter.div',0,2,1,'N');
-- 测试Trigger
INSERT INTO T_PCK VALUES('test1','FIX1','Hello,World!!!','converter.fix',0);