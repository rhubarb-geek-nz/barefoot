CREATE TABLE users(username varchar(64) PRIMARY KEY, password varchar(256) DEFAULT NULL, enabled boolean);
CREATE TABLE authorities(username VARCHAR(255), authority VARCHAR(255), foreign key (username) references users(username));

INSERT INTO users(username, password, enabled) VALUES ('barefoot', '$2a$10$Y8Ka6E8qo..PH6FZY5TvhOVEKXhsTCFCVJdYSLa/IweD7WDLivr1q', 1);
INSERT INTO authorities(username, authority) VALUES ('barefoot', 'ROLE_USER');
COMMIT;
