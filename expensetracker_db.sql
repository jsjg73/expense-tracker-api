drop table if exists et_users;

create table et_users(
user_id int AUTO_INCREMENT primary key not null,
first_name varchar(20) not null,
last_name varchar(20) not null,
email varchar(50) not null,
password text not null
);

drop table if exists et_categories;
create table et_categories(
category_id int AUTO_INCREMENT primary key not null,
user_id int not null,
title varchar(20) not null,
description varchar(50) not null,
constraint cat_users_fk foreign key (user_id)
    references et_users(user_id)
);

drop table if exists et_transactions;
create table et_transactions(
transaction_id int AUTO_INCREMENT primary key not null,
category_id int not null,
user_id int not null,
amount decimal(10,2) not null,
note varchar(50) not null,
transaction_date bigint not null,
CONSTRAINT trans_cat_fk FOREIGN KEY (category_id) REFERENCES et_categories(category_id),
CONSTRAINT trans_users_fk FOREIGN KEY (user_id) REFERENCES et_users(user_id)
);