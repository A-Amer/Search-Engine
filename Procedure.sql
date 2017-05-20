Alter Table guest.Keyword ADD ID INT IDENTITY not null ;
create unique index Keyword_Idx on guest.keyword(ID)  
Create fulltext Catalog  kw_ind as default; 
create fulltext index on guest.Keyword(keyword) 
key index ID;
CREATE TABLE guest.Snippet
 (
  url VARCHAR(500) NOT NULL,
  title VARCHAR(500) NOT NULL,
  txt varchar(max),
  PRIMARY KEY (url));

sp_tableoption 'guest.Snippet','large value types out of row', 'ON';
create fulltext index on guest.Snippet(txt) 
key index PK__Snippet__DD778416DC2065D3 ;

Create view guest.Total as Select Count(DISTINCT URL) From guest.Keyword;

Create Procedure GetTot
AS
SELECT Tot FROM guest.Total;

Create table guest.Inters(url VARCHAR(500));

create procedure GetFree(@word VARCHAR(50))
AS
select k.URL as URL,pin,pout,keyword,frequency
from guest.Keyword AS k,guest.PagesInOut AS p
where freetext(keyword,@word)  AND p.url=k.URL
ORDER BY pin ,pout ;


drop procedure GetExact;
create procedure GetExact(@word VARCHAR(1000))
as
select s.url as URL,(((LEN(s.txt)-LEN(REPLACE(s.txt,@word,'')))/LEN(@word))*(pin+1)) as  ranking
from guest.Snippet as s,guest.PagesInOut AS p 
where REPLACE(REPLACE(s.txt,',',' '),'.',' ') Like'%'+@word+' %' AND s.url=p.URL
order by ranking desc ;

CREATE PROCEDURE AddSnippet(
@Url varchar(800),
@txt varchar(max), 
@title VARCHAR(500))
AS
insert into guest.Snippet values (@Url,@title,@txt);


create procedure GetSnip(@url VARCHAR(500))
AS
select  title,txt
from guest.Snippet
where url=@url;
