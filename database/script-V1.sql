 ---------------------------------------------------------------
 --        Script Oracle.  
 ---------------------------------------------------------------


------------------------------------------------------------
-- Table: Address
------------------------------------------------------------
CREATE TABLE public.address(
    address_id       SERIAL NOT NULL ,
    address_street   VARCHAR (50) NOT NULL ,
    address_number   VARCHAR (50) NOT NULL  ,
    CONSTRAINT address_PK PRIMARY KEY (address_id)
)WITHOUT OIDS;

/**
 * Test if Tuple (street, number) already exist, if not insert the new tuple
 * return the id of tuple
 * fixture test :  SELECT * FROM f_create_address('rue saint-gilles','412');  
 */
CREATE OR REPLACE FUNCTION f_create_address(v_street TEXT,v_number TEXT)
RETURNS INT AS $$
DECLARE
    v_address_id INT;
BEGIN
    SELECT address_id INTO v_address_id FROM address WHERE address_street=LOWER(TRIM(v_street)) AND address_number=LOWER(TRIM(v_number));
    IF(v_address_id IS NULL) THEN
        INSERT INTO address(address_street,address_number) VALUES ($1,$2) RETURNING address_id INTO v_address_id; 
    END IF;
    return v_address_id;
END $$ LANGUAGE plpgsql;   

-- Transform all entries into lower case and trimmed
CREATE OR REPLACE FUNCTION ft_create_address()
RETURNS trigger AS $$
BEGIN
    new.address_street=LOWER(TRIM(new.address_street));
    new.address_number=LOWER(TRIM(new.address_number));
    RETURN new;
END $$ LANGUAGE plpgsql;
CREATE TRIGGER t_create_address
BEFORE INSERT OR UPDATE ON address
FOR EACH ROW
EXECUTE PROCEDURE ft_create_address();

/**
 * Update an addres
 * fixture test : CALL p_update_address(1,'rue saint-gilles','348');
 */
CREATE OR REPLACE PROCEDURE p_update_address(v_address_id INT,v_street TEXT,v_number TEXT) AS $$
BEGIN
    UPDATE address SET address_street=v_street, address_number=v_number WHERE address_id=v_address_id;
END $$ LANGUAGE plpgsql;



------------------------------------------------------------
-- Table: Cities
------------------------------------------------------------
CREATE TABLE public.cities(
    city_id     SERIAL NOT NULL ,
    city_name   VARCHAR (50) NOT NULL ,
    city_zip    VARCHAR (50) NOT NULL  ,
    CONSTRAINT cities_PK PRIMARY KEY (city_id)
)WITHOUT OIDS;

/**
 * Test if Tuple (name, zip) already exist, if not insert the new tuple
 * return the id of tuple
 * fixture test : SELECT * from f_create_city('Liege','4000');
 */
CREATE OR REPLACE FUNCTION f_create_city(v_name TEXT,v_zip TEXT)
RETURNS INT AS $$
DECLARE
    v_id INT;
BEGIN
    SELECT city_id INTO v_id FROM cities WHERE city_name=LOWER(TRIM(v_name)) AND city_zip=LOWER(TRIM(v_zip)); 
    IF (v_id IS NULL) THEN
        INSERT INTO cities(city_name,city_zip) VALUES($1,$2) RETURNING city_id INTO v_id;
    END IF;
    return v_id;
END $$ LANGUAGE plpgsql;

-- Transform all entries into lower case and trimmed
CREATE OR REPLACE FUNCTION ft_create_city()
RETURNS trigger AS $$
BEGIN
    new.city_name=LOWER(TRIM(new.city_name));
    new.city_zip=LOWER(TRIM(new.city_zip));
    RETURN new;
END $$ LANGUAGE plpgsql;
CREATE TRIGGER t_create_city
BEFORE INSERT OR UPDATE ON cities
FOR EACH ROW
EXECUTE PROCEDURE ft_create_city();

CREATE OR REPLACE PROCEDURE p_update_city(v_id INT,v_city TEXT,v_zip TEXT) AS $$
BEGIN
    UPDATE cities SET city_name=v_city, city_zip=v_zip WHERE city_id=v_id;
