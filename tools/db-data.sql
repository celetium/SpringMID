insert into T_ROUTE values ('ABC', 'biz-sample-1', 0);

insert into T_PCK values ('test1', 'FIX1', '', 'converter.fix', 0);
insert into T_PCK values ('test1', 'DIV1', '', 'converter.div', 0);

insert into T_PCK_ITEM (pckId, no, id, length) values ('FIX1', 1, 'Num', 5);
insert into T_PCK_ITEM (pckId, no, id, length) values ('FIX1', 2, 'Str', 3);
insert into T_PCK_ITEM (pckId, no, id, length, ref, refAs) values ('FIX1', 3, 'End', 4, 'DIV1', 1);
insert into T_PCK_ITEM (pckId, no, id) values ('DIV1', 1, 'Sub1');
insert into T_PCK_ITEM (pckId, no, id) values ('DIV1', 2, 'Sub2');
