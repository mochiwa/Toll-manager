------------------------------------------------------------
--        Script Postgre 
------------------------------------------------------------
/**
TRUNCATE ALL :
SELECT 'TRUNCATE TABLE '
       || string_agg(quote_ident(schemaname) || '.' || quote_ident(tablename), ', ')
       || ' CASCADE'
   FROM   pg_tables
   WHERE  tableowner = 'postgres'
   AND    schemaname = 'public'



   TRUNCATE TABLE public.rule, public.groups, public.address, public.phone, public.email, public.planning, public.situated, public.located, public.hasrules, public.belong, public.team, public.ismember, public.cities, public.country, public.employee, public.lives CASCADE

**/

------------------------------------------------------------
-- Table: address
------------------------------------------------------------
CREATE TABLE public.address(
	address_id       SERIAL NOT NULL ,
	address_street   VARCHAR (50) NOT NULL ,
	address_number   VARCHAR (50) NOT NULL  ,
	CONSTRAINT address_PK PRIMARY KEY (address_id)
)WITHOUT OIDS;


------------------------------------------------------------
-- Table: cities
------------------------------------------------------------
CREATE TABLE public.cities(
	city_id     SERIAL NOT NULL ,
	city_name   VARCHAR (50) NOT NULL ,
	city_zip    VARCHAR (50) NOT NULL  ,
	CONSTRAINT cities_PK PRIMARY KEY (city_id)
)WITHOUT OIDS;


------------------------------------------------------------
-- Table: country
------------------------------------------------------------
CREATE TABLE public.country(
	country_id     SERIAL NOT NULL ,
	country_name   VARCHAR (50) NOT NULL  ,
	CONSTRAINT country_PK PRIMARY KEY (country_id)
)WITHOUT OIDS;


------------------------------------------------------------
-- Table: employee
------------------------------------------------------------
CREATE TABLE public.employee(
	employee_id          VARCHAR (100) NOT NULL ,
	employee_niss        VARCHAR (50) NOT NULL  ,
	employee_name        VARCHAR (50) NOT NULL ,
	employee_forename    VARCHAR (50) NOT NULL ,
	employee_birthday    DATE  NOT NULL ,
	employee_hire_date   DATE  NOT NULL ,
	employee_login       VARCHAR (50)  ,
	employee_password    VARCHAR (50)  ,
	CONSTRAINT employee_PK PRIMARY KEY (employee_id) ,
	CONSTRAINT employee_AK UNIQUE (employee_niss)
)WITHOUT OIDS;


------------------------------------------------------------
-- Table: rule
------------------------------------------------------------
CREATE TABLE public.rule(
	rule_id            SERIAL NOT NULL ,
	rule_name          VARCHAR (50) NOT NULL ,
	rule_target        VARCHAR (50) NOT NULL  ,
	rule_description   TEXT,
	CONSTRAINT rule_PK PRIMARY KEY (rule_id)
)WITHOUT OIDS;


------------------------------------------------------------
-- Table: groups
------------------------------------------------------------
CREATE TABLE public.groups(
	group_id     SERIAL  NOT NULL ,
	group_name   VARCHAR (50) NOT NULL  ,
	CONSTRAINT groups_PK PRIMARY KEY (group_id,group_name)
)WITHOUT OIDS;


------------------------------------------------------------
-- Table: phone
------------------------------------------------------------
CREATE TABLE public.phone(
	phone_id        SERIAL NOT NULL ,
	phone_number    VARCHAR (50) NOT NULL UNIQUE,
	phone_country   VARCHAR (50) NOT NULL ,
	employee_id     VARCHAR (100) NOT NULL  ,
	CONSTRAINT phone_PK PRIMARY KEY (phone_id)
)WITHOUT OIDS;


------------------------------------------------------------
-- Table: email
------------------------------------------------------------
CREATE TABLE public.email(
	email_id      SERIAL NOT NULL ,
	email_value   VARCHAR (50) NOT NULL UNIQUE,
	employee_id   VARCHAR (100) NOT NULL  ,
	CONSTRAINT email_PK PRIMARY KEY (email_id)
)WITHOUT OIDS;


