/*
Create table for test

*/
--------------------------------------------------------
--  DDL for Table X_LOB
--------------------------------------------------------
DROP TABLE X_LOB;


  CREATE TABLE "X_LOB"
   (
   "ID" NUMBER,
   "CAMPO_JSON" CLOB,
   "INS_DATE" DATE DEFAULT SYSDATE
   ) SEGMENT CREATION IMMEDIATE
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255
 NOCOMPRESS NOLOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "SUAPER_DATA"
 LOB ("CAMPO_JSON") STORE AS SECUREFILE (
  TABLESPACE "TS_DATA" DISABLE STORAGE IN ROW CHUNK 16384
  NOCACHE LOGGING  NOCOMPRESS  KEEP_DUPLICATES
  STORAGE(INITIAL 1024M NEXT 100M MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT))
  PARALLEL ;

   COMMENT ON TABLE "X_LOB"  IS 'DA CANCELLARE';





COMMENT ON TABLE X_LOB IS 'DA CANCELLARE';



