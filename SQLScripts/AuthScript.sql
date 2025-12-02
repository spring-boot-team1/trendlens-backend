-- Account 테이블
-- DROP TABLE Account;

CREATE TABLE Account (
    seqAccount	    NUMBER(19)	        NOT NULL,
    email	        VARCHAR2(100)		NOT NULL,
    password	    VARCHAR2(100)		NULL,
    role	        VARCHAR2(50)	    DEFAULT 'ROLE_USER'	NOT NULL,
    provider	    VARCHAR2(50)		NULL,
    providerId	    VARCHAR2(100)		NULL
);

ALTER TABLE Account ADD CONSTRAINT "PK_Account" PRIMARY KEY (seqAccount);

-- AccountDetails 테이블
-- DROP TABLE AccountDetails;

CREATE TABLE AccountDetails (
    seqAccountDetail	NUMBER(19)		NOT NULL,
    seqAccount	        NUMBER(19)		NOT NULL,
    username	        VARCHAR2(50)	NULL,
    nickname	        VARCHAR2(50)	NULL,
    phonenum	        VARCHAR2(50)	NULL,
    birthday	        TIMESTAMP		NULL,
    profilepic	        VARCHAR2(200)	NULL
);

ALTER TABLE AccountDetails ADD CONSTRAINT "PK_AccountDETAILS"
PRIMARY KEY (seqAccountDetail);

ALTER TABLE AccountDetails ADD CONSTRAINT "FK_Account_TO_AccountDetails"
FOREIGN KEY (seqAccount) REFERENCES Account (seqAccount);