------------------------------------------------------------
-- Table: Planning
------------------------------------------------------------
CREATE TABLE public.Planning(
	planning_id          VARCHAR (100) NOT NULL ,
	planning_beginning   DATE  NOT NULL ,
	planning_ending      DATE  NOT NULL ,
	planning_comment     VARCHAR (2000)  NOT NULL ,
	employee_id          VARCHAR (100) NOT NULL  ,
	CONSTRAINT Planning_PK PRIMARY KEY (planning_id)
)WITHOUT OIDS;


------------------------------------------------------------
-- Table: situated
------------------------------------------------------------
CREATE TABLE public.situated(
	city_id      INT  NOT NULL ,
	address_id   INT  NOT NULL  ,
	CONSTRAINT situated_PK PRIMARY KEY (city_id,address_id)
)WITHOUT OIDS;


------------------------------------------------------------
-- Table: located
------------------------------------------------------------
CREATE TABLE public.located(
	country_id   INT  NOT NULL ,
	city_id      INT  NOT NULL  ,
	CONSTRAINT located_PK PRIMARY KEY (country_id,city_id)
)WITHOUT OIDS;


------------------------------------------------------------
-- Table: lives
------------------------------------------------------------
CREATE TABLE public.lives(
	employee_id   VARCHAR (100) NOT NULL ,
	address_id    INT  NOT NULL  ,
	CONSTRAINT lives_PK PRIMARY KEY (employee_id,address_id)
)WITHOUT OIDS;


------------------------------------------------------------
-- Table: hasRules
------------------------------------------------------------
CREATE TABLE public.hasRules(
	rule_id      INT  NOT NULL ,
	group_id     INT  NOT NULL ,
	group_name   VARCHAR (50) NOT NULL  ,
	CONSTRAINT hasRules_PK PRIMARY KEY (rule_id,group_id,group_name)
)WITHOUT OIDS;


------------------------------------------------------------
-- Table: belong
------------------------------------------------------------
CREATE TABLE public.belong(
	employee_id   VARCHAR (100) NOT NULL ,
	group_id      INT  NOT NULL ,
	group_name    VARCHAR (50) NOT NULL  ,
	CONSTRAINT belong_PK PRIMARY KEY (employee_id,group_id,group_name)
)WITHOUT OIDS;


------------------------------------------------------------
-- Table: team
------------------------------------------------------------
CREATE TABLE public.team(
	team_id            VARCHAR (100) NOT NULL ,
	team_name          VARCHAR (50) NOT NULL UNIQUE ,
	team_description   VARCHAR (2000)  ,
	employee_id        VARCHAR (100) NOT NULL ,
	team_id_parent     VARCHAR (100)   ,
	CONSTRAINT team_PK PRIMARY KEY (team_id)
)WITHOUT OIDS;


------------------------------------------------------------
-- Table: isMember
------------------------------------------------------------
CREATE TABLE public.isMember(
	team_id       VARCHAR (100) NOT NULL ,
	employee_id   VARCHAR (100) NOT NULL  ,
	CONSTRAINT isMember_PK PRIMARY KEY (team_id,employee_id)
)WITHOUT OIDS;



