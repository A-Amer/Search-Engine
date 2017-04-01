
-- -----------------------------------------------------
-- Schema indexer
-- -----------------------------------------------------
CREATE Database indexer 
GO
use indexer 

CREATE TABLE keyword
 (
  url VARCHAR(500) NOT NULL,
  keyword VARCHAR(45) NOT NULL,
  frequency int NOT NULL,
  orders VARCHAR(500) NOT NULL,
  PRIMARY KEY (url, keyword));




-- -----------------------------------------------------
-- Table `indexer`.`spam`
-- -----------------------------------------------------

CREATE TABLE spam (
  spamurl VARCHAR(500) NOT NULL,
  PRIMARY KEY (spamurl))

CREATE TABLE seeds
( seedURL VARCHAR(500)
   PRIMARY KEY (seedURL)
 ) 

CREATE TABLE PagesInOut
( url VARCHAR (500),
   pin int , 
   pout int ,
   PRIMARY KEY (url)) 
-- -----------------------------------------------------
-- procedure AddKeyword
-- -----------------------------------------------------






USE indexer;
GO 

CREATE PROCEDURE AddKeyword(
@Url varchar(500),
@Word varchar(45), 
@Weight int,
@wordorder varchar(500))
AS
BEGIN

insert into keywords values (@Url,@Word,@Weight,@wordorder);

END
GO

CREATE PROCEDURE AddSpam(@spamurl varchar(500))
 AS
BEGIN
insert into spam values (@spamurl);
END
GO

-- -----------------------------------------------------
-- procedure DeleteUrlKeywords
-- -----------------------------------------------------


CREATE PROCEDURE DeleteUrlKeywords (@U varchar(500) ) AS
BEGIN
delete from keywords where url = @U; 

END
GO

CREATE PROCEDURE InsertPagesInOut (@URL varchar(500) , @pin int , @pout int)
AS 
BEGIN
Insert into PagesInOut  values (@URL , @pin , @pout ) ;
END 
GO




CREATE PROCEDURE InsertSeeds (@URL varchar(500))
AS 
BEGIN
Insert into seeds (seedURL) values (@URL) ;
END
GO
-- -----------------------------------------------------
-- procedure DeleteSeed
-- -----------------------------------------------------

CREATE PROCEDURE DeleteSeeds
AS 
BEGIN
delete from seeds ;
END
GO