END $$ LANGUAGE plpgsql;

------------------------------------------------------------
-- Table: situated (links address and city)
------------------------------------------------------------
CREATE TABLE public.situated(
    city_id      INT  NOT NULL ,
    address_id   INT  NOT NULL  ,
    CONSTRAINT situated_PK PRIMARY KEY (city_id,address_id)
)WITHOUT OIDS;

-- make the link between city and address
CREATE OR REPLACE PROCEDURE link_address_city(v_idAddress INT,v_idCity INT) AS $$
BEGIN
    IF ((SELECT address_id FROM situated WHERE address_id=v_idAddress AND city_id=v_idCity) IS NULL) THEN
        INSERT INTO situated(address_id,city_id) VALUES($1,$2);
    END IF;
END $$ LANGUAGE plpgsql;

-------------------------------------------------------------------

------------------------------------------------------------
-- Table: country
------------------------------------------------------------
CREATE TABLE public.country(
    country_id     SERIAL NOT NULL ,
    country_name   VARCHAR (50) NOT NULL UNIQUE ,
    CONSTRAINT country_PK PRIMARY KEY (country_id)
)WITHOUT OIDS;

/**
 * if country not exist , append it then return id
 * Fixture test : SELECT * FROM f_create_country('BelGique');
 */
CREATE OR REPLACE FUNCTION f_create_country(v_name TEXT)
RETURNS INT AS $$
DECLARE
    v_id INT;
BEGIN
    SELECT country_id INTO v_id FROM country WHERE country_name=LOWER(TRIM(v_name));
    IF (v_id IS NULL) THEN
        INSERT INTO country(country_name) VALUES($1) RETURNING country_id INTO v_id;
    END IF;
    RETURN v_id;
END $$ LANGUAGE plpgsql;

-- Transform all entries into lower case and trimmed
CREATE OR REPLACE FUNCTION ft_create_country()
RETURNS trigger AS $$
BEGIN
    new.country_name=LOWER(TRIM(new.country_name));
    return new;
END $$ LANGUAGE plpgsql;
CREATE TRIGGER t_create_country
BEFORE INSERT OR UPDATE ON country
FOR EACH ROW
EXECUTE PROCEDURE ft_create_country();

------------------------------------------------------------
-- Table: located
------------------------------------------------------------
CREATE TABLE public.located(
    country_id   INT  NOT NULL ,
    city_id      INT  NOT NULL  ,
    CONSTRAINT located_PK PRIMARY KEY (country_id,city_id)
)WITHOUT OIDS;

--Make the link between city and country
CREATE OR REPLACE PROCEDURE link_city_country(v_idCity INT,v_idCountry INT) AS $$
BEGIN
    IF ((SELECT city_id FROM located WHERE city_id=v_idcity AND country_id=v_idCountry) IS NULL) THEN
        INSERT INTO located(city_id,country_id) VALUES($1,$2);
    END IF;
END $$ LANGUAGE plpgsql;



/**
 * Create a postal address
 */
CREATE OR REPLACE PROCEDURE p_create_postalAddress(
    v_street TEXT,v_number TEXT,
    v_city TEXT,v_zip TEXT,
    v_country TEXT)
AS $$
DECLARE
    v_idAddress INT;
    v_idCity INT;
    v_idCountry INT;
BEGIN
    SELECT * INTO v_idCountry FROM f_create_country(v_country);
    SELECT * INTO v_idCity FROM f_create_city(v_city,v_zip);
    SELECT * INTO v_idAddress FROM f_create_address(v_street,v_number);

    IF (v_idAddress IS NULL OR v_idCity IS NULL OR v_idCountry IS NULL) THEN
        ROLLBACK;
    ELSE
        CALL link_address_city(v_idAddress,v_idCity);
        CALL link_city_country(v_idCity,v_idCountry);
    END IF;
END $$ LANGUAGE plpgsql;