/*
ALTER TABLE public.phone
	ADD CONSTRAINT phone_employee0_FK
	FOREIGN KEY (employee_id)
	REFERENCES public.employee(employee_id);

ALTER TABLE public.email
	ADD CONSTRAINT email_employee0_FK
	FOREIGN KEY (employee_id)
	REFERENCES public.employee(employee_id);

ALTER TABLE public.Planning
	ADD CONSTRAINT Planning_employee0_FK
	FOREIGN KEY (employee_id)
	REFERENCES public.employee(employee_id);

ALTER TABLE public.situated
	ADD CONSTRAINT situated_cities0_FK
	FOREIGN KEY (city_id)
	REFERENCES public.cities(city_id);

ALTER TABLE public.situated
	ADD CONSTRAINT situated_address1_FK
	FOREIGN KEY (address_id)
	REFERENCES public.address(address_id);

ALTER TABLE public.located
	ADD CONSTRAINT located_country0_FK
	FOREIGN KEY (country_id)
	REFERENCES public.country(country_id);

ALTER TABLE public.located
	ADD CONSTRAINT located_cities1_FK
	FOREIGN KEY (city_id)
	REFERENCES public.cities(city_id);

ALTER TABLE public.lives
	ADD CONSTRAINT lives_employee0_FK
	FOREIGN KEY (employee_id)
	REFERENCES public.employee(employee_id);

ALTER TABLE public.lives
	ADD CONSTRAINT lives_address1_FK
	FOREIGN KEY (address_id)
	REFERENCES public.address(address_id);

ALTER TABLE public.hasRules
	ADD CONSTRAINT hasRules_rule0_FK
	FOREIGN KEY (rule_id)
	REFERENCES public.rule(rule_id);

ALTER TABLE public.hasRules
	ADD CONSTRAINT hasRules_groups1_FK
	FOREIGN KEY (group_id,group_name)
	REFERENCES public.groups(group_id,group_name);

ALTER TABLE public.belong
	ADD CONSTRAINT belong_employee0_FK
	FOREIGN KEY (employee_id)
	REFERENCES public.employee(employee_id);

ALTER TABLE public.belong
	ADD CONSTRAINT belong_groups1_FK
	FOREIGN KEY (group_id,group_name)
	REFERENCES public.groups(group_id,group_name);

ALTER TABLE public.team
	ADD CONSTRAINT team_employee0_FK
	FOREIGN KEY (employee_id)
	REFERENCES public.employee(employee_id);

ALTER TABLE public.team
	ADD CONSTRAINT team_team1_FK
	FOREIGN KEY (team_id_parent)
	REFERENCES public.team(team_id);

ALTER TABLE public.isMember
	ADD CONSTRAINT isMember_team0_FK
	FOREIGN KEY (team_id)
	REFERENCES public.team(team_id);

ALTER TABLE public.isMember
	ADD CONSTRAINT isMember_employee1_FK
	FOREIGN KEY (employee_id)
	REFERENCES public.employee(employee_id);
*/








------------------------------------------------------------
--        Script Postgre 
------------------------------------------------------------

CREATE TYPE IdentityDomain AS ENUM ('employee');


------------------------------------------------------------
-- Table: address
------------------------------------------------------------
CREATE TABLE public.address(
	address_id       SERIAL NOT NULL ,
	address_street   VARCHAR (50) NOT NULL ,
	address_number   VARCHAR (50) NOT NULL  ,
	CONSTRAINT address_PK PRIMARY KEY (address_id)
)WITHOUT OIDS;


------------------------------------------------------------
-- Table: cities
------------------------------------------------------------
CREATE TABLE public.cities(
	city_id     SERIAL NOT NULL ,
	city_name   VARCHAR (50) NOT NULL ,
	city_zip    VARCHAR (50) NOT NULL  ,
	CONSTRAINT cities_PK PRIMARY KEY (city_id)
)WITHOUT OIDS;


------------------------------------------------------------
-- Table: country
------------------------------------------------------------
CREATE TABLE public.country(
	country_id     SERIAL NOT NULL ,
	country_name   VARCHAR (50) NOT NULL  ,
	CONSTRAINT country_PK PRIMARY KEY (country_id)
)WITHOUT OIDS;


------------------------------------------------------------
-- Table: employee
------------------------------------------------------------
CREATE TABLE public.employee(
	employee_id          VARCHAR (100) NOT NULL ,
	employee_name        VARCHAR (50) NOT NULL ,
	employee_forename    VARCHAR (50) NOT NULL ,
	employee_birthday    DATE  NOT NULL ,
	employee_hire_date   DATE  NOT NULL ,
	employee_login       VARCHAR (50)  ,
	employee_password    VARCHAR (50)  ,
	employee_niss        VARCHAR (50) NOT NULL  ,
	CONSTRAINT employee_PK PRIMARY KEY (employee_id) ,
	CONSTRAINT employee_AK UNIQUE (employee_niss)
)WITHOUT OIDS;


