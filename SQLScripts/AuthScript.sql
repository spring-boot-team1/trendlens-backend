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
    profilepic	        VARCHAR2(500)	NULL -- S3에 저장된 이미지의 주소
);

ALTER TABLE AccountDetail ADD CONSTRAINT "PK_AccountDETAIL" PRIMARY KEY (seqAccountDetail);

ALTER TABLE AccountDetail ADD CONSTRAINT "FK_Account_TO_AccountDetail" FOREIGN KEY (seqAccount) REFERENCES Account (seqAccount);

ALTER TABLE AccountDetail MODIFY profilepic varchar2(500);
ALTER TABLE AccountDetail MODIFY profilepic varchar2(500) DEFAULT
'https://trendlens.s3.ap-northeast-2.amazonaws.com/uploads/profilepic/8f90e5a7-3519-4a58-b8ea-a91a41e74bd8.png';

select * from Account order by seqAccount;
select * from AccountDetail order by seqAccountDetail;

select * from Account A
    inner join AccountDetail AD on A.seqAccount = AD.seqAccount
order by A.seqAccount DESC;

delete from AccountDetail where seqAccountDetail between 3 and 8;
delete from Account where seqAccount between 3 and 8;
commit;

update ACCOUNTDETAIL SET profilepic='https://trendlens.s3.ap-northeast-2.amazonaws.com/uploads/profilepic/8f90e5a7-3519-4a58-b8ea-a91a41e74bd8.png' where seqAccountDetail between 1 and 23;
commit;