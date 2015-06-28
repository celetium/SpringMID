
--
-- 源表: T_PCK -- BEGIN
--

DROP TABLE XP_T_PCK;
CREATE TABLE XP_T_PCK AS SELECT * FROM T_PCK WHERE 1 > 9;
ALTER TABLE XP_T_PCK ADD (
  syncSeqNo INTEGER NOT NULL AUTO_INCREMENT,
  syncRowType INTEGER NOT NULL,
  extraStatus VARCHAR(1) NULL,
  primary key (syncSeqNo)
);

DELIMITER $
DROP TRIGGER IF EXISTS TRI_T_PCK_INSERT$
CREATE TRIGGER TRI_T_PCK_INSERT AFTER INSERT ON T_PCK
FOR EACH ROW
BEGIN
  INSERT INTO XP_T_PCK (groupId, id, name, converterId, trim, syncRowType, extraStatus) 
  VALUES (NEW.groupId, NEW.Id, NEW.name, NEW.converterId, NEW.trim, 1, 'N');
END$
DROP TRIGGER IF EXISTS TRI_T_PCK_UPDATE$
CREATE TRIGGER TRI_T_PCK_UPDATE AFTER UPDATE ON T_PCK
FOR EACH ROW
BEGIN
  set @xType = 2;
  set @xgroupId = OLD.groupId;
  IF OLD.groupId != NEW.groupId THEN
    set @xgroupId = NEW.groupId;
    set @xType = 1;
  END IF;
  set @xId = OLD.Id;
  IF OLD.Id != NEW.Id THEN
    set @xId = NEW.Id;
    set @xType = 1;
  END IF;
  INSERT INTO XP_T_PCK (groupId, id, name, converterId, trim, syncRowType, extraStatus) 
    VALUES (NEW.groupId, NEW.Id, NEW.name, NEW.converterId, NEW.trim, @xType, 'N');
  IF @xType = 1 THEN
    INSERT INTO XP_T_PCK (groupId, id, name, converterId, trim, syncRowType, extraStatus) 
      VALUES (OLD.groupId, OLD.Id, OLD.name, OLD.converterId, OLD.trim, 3, 'N');
  END IF;
END;$
DROP TRIGGER IF EXISTS TRI_T_PCK_DELETE$
CREATE TRIGGER TRI_T_PCK_DELETE AFTER DELETE ON T_PCK
FOR EACH ROW
BEGIN
  INSERT INTO XP_T_PCK (groupId, id, name, converterId, trim, syncRowType, extraStatus) 
    VALUES (OLD.groupId, OLD.Id, OLD.name, OLD.converterId, OLD.trim, 3, 'N');
END$
DELIMITER ;

--
-- 源表: T_PCK -- END
--