------------------------------------------------------------
-- Table: rule
------------------------------------------------------------
CREATE TABLE public.rule(
	rule_id            SERIAL NOT NULL ,
	rule_name          VARCHAR (50) NOT NULL ,
	rule_description   VARCHAR (50) NOT NULL ,
	rule_target        VARCHAR (50) NOT NULL  ,
	CONSTRAINT rule_PK PRIMARY KEY (rule_id)
)WITHOUT OIDS;


------------------------------------------------------------
-- Table: groups
------------------------------------------------------------
CREATE TABLE public.groups(
	group_id     INT  NOT NULL ,
	group_name   VARCHAR (50) NOT NULL  ,
	CONSTRAINT groups_PK PRIMARY KEY (group_id,group_name)
)WITHOUT OIDS;


------------------------------------------------------------
-- Table: phone
------------------------------------------------------------
CREATE TABLE public.phone(
	phone_id        SERIAL NOT NULL ,
	phone_number    VARCHAR (50) NOT NULL ,
	phone_country   VARCHAR (50) NOT NULL ,
	employee_id     VARCHAR (100) NOT NULL  ,
	CONSTRAINT phone_PK PRIMARY KEY (phone_id)
)WITHOUT OIDS;


------------------------------------------------------------
-- Table: email
------------------------------------------------------------
CREATE TABLE public.email(
	email_id      SERIAL NOT NULL ,
	email_value   VARCHAR (50) NOT NULL ,
	employee_id   VARCHAR (100) NOT NULL  ,
	CONSTRAINT email_PK PRIMARY KEY (email_id)
)WITHOUT OIDS;


------------------------------------------------------------
-- Table: situated
------------------------------------------------------------
CREATE TABLE public.situated(
	city_id      INT  NOT NULL ,
	address_id   INT  NOT NULL  ,
	CONSTRAINT situated_PK PRIMARY KEY (city_id,address_id)
)WITHOUT OIDS;


------------------------------------------------------------
-- Table: located
------------------------------------------------------------
CREATE TABLE public.located(
	country_id   INT  NOT NULL ,
	city_id      INT  NOT NULL  ,
	CONSTRAINT located_PK PRIMARY KEY (country_id,city_id)
)WITHOUT OIDS;


------------------------------------------------------------
-- Table: lives
------------------------------------------------------------
CREATE TABLE public.lives(
	employee_id   VARCHAR (100) NOT NULL ,
	address_id    INT  NOT NULL  ,
	CONSTRAINT lives_PK PRIMARY KEY (employee_id,address_id)
)WITHOUT OIDS;


------------------------------------------------------------
-- Table: hasRules
------------------------------------------------------------
CREATE TABLE public.hasRules(
	rule_id      INT  NOT NULL ,
	group_id     INT  NOT NULL ,
	group_name   VARCHAR (50) NOT NULL  ,
	CONSTRAINT hasRules_PK PRIMARY KEY (rule_id,group_id,group_name)
)WITHOUT OIDS;


------------------------------------------------------------
-- Table: belong
------------------------------------------------------------
CREATE TABLE public.belong(
	employee_id   VARCHAR (100) NOT NULL ,
	group_id      INT  NOT NULL ,
	group_name    VARCHAR (50) NOT NULL  ,
	CONSTRAINT belong_PK PRIMARY KEY (employee_id,group_id,group_name)
)WITHOUT OIDS;


------------------------------------------------------------
-- Table: team
------------------------------------------------------------
CREATE TABLE public.team(
	team_id            VARCHAR (100) NOT NULL ,
	team_name          VARCHAR (50) NOT NULL ,
	team_description   VARCHAR (2000)  NOT NULL ,
	employee_id        VARCHAR (100) NOT NULL ,
	team_id_parent     VARCHAR (100)   ,
	CONSTRAINT team_PK PRIMARY KEY (team_id)
)WITHOUT OIDS;