------------------------------------------------------------
-- Table: phone
------------------------------------------------------------
CREATE TABLE public.phone(
    phone_id        SERIAL NOT NULL ,
    phone_number    VARCHAR (50) NOT NULL ,
    phone_country   VARCHAR (50) NOT NULL ,
    employee_id       VARCHAR (100) NOT NULL  ,
    CONSTRAINT phone_PK PRIMARY KEY (phone_id)
)WITHOUT OIDS;

CREATE OR REPLACE FUNCTION f_append_phoneNumber_for(v_employeeId INT, v_number TEXT, v_country TEXT)
RETURNS INT AS $$
DECLARE
    v_phoneId INT;
BEGIN
    SELECT phone_id INTO v_phoneId FROM phone WHERE phone_number=v_number;
    IF (v_phoneId IS NULL) THEN
        INSERT INTO phone(employee_id,phone_number,phone_country) VALUES($1,$2,$3) RETURNING phone_id INTO v_phoneId;
    END IF;
    RETURN v_phoneId;
END $$ LANGUAGE plpgsql;


------------------------------------------------------------
-- Table: email
------------------------------------------------------------
CREATE TABLE public.email(
    email_id      SERIAL NOT NULL ,
    email_value   VARCHAR (50) NOT NULL UNIQUE,
    employee_id     VARCHAR (100) NOT NULL  ,
    CONSTRAINT email_PK PRIMARY KEY (email_id)
)WITHOUT OIDS;


CREATE OR REPLACE FUNCTION f_append_email_for(v_employeeId INT,v_email TEXT)
RETURNS INT AS $$
DECLARE
    v_emailId INT;
BEGIN
    SELECT email_id INTO v_emailId FROM email WHERE email_value=v_email;
    IF(v_emailId IS NULL) THEN
        INSERT INTO email(employee_id,email_value) VALUES(v_employeeId,v_email) RETURNING email_id INTO v_emailId;
    END IF;
    RETURN v_emailId;
END $$ LANGUAGE plpgsql;

CREATE TABLE public.employee(
    employee_id          VARCHAR (100) NOT NULL ,
    employee_niss        VARCHAR (50) NOT NULL ,
    employee_name        VARCHAR (50) NOT NULL ,
    employee_forename    VARCHAR (50) NOT NULL ,
    employee_birthday    VARCHAR (50) NOT NULL ,
    employee_hire_date   DATE  NOT NULL ,
    employee_login       VARCHAR (50)  ,
    employee_password    VARCHAR (50)   ,
    CONSTRAINT employee_PK PRIMARY KEY (employee_id)
)WITHOUT OIDS;

/**
 * Fixtures test :SELECT * FROM f_create_employee('aasa','11.11.11-111.12','jOhn','does',TO_DATE('1993-11-20','yyyy-mm-dd'));
 */
CREATE OR REPLACE FUNCTION f_create_employee(
    v_id TEXT,
    v_niss TEXT,
    v_name TEXT,
    v_forname TEXT,
    v_birthday DATE,
    v_hire_date Date DEFAULT CURRENT_DATE
    )
RETURNS TEXT AS $$
DECLARE
    v_employeeId TEXT;
BEGIN
    SELECT employee_id INTO v_employeeId FROM employee WHERE employee_id=v_id OR employee_niss=v_niss;
    IF( v_employeeId IS NULL) THEN
        INSERT INTO employee (employee_id,employee_niss,employee_name,employee_forename,employee_birthday,employee_hire_date) VALUES ($1,$2,$3,$4,$5,$6) 
        RETURNING employee_id INTO v_employeeId;
    END IF;
    RETURN v_employeeId;
END $$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION ft_create_employee()
RETURNS trigger AS $$
BEGIN
    new.employee_name=LOWER(TRIM(new.employee_name));
    new.employee_forename=LOWER(TRIM(new.employee_forename));
    RETURN new;
END $$ LANGUAGE plpgsql;
CREATE TRIGGER t_create_employee
BEFORE INSERT OR UPDATE ON employee
FOR EACH ROW
EXECUTE PROCEDURE ft_create_employee();



CREATE OR REPLACE PROCEDURE p_update_employee(
    v_id TEXT,
    v_niss TEXT,
    v_name TEXT,
    v_forname TEXT,
    v_birthday DATE,
    v_hire_date Date DEFAULT CURRENT_DATE
    )AS $$ 
