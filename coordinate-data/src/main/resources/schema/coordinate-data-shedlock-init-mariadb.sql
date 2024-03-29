CREATE TABLE IF NOT EXISTS COD_DB_LOCK(
    name VARCHAR(128), 
    lock_until TIMESTAMP(3) NULL, 
    locked_at TIMESTAMP(3) NULL, 
    locked_by  VARCHAR(255), 
    PRIMARY KEY (name)
);