CREATE TABLE rabbit
(
    "id"           serial NOT NULL PRIMARY KEY,
    "created_date" VARCHAR(50)
);
CREATE TABLE post
(
    "id"      serial    NOT NULL PRIMARY KEY,
    "post_id" int       NOT NULL UNIQUE,
    "name"    VARCHAR   NOT NULL,
    "text"    VARCHAR   NOT NULL,
    "link"    VARCHAR   NOT NULL,
    "created" TIMESTAMP NOT NULL
);