BEGIN
    IF((SELECT employee_id FROM employee WHERE employee_id=v_id) IS NOT NULL) THEN
        UPDATE employee SET employee_niss=v_niss, employee_name=v_name, employee_forename=v_forname,
            employee_birthday=v_birthday,employee_hire_date=v_hire_date
            WHERE employee_id=v_id;  
    END IF;
END $$ LANGUAGE plpgsql;

-- if update the niss and the new niss is already used then the old niss stays
CREATE OR REPLACE FUNCTION ft_update_employee()
RETURNS trigger AS $$
BEGIN
    IF(old.employee_niss != new.employee_niss AND (SELECT employee_id FROM employee WHERE employee_niss=new.employee_niss) IS NOT NULL) THEN
        new.employee_niss=old.employee_niss;
    END IF;

    IF((SELECT employee_id FROM employee WHERE employee_login=new.employee_login) IS NOT NULL) THEN
        new.employee_login=old.employee_login;
    END IF;

    RETURN new;
END $$ LANGUAGE plpgsql;
CREATE TRIGGER t_update_employee
BEFORE UPDATE ON employee
FOR EACH ROW
EXECUTE PROCEDURE ft_update_employee();


CREATE OR REPLACE PROCEDURE p_append_account_for_employee(v_employeeId TEXT, v_login TEXT,v_password TEXT) AS $$
BEGIN
    IF( (SELECT employee_id FROM employee WHERE employee_id=v_employeeId) IS NOT NULL AND 
         (SELECT employee_login FROM employee WHERE employee_login=v_login) IS NULL ) THEN
        UPDATE employee SET employee_login=v_login,employee_password=v_password WHERE employee_id=v_employeeId;
    END IF;
END $$ LANGUAGE plpgsql

------------------------------------------------------------
-- Table: lives
------------------------------------------------------------
CREATE TABLE public.lives(
    employee_id   VARCHAR (100) NOT NULL ,
    address_id    INT  NOT NULL  ,
    CONSTRAINT lives_PK PRIMARY KEY (employee_id,address_id)
)WITHOUT OIDS;

CREATE OR REPLACE PROCEDURE link_employee_address(v_idEmployee TEXT,v_idAddress INT)
AS $$ 
BEGIN
    IF((select employee_id FROM employee WHERE employee_id=v_idEmployee) IS NOT NULL AND (SELECT address_id FROM address WHERE address_id=v_idAddress) IS NOT NULL) THEN
        INSERT INTO lives(employee_id,address_id) VALUES($1,$2);
    END IF;
END $$ LANGUAGE plpgsql; 


-------------------------------------------------------------------------------------


------------------------------------------------------------
-- Table: group
------------------------------------------------------------
CREATE TABLE public.groups(
    group_id     SERIAL  NOT NULL ,
    group_name   VARCHAR (50) NOT NULL UNIQUE ,
    CONSTRAINT groups_PK PRIMARY KEY (group_id,group_name)
)WITHOUT OIDS;

CREATE OR REPLACE FUNCTION f_create_group(v_name TEXT)
RETURNS INT AS $$
DECLARE
    v_id INT;
BEGIN
    SELECT group_id INTO v_id FROM groups WHERE group_name = v_name;
    IF(v_id IS NULL) THEN
        INSERT INTO groups(group_name) VALUES($1) RETURNING group_id INTO v_id;
    END IF;
    RETURN v_id;
END $$ LANGUAGE plpgsql; 


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

/**
 * BEFORE INSERTION test if tuple (rule_name , target) already exists.
 */
CREATE OR REPLACE FUNCTION f_create_rule(v_name TEXT,v_description TEXT,v_target TEXT)
RETURNS INT AS $$
DECLARE
    v_id INT;
BEGIN
    SELECT rule_id INTO v_id FROM rule WHERE rule_name=v_name AND rule_target=v_target;
    IF(v_id IS NULL) THEN
        INSERT INTO rule(rule_name,rule_description,rule_target) VALUES($1,$2,$3) RETURNING rule_id INTO v_id;
    END IF;
    RETURN v_id;
