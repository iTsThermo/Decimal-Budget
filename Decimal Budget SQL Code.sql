create database javabook;
create user 'scott'@'localhost' identified by 'tiger';
grant select, insert, update, delete, create, create view, drop,
 execute, references on javabook.* to 'scott'@'localhost';

use javabook;
drop table if exists Accounts;
drop table if exists Transactions;
drop table if exists CategoryIdTable;
drop table if exists Category;

create table Accounts (
    username varchar(15),
    pass varchar(15),
    firstName varchar(25),
    lastName varchar(25),
    accountId int(25) auto_increment,
    primary key (accountId)
);

create table CategoryIdTable(
    categoryId int(20) auto_increment,
    accountId int (25),
    primary key(categoryId),
    foreign key (accountId) references Accounts(accountId)
);

create table Transactions (
    accountId int(25),
    transactionId int(25) auto_increment,
    transactionDesc varchar(150),
    entryDate date,
    current_val float(10) DEFAULT 0,
    amount float(10),
    categoryId int(20),
    primary key (transactionId),
    foreign key (accountId) references Accounts(accountId),
    foreign key (categoryId) references CategoryIdTable(categoryId)
);

create table Category(
    categoryName varchar(25),
    startDate date,
    endDate date,
    amountSet float(20),
    categoryId int(20) auto_increment,
    foreign key (categoryId) references CategoryIdTable(CategoryId)
);