------------------------------------------------------------
-- Table: Planning
------------------------------------------------------------
CREATE TABLE public.Planning(
	planning_id          VARCHAR (100) NOT NULL ,
	planning_beginning   TIMESTAMP  NOT NULL ,
	planning_ending      TIMESTAMP  NOT NULL ,
	planning_comment     VARCHAR (2000)  NOT NULL ,
	planning_isBreak     BOOL  NOT NULL DEFAULT false ,
	employee_id          VARCHAR (100) NOT NULL ,
	team_id              VARCHAR (100)   ,
	CONSTRAINT Planning_PK PRIMARY KEY (planning_id)
)WITHOUT OIDS;

------------------------------------------------------------
-- Table: isMember
------------------------------------------------------------
CREATE TABLE public.isMember(
	team_id       VARCHAR (100) NOT NULL ,
	employee_id   VARCHAR (100) NOT NULL  ,
	CONSTRAINT isMember_PK PRIMARY KEY (team_id,employee_id)
)WITHOUT OIDS;




ALTER TABLE public.phone
	ADD CONSTRAINT phone_employee0_FK
	FOREIGN KEY (employee_id)
	REFERENCES public.employee(employee_id);

ALTER TABLE public.email
	ADD CONSTRAINT email_employee0_FK
	FOREIGN KEY (employee_id)
	REFERENCES public.employee(employee_id);

ALTER TABLE public.situated
	ADD CONSTRAINT situated_cities0_FK
	FOREIGN KEY (city_id)
	REFERENCES public.cities(city_id);

ALTER TABLE public.situated
	ADD CONSTRAINT situated_address1_FK
	FOREIGN KEY (address_id)
	REFERENCES public.address(address_id);

ALTER TABLE public.located
	ADD CONSTRAINT located_country0_FK
	FOREIGN KEY (country_id)
	REFERENCES public.country(country_id);

ALTER TABLE public.located
	ADD CONSTRAINT located_cities1_FK
	FOREIGN KEY (city_id)
	REFERENCES public.cities(city_id);

ALTER TABLE public.lives
	ADD CONSTRAINT lives_employee0_FK
	FOREIGN KEY (employee_id)
	REFERENCES public.employee(employee_id);

ALTER TABLE public.lives
	ADD CONSTRAINT lives_address1_FK
	FOREIGN KEY (address_id)
	REFERENCES public.address(address_id);

ALTER TABLE public.hasRules
	ADD CONSTRAINT hasRules_rule0_FK
	FOREIGN KEY (rule_id)
	REFERENCES public.rule(rule_id);

ALTER TABLE public.hasRules
	ADD CONSTRAINT hasRules_groups1_FK
	FOREIGN KEY (group_id,group_name)
	REFERENCES public.groups(group_id,group_name);

ALTER TABLE public.belong
	ADD CONSTRAINT belong_employee0_FK
	FOREIGN KEY (employee_id)
	REFERENCES public.employee(employee_id);

ALTER TABLE public.belong
	ADD CONSTRAINT belong_groups1_FK
	FOREIGN KEY (group_id,group_name)
	REFERENCES public.groups(group_id,group_name);

ALTER TABLE public.team
	ADD CONSTRAINT team_employee0_FK
	FOREIGN KEY (employee_id)
	REFERENCES public.employee(employee_id);

ALTER TABLE public.team
	ADD CONSTRAINT team_team1_FK
	FOREIGN KEY (team_id_parent)
	REFERENCES public.team(team_id);

ALTER TABLE public.Planning
	ADD CONSTRAINT Planning_employee0_FK
	FOREIGN KEY (employee_id)
	REFERENCES public.employee(employee_id);

ALTER TABLE public.Planning
	ADD CONSTRAINT Planning_team1_FK
	FOREIGN KEY (team_id)
	REFERENCES public.team(team_id);

ALTER TABLE public.isMember
	ADD CONSTRAINT isMember_team0_FK
	FOREIGN KEY (team_id)
	REFERENCES public.team(team_id);

ALTER TABLE public.isMember
	ADD CONSTRAINT isMember_employee1_FK
	FOREIGN KEY (employee_id)
	REFERENCES public.employee(employee_id);