END $$ LANGUAGE plpgsql; 

------------------------------------------------------------
-- Table: hasRules
------------------------------------------------------------
CREATE TABLE public.hasRules(
    rule_id      INT  NOT NULL ,
    group_id     INT  NOT NULL ,
    group_name   VARCHAR (50) NOT NULL  ,
    CONSTRAINT hasRules_PK PRIMARY KEY (rule_id,group_id,group_name)
)WITHOUT OIDS;

CREATE OR REPLACE PROCEDURE p_append_rule_to_group(v_groupId INT ,v_roleId INT) AS $$
DECLARE
    v_groupName TEXT;
BEGIN
    SELECT group_name INTO v_groupName FROM groups WHERE group_id=v_groupId;
    INSERT INTO hasRules(group_id,rule_id,group_name) VALUES ($1,$2,v_groupName);
END $$ LANGUAGE plpgsql; 


------------------------------------------------------------
-- Table: belong
------------------------------------------------------------
CREATE TABLE public.belong(
    employee_id   VARCHAR (100) NOT NULL ,
    group_id      INT  NOT NULL ,
    group_name    VARCHAR (50) NOT NULL  ,
    CONSTRAINT belong_PK PRIMARY KEY (employee_id,group_id,group_name)
)WITHOUT OIDS;

CREATE OR REPLACE PROCEDURE p_append_member_to_group(v_employeeId TEXT,v_groupName TEXT) AS $$
DECLARE
    v_groupId INT;
BEGIN
    SELECT group_id INTO v_groupId FROM groups WHERE group_name=v_groupName;
    IF(v_groupId IS NOT NULL) THEN
        INSERT INTO belong(employee_id,group_name,group_id) VALUES($1,$2,v_groupId);
    END IF; 
END $$ LANGUAGE plpgsql;

CREATE OR REPLACE PROCEDURE p_remove_member_to_group(v_employeeId TEXT,v_groupName TEXT) AS $$
DECLARE
    v_groupId INT;
BEGIN
    SELECT group_id INTO v_groupId FROM groups WHERE group_name=v_groupName;
    IF(v_groupId IS NOT NULL) THEN
        DELETE FROM belong WHERE employee_id=v_employeeId AND group_id=v_groupId;
    END IF; 
END $$ LANGUAGE plpgsql;


CREATE TYPE groupMember AS(
    employee_id TEXT,
    member_name TEXT
);


/**
FIXTURE :
    SELECT * FROM f_create_group('group-a');
    SELECT * from f_create_rule('rule-a','tse','*');
    SELECT * from f_create_rule('rule-b','tse','*');
    SELECT * from f_create_rule('rule-c','tse','*');
    Call p_append_rule_to_group(1,1);
    Call p_append_rule_to_group(1,2);
**/
CREATE OR REPLACE FUNCTION f_find_rule_of_group(v_groupId INT) RETURNS SETOF rule AS $$
BEGIN
    RETURN QUERY (SELECT rule.*  FROM rule 
    INNER JOIN hasRules USING(rule_id)
    WHERE group_id=v_groupId);
END $$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION f_find_rule_of_group(v_groupName TEXT) RETURNS SETOF rule AS $$
BEGIN
    RETURN QUERY (SELECT rule.*  FROM rule 
    INNER JOIN hasRules USING(rule_id)
    WHERE group_name=v_groupName);
END $$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION f_find_member_of_group(v_groupId INT) RETURNS SETOF groupMember AS $$
DECLARE
    row groupMember%rowtype;
BEGIN
    for row in (SELECT employee_id,employee_login FROM employee
        INNER JOIN BELONG USING(employee_id)
        INNER JOIN GROUPS USING(group_id)
        WHERE group_id=v_groupId) 
    LOOP RETURN NEXT row;
    END LOOP;
    return;
END $$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION f_find_group_by_name(v_name TEXT) RETURNS setof groups AS $$
BEGIN
    return query( select * from groups WHERE group_name=v_name);
END $$ LANGUAGE plpgsql;





