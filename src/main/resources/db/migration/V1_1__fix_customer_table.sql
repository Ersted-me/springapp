ALTER TABLE customer MODIFY COLUMN email varchar(255) not null unique;
ALTER TABLE customer MODIFY COLUMN login varchar(255) not null unique;