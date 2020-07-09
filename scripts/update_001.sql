CREATE TABLE rabbit
(
    "id"           serial NOT NULL PRIMARY KEY,
    "created_date" VARCHAR(50)
);
CREATE TABLE post
(
    "id"      serial       NOT NULL PRIMARY KEY,
    "name"    VARCHAR(80)  NOT NULL,
    "text"    VARCHAR(300) NOT NULL,
    "link"    VARCHAR(80)  NOT NULL,
    "created" TIMESTAMP    NOT NULL
);