------------------------------------------------------------
-- Table: team
------------------------------------------------------------
CREATE TABLE public.team(
    team_id             VARCHAR (100) NOT NULL ,
    team_name           VARCHAR (50) NOT NULL ,
    employee_id         VARCHAR (100) NOT NULL ,
    team_description    VARCHAR (2000) ,
    team_id_hasParent   VARCHAR (100)   ,
    CONSTRAINT team_PK PRIMARY KEY (team_id)
)WITHOUT OIDS;

CREATE OR REPLACE FUNCTION f_create_team(
    v_teamId TEXT,
    v_name TEXT,
    v_leader TEXT,
    v_description TEXT DEFAULT NULL,
    v_parentId TEXT DEFAULT NULL)
RETURNS TEXT AS $$
DECLARE 
    v_id TEXT;
BEGIN
    SELECT team_id INTO v_id FROM team WHERE team_id=v_teamId ;
    IF(v_id IS NULL) THEN
        INSERT INTO team(team_id,team_name,employee_id,team_description,team_id_hasParent) VALUES($1,$2,$3,$4,$5) RETURNING team_id INTO v_id;
    END IF;
    RETURN v_id;
END $$ LANGUAGE plpgsql;

    




------------------------------------------------------------
-- Table: isMember
------------------------------------------------------------
CREATE TABLE public.isMember(
    team_id       VARCHAR (100) NOT NULL ,
    employee_id   VARCHAR (100) NOT NULL  ,
    CONSTRAINT isMember_PK PRIMARY KEY (team_id,employee_id)
)WITHOUT OIDS;

CREATE OR REPLACE PROCEDURE p_append_to_team(v_employeeId TEXT,v_teamId TEXT) AS $$
BEGIN
    INSERT INTO isMember(employee_id,team_id) VALUES($1,$2);
END $$ LANGUAGE plpgsql;



ALTER TABLE public.phone
    ADD CONSTRAINT phone_employee0_FK
    FOREIGN KEY (employee_id)
    REFERENCES public.employee(employee_id);

ALTER TABLE public.email
    ADD CONSTRAINT email_employee0_FK
    FOREIGN KEY (employee_id)
    REFERENCES public.employee(employee_id);


ALTER TABLE public.situated
    ADD CONSTRAINT situated_Cities0_FK
    FOREIGN KEY (city_id)
    REFERENCES public.Cities(city_id);

ALTER TABLE public.situated
    ADD CONSTRAINT situated_Address1_FK
    FOREIGN KEY (address_id)
    REFERENCES public.Address(address_id);

ALTER TABLE public.located
    ADD CONSTRAINT located_countries0_FK
    FOREIGN KEY (country_id)
    REFERENCES public.country(country_id);

ALTER TABLE public.located
    ADD CONSTRAINT located_Cities1_FK
    FOREIGN KEY (city_id)
    REFERENCES public.Cities(city_id);

ALTER TABLE public.lives
    ADD CONSTRAINT lives_employee0_FK
    FOREIGN KEY (employee_id)
    REFERENCES public.employee(employee_id);

ALTER TABLE public.lives
    ADD CONSTRAINT lives_Address1_FK
    FOREIGN KEY (address_id)
    REFERENCES public.Address(address_id);

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
    FOREIGN KEY (team_id_hasParent)
    REFERENCES public.team(team_id);

ALTER TABLE public.isMember
    ADD CONSTRAINT isMember_team0_FK
    FOREIGN KEY (team_id)
    REFERENCES public.team(team_id);

ALTER TABLE public.isMember
    ADD CONSTRAINT isMember_employee1_FK
    FOREIGN KEY (employee_id)
    REFERENCES public.employee(employee_id);



/***********
CREATE OR REPLACE FUNCTION return and fill array(v_groupId INT) RETURNS rule[] AS $$
DECLARE
 v_row rule;
 v_array rule[];
BEGIN
    
    FOR v_row IN
        SELECT rule.*  FROM rule INNER JOIN hasRules USING(rule_id) WHERE group_id=v_groupId
    LOOP
        SELECT array_append(v_array,v_row) INTO v_array;
    END LOOP;
    return v_array;
END $$ LANGUAGE plpgsql;

select * from f_find_rule_of_groupTOArray(1);
********************/