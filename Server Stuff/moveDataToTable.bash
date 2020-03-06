#!/bin/bash
export PATH=/home/students/apelianr/java/jre1.8.0_241/bin:$PATH
java -jar Table_Maker.jar
sleep 0.5
mysql <<MY_QUERY
USE test
DELETE FROM table1;
ALTER TABLE table1 AUTO_INCREMENT = 1;
LOAD DATA LOCAL INFILE 'data.csv'
INTO TABLE table1
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
MY_QUERY
