-- Account 테이블
-- DROP TABLE Account;
-- DROP SEQUENCE seqAccount;

CREATE SEQUENCE seqAccount;

CREATE TABLE Account (
    seqAccount	    NUMBER(19)	        NOT NULL, --PK
    email	        VARCHAR2(100)		NOT NULL, --UNIQUE
    password	    VARCHAR2(100)		NULL,
    role	        VARCHAR2(50)	    DEFAULT 'ROLE_USER'	NOT NULL,
    provider	    VARCHAR2(50)		NULL,
    providerId	    VARCHAR2(100)		NULL
);

ALTER TABLE Account ADD CONSTRAINT "PK_Account" PRIMARY KEY (seqAccount);
ALTER TABLE Account ADD CONSTRAINT "UQ_Account" UNIQUE (email);

-- AccountDetails 테이블
-- DROP TABLE AccountDetail;
-- DROP SEQUENCE seqAccountDetail

CREATE SEQUENCE seqAccountDetail;

CREATE TABLE AccountDetail (
    seqAccountDetail	NUMBER(19)		NOT NULL, --PK
    seqAccount	        NUMBER(19)		NOT NULL,
    username	        VARCHAR2(50)	NULL, --사용자 이름(실명)
    nickname	        VARCHAR2(50)	NULL, --사용자 닉네임
    phonenum	        VARCHAR2(50)	NULL,
    birthday	        DATE            NULL,
    profilepic	        VARCHAR2(200)	NULL
);

ALTER TABLE AccountDetail ADD CONSTRAINT "PK_AccountDETAIL" PRIMARY KEY (seqAccountDetail);

ALTER TABLE AccountDetail ADD CONSTRAINT "FK_Account_TO_AccountDetail" FOREIGN KEY (seqAccount) REFERENCES Account (seqAccount);

select * from Account;
select * from AccountDetail;

select * from Account
    inner join AccountDetail AD on Account.seqAccount = AD.seqAccount;

commit;