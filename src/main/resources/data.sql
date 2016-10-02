create database school;
use  school;

DROP TABLE IF EXISTS user;
CREATE TABLE user (
  userid int(11) NOT NULL AUTO_INCREMENT,
  username varchar(255) NOT NULL,
  password varchar(255) NOT NULL,
  PRIMARY KEY (userid)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

select *from user;
insert into user(username,password) values('a','a');