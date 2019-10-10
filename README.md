# spring-boot-batch_clobloader_oracle
Upload massivo da una cartella contenente file di testo (esempio json) nelle righe di una tabella con una colonna clob oracle, esperimento con spring boot bach


Per eseguire il test
va creata una tabella X_LOB attraverso il file **src\main\resources\ddl.sql**

Come 
TABLESPACE "TS_DATA" DISABLE STORAGE IN ROW CHUNK 16384
  **NOCACHE LOGGING  NOCOMPRESS**  KEEP_DUPLICATES
  STORAGE(INITIAL 1024M NEXT 100M MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0
