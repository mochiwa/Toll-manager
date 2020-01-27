CREATE OR REPLACE FUNCTION f_append_email(v_employeeId TEXT,v_email TEXT)
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


CREATE OR REPLACE FUNCTION find_emails(v_employeeId TEXT)
RETURNS SETOF email AS $$
BEGIN
	RETURN QUERY (
		SELECT * FROM email where employee_id=v_employeeId
	);
END $$ LANGUAGE plpgsql;


CREATE OR REPLACE PROCEDURE p_dislink_employee_email(v_email TEXT)
AS $$
BEGIN
	DELETE FROM email WHERE email_value=v_email;
END $$ LANGUAGE plpgsql;

-- TRIGGER employee must have atleast one phone ,then if delete last phone known it canceled the process
CREATE OR REPLACE FUNCTION ft_oneEmailRequired()
RETURNS TRIGGER AS $$
BEGIN
    IF((SELECT count(*) FROM email WHERE employee_id=old.employee_id) = 0 ) THEN
		INSERT INTO email(employee_id,email_id,email_value) VALUES
			(old.employee_id,old.email_id,old.email_value);
    END IF;
	RETURN OLD;
END $$ LANGUAGE plpgsql;
CREATE TRIGGER t_oneEmailRequired
AFTER DELETE OR UPDATE ON email
FOR EACH ROW
EXECUTE PROCEDURE ft_oneEmailRequired();

-------------------------------------------------------------------------------------------------------------------------------

CREATE OR REPLACE FUNCTION f_append_phoneNumber(v_employeeId TEXT, v_number TEXT, v_country TEXT)
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

CREATE OR REPLACE FUNCTION find_phones(v_employeeId TEXT)
RETURNS SETOF phone AS $$
BEGIN
    RETURN QUERY (
        SELECT * FROM phone where employee_id=v_employeeId
    );
END $$ LANGUAGE plpgsql;



CREATE OR REPLACE PROCEDURE p_dislink_employee_phone(v_employeeID TEXT,v_phone TEXT)
AS $$
DECLARE
    v_idPhone INT;
BEGIN
    DELETE FROM phone WHERE phone_number=v_phone AND employee_id=v_employeeID;
END $$ LANGUAGE plpgsql;

-- TRIGGER employee must have atleast one phone ,then if delete last phone known it canceled the process
CREATE OR REPLACE FUNCTION ft_onePhoneRequired()
RETURNS TRIGGER AS $$
BEGIN
    IF((SELECT count(*) FROM phone WHERE employee_id=old.employee_id) = 0 ) THEN
        INSERT INTO phone(employee_id,phone_id,phone_number,phone_country) VALUES
            (old.employee_id,old.phone_id,old.phone_number,old.phone_country);
    END IF;
    RETURN OLD;
END $$ LANGUAGE plpgsql;
CREATE TRIGGER t_onePhoneRequired
AFTER DELETE OR UPDATE ON phone
FOR EACH ROW
EXECUTE PROCEDURE ft_onePhoneRequired();



-----------------------------------------------------------------------------------------------------------------------------------


CREATE OR REPLACE FUNCTION f_create_employee(
    v_id TEXT,
    v_niss TEXT,
    v_name TEXT,
    v_forname TEXT,
    v_birthday DATE,
    v_hire_date DATE DEFAULT CURRENT_DATE
    )
RETURNS TEXT AS $$
DECLARE
    v_employeeId TEXT;
BEGIN
    SELECT employee_id INTO v_employeeId FROM employee WHERE employee_id=v_id OR employee_niss=v_niss;
    IF( v_employeeId IS NULL) THEN
        INSERT INTO employee (employee_id,employee_niss,employee_name,employee_forename,employee_birthday,employee_hire_date)
         VALUES ($1,$2,$3,$4,$5,$6) RETURNING employee_id INTO v_employeeId;
    END IF;
    RETURN v_employeeId;
END $$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION ft_employee_entriesToLowerCase()
RETURNS trigger AS $$
BEGIN
    new.employee_name=LOWER(TRIM(new.employee_name));
    new.employee_forename=LOWER(TRIM(new.employee_forename));
    RETURN new;
END $$ LANGUAGE plpgsql;
CREATE TRIGGER t_employee_entriesToLowerCase
BEFORE INSERT OR UPDATE ON employee
FOR EACH ROW
EXECUTE PROCEDURE ft_employee_entriesToLowerCase();


CREATE OR REPLACE PROCEDURE p_update_employee(
    v_id TEXT,
    v_niss TEXT,
    v_name TEXT,
    v_forname TEXT,
    v_birthday DATE
    )AS $$ 
BEGIN
    IF((SELECT employee_id FROM employee WHERE employee_id=v_id) IS NOT NULL) THEN
        UPDATE employee SET employee_niss=v_niss, employee_name=v_name, employee_forename=v_forname,employee_birthday=v_birthday
        WHERE employee_id=v_id;  
    END IF;
END $$ LANGUAGE plpgsql;


-- if update the niss and the new niss is already used then the old niss stays
-- if login is already used then the old login stay
CREATE OR REPLACE FUNCTION ft_update_employee()
RETURNS trigger AS $$
BEGIN
    IF( (old.employee_niss != new.employee_niss) AND
       ( (SELECT * from find_employee_by_niss(new.employee_niss)) IS NOT NULL))THEN
        new.employee_niss=old.employee_niss;
    END IF;

    IF((SELECT * FROM find_user_by_login(new.employee_login)) IS NOT NULL) THEN
        new.employee_login=old.employee_login;
    END IF;
    RETURN new;
END $$ LANGUAGE plpgsql;


CREATE OR REPLACE PROCEDURE p_delete_employee(v_employeeId TEXT) AS $$
BEGIN
    UPDATE employee SET employee_isDeleted=true WHERE employee_id=v_employeeId;
END $$ LANGUAGE plpgsql;


-----------------------------------------------------------------------------------------------------------------------------------


----------------------------------------------------------------------------------------------------------------------
CREATE OR REPLACE PROCEDURE p_append_account_for_employee(v_employeeId TEXT, v_login TEXT,v_password TEXT) AS $$
BEGIN
    IF( (SELECT employee_id FROM employee WHERE employee_id=v_employeeId) IS NOT NULL AND 
         (SELECT employee_login FROM employee WHERE employee_login=v_login) IS NULL ) THEN
        UPDATE employee SET employee_login=v_login,employee_password=v_password WHERE employee_id=v_employeeId AND employee_isDeleted=false;
    END IF;
END $$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION find_employee_by_id(v_idEmployee TEXT) 
RETURNS employee  AS $$
DECLARE
    v_employee employee%ROWTYPE;
BEGIN
    SELECT * INTO v_employee FROM employee WHERE employee_id=v_idEmployee AND employee_isDeleted=false;
    RETURN v_employee;
END $$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION find_employee_by_niss(v_niss TEXT) 
RETURNS employee  AS $$
DECLARE
    v_employee employee%ROWTYPE;
BEGIN
    SELECT * INTO v_employee FROM employee WHERE employee_niss=v_niss AND employee_isDeleted=false;
    RETURN v_employee;
END $$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION find_employee_by_email(v_email TEXT) 
RETURNS employee  AS $$
DECLARE
    v_employee employee%ROWTYPE;
BEGIN
    SELECT * INTO v_employee FROM employee 
    INNER JOIN email USING (employee_id)
    WHERE email_value=v_email AND employee_isDeleted=false;
    RETURN v_employee;
END $$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION find_employee_by_phone(v_phone TEXT) 
RETURNS employee  AS $$
DECLARE
    v_employee employee%ROWTYPE;
BEGIN
    SELECT * INTO v_employee FROM employee 
    INNER JOIN phone USING (employee_id)
    WHERE phone_number=v_phone AND employee_isDeleted=false;
    RETURN v_employee;
END $$ LANGUAGE plpgsql;

------------------------------------------------------------------------

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

CREATE OR REPLACE FUNCTION find_group_by_name(v_name TEXT) 
RETURNS GROUPS AS $$ 
DECLARE
    v_group groups%ROWTYPE;
BEGIN
    SELECT * INTO v_group FROM GROUPS WHERE group_name=v_name;
    RETURN v_group;
END $$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION find_rule_of_group(v_groupName TEXT) RETURNS SETOF rule AS $$
BEGIN
    RETURN QUERY (SELECT rule.*  FROM rule 
    INNER JOIN hasRules USING(rule_id)
    WHERE group_name=v_groupName);
END $$ LANGUAGE plpgsql;


CREATE TYPE groupMember AS(
    employee_id TEXT,
    member_name TEXT
);
CREATE OR REPLACE FUNCTION find_member_of_group(v_groupName TEXT) RETURNS SETOF groupMember AS $$
DECLARE
    v_groupId INT;
    row groupMember%rowtype;
BEGIN
    SELECT group_id into v_groupId FROM groups WHERE group_name=v_groupName;

    for row in (SELECT employee_id,employee_login FROM employee
        INNER JOIN BELONG USING(employee_id)
        INNER JOIN GROUPS USING(group_id)
        WHERE group_id=v_groupId) 
    LOOP RETURN NEXT row;
    END LOOP;
    return;
END $$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION find_groupName_where_belong(v_employeeId TEXT)
RETURNS SETOF TEXT AS $$
DECLARE
    groupsName record;
    rec TEXT;
BEGIN
    FOR rec IN (
        SELECT groups.group_name FROM groups
        INNER JOIN belong USING(group_id)
        WHERE employee_id=v_employeeId
    ) LOOP
    RETURN NEXT rec;
    END LOOP;
END $$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION find_all_groupName()
RETURNS SETOF TEXT AS $$
DECLARE
    groupsName record;
    rec TEXT;
BEGIN
    FOR rec IN (
        SELECT groups.group_name FROM groups
    ) LOOP
    RETURN NEXT rec;
    END LOOP;
END $$ LANGUAGE plpgsql;

---------------------------------------------------------------------------------------------------------------------

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

CREATE OR REPLACE PROCEDURE p_append_rule_to_group(v_group TEXT ,v_ruleId INT) AS $$
DECLARE
    v_groupId INT;
BEGIN
    SELECT group_id INTO v_groupId FROM groups WHERE group_name=v_group;
    IF((SELECT COUNT(*) FROM hasRules WHERE group_id=v_groupId AND rule_id=v_ruleId)=0 )THEN
        INSERT INTO hasRules(group_id,group_name,rule_id) VALUES (v_groupId,$1,$2);
    END IF;
END $$ LANGUAGE plpgsql; 


CREATE OR REPLACE PROCEDURE p_remove_rule_to_group(v_group TEXT ,v_ruleName TEXT,v_ruleTarget TEXT) AS $$
DECLARE
    v_ruleId INT;
BEGIN
    SELECT rule_id INTO v_ruleId FROM rule WHERE rule_name=v_ruleName AND rule_target=v_ruleTarget;
    IF(v_ruleId IS NOT NULL) THEN
        DELETE FROM hasRules WHERE group_name=v_group AND rule_id=v_ruleId;
    END IF;
END $$ LANGUAGE plpgsql; 


---------------------------------------------------------------------------------------------------------------------

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

/**
 * Transform all entries into lower case and trimmed
 */
CREATE OR REPLACE FUNCTION ft_address_entriesToLowerCase()
RETURNS trigger AS $$
BEGIN
    new.address_street=LOWER(TRIM(new.address_street));
    new.address_number=LOWER(TRIM(new.address_number));
    RETURN new;
END $$ LANGUAGE plpgsql;
CREATE TRIGGER t_address_entriesToLowerCase
BEFORE INSERT OR UPDATE ON address
FOR EACH ROW
EXECUTE PROCEDURE ft_address_entriesToLowerCase();

/**
 * Update an addres
 * fixture test : CALL p_update_address(1,'rue saint-gilles','348');
 */
CREATE OR REPLACE PROCEDURE p_update_address(v_address_id INT,v_street TEXT,v_number TEXT) AS $$
BEGIN
    UPDATE address SET address_street=v_street, address_number=v_number WHERE address_id=v_address_id;
END $$ LANGUAGE plpgsql;





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
CREATE OR REPLACE FUNCTION ft_city_entriesToLowerCase()
RETURNS trigger AS $$
BEGIN
    new.city_name=LOWER(TRIM(new.city_name));
    new.city_zip=LOWER(TRIM(new.city_zip));
    RETURN new;
END $$ LANGUAGE plpgsql;
CREATE TRIGGER t_city_entriesToLowerCase
BEFORE INSERT OR UPDATE ON cities
FOR EACH ROW
EXECUTE PROCEDURE ft_city_entriesToLowerCase();

CREATE OR REPLACE PROCEDURE p_update_city(v_id INT,v_city TEXT,v_zip TEXT) AS $$
BEGIN
    UPDATE cities SET city_name=v_city, city_zip=v_zip WHERE city_id=v_id;
END $$ LANGUAGE plpgsql;

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
CREATE OR REPLACE FUNCTION ft_country_entryToLowerCase()
RETURNS trigger AS $$
BEGIN
    new.country_name=LOWER(TRIM(new.country_name));
    return new;
END $$ LANGUAGE plpgsql;
CREATE TRIGGER t_country_entryToLowerCase
BEFORE INSERT OR UPDATE ON country
FOR EACH ROW
EXECUTE PROCEDURE ft_country_entryToLowerCase();



/** 
 *make the link between city and address
 */
CREATE OR REPLACE PROCEDURE link_address_city(v_idAddress INT,v_idCity INT) AS $$ 
BEGIN
    IF ((SELECT address_id FROM situated WHERE address_id=v_idAddress AND city_id=v_idCity) IS NULL) THEN
        INSERT INTO situated(address_id,city_id) VALUES($1,$2);
    END IF;
END $$ LANGUAGE plpgsql;

--Make the link between city and country
CREATE OR REPLACE PROCEDURE link_city_country(v_idCity INT,v_idCountry INT) AS $$
BEGIN
    IF ((SELECT city_id FROM located WHERE city_id=v_idcity AND country_id=v_idCountry) IS NULL) THEN
        INSERT INTO located(city_id,country_id) VALUES($1,$2);
    END IF;
END $$ LANGUAGE plpgsql;

--Make the link between employee and address
CREATE OR REPLACE PROCEDURE link_employee_address(v_idEmployee TEXT,v_idAddress INT)AS $$ 
BEGIN
    IF((select employee_id FROM employee WHERE employee_id=v_idEmployee) IS NOT NULL
        AND (SELECT address_id FROM address WHERE address_id=v_idAddress) IS NOT NULL
        AND (SELECT COUNT(*) FROM lives WHERE  employee_id=v_idEmployee AND address_id=v_idAddress)=0) THEN
        INSERT INTO lives(employee_id,address_id) VALUES($1,$2);
    END IF;
END $$ LANGUAGE plpgsql;  
-- Remove address from an employee
CREATE OR REPLACE PROCEDURE p_dislink_employee_address(v_employeeID TEXT,v_street TEXT,v_number TEXT)
AS $$
DECLARE
    v_idAddress INT;
BEGIN
    SELECT address_id INTO v_idAddress FROM address WHERE address_street=v_street AND address_number=v_number;
    IF(v_idAddress IS NOT NULL)THEN
        DELETE FROM lives WHERE (address_id=v_idAddress AND employee_id=v_employeeId);
    END IF;
END $$ LANGUAGE plpgsql;
-- TRIGGER employee must have atleast one address ,then if delete last address know it canceled the process
CREATE OR REPLACE FUNCTION ft_oneAddressRequired()
RETURNS TRIGGER AS $$
BEGIN
    IF((SELECT count(*) FROM lives WHERE employee_id=old.employee_id) =0) THEN
        INSERT INTO LIVES(address_id,employee_id) VALUES(old.address_id,old.employee_id);
    END IF;
    RETURN OLD;
END $$ LANGUAGE plpgsql;
CREATE TRIGGER t_oneAddressRequired
AFTER DELETE OR UPDATE ON lives
FOR EACH ROW
EXECUTE PROCEDURE ft_oneAddressRequired();


-- create a complet postal address with linking
CREATE OR REPLACE FUNCTION f_create_postalAddress(
    v_street TEXT,v_number TEXT,
    v_city TEXT,v_zip TEXT,
    v_country TEXT)
    RETURNS INT AS $$
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
    RETURN v_idAddress;
END $$ LANGUAGE plpgsql;

------------------------------------------------------------------------------------------------
--features:
/*
Call p_create_postalAddress('rue saint-gilles','412','liege','4000','belgique');
Call p_create_postalAddress('rue saint-gerond','12','liege','4000','belgique');
Call p_create_postalAddress('rue saint-gilles','10','liege','4000','belgique');
Call p_create_postalAddress('rue saint-gerond','221','liege','4000','belgique');
*/


-- Type that represent a postal address
CREATE TYPE postal_address AS(
    house_number TEXT,
    street TEXT,
    city TEXT,
    zip TEXT,
    country TEXT
);
-- return list of postal address linked to the employeeId
CREATE OR REPLACE FUNCTION find_postal_addresses(v_employeeId TEXT) 
RETURNS SETOF postal_address AS $$
DECLARE
    addresses record;
    rec postal_address;
BEGIN
    FOR rec IN SELECT
         address_number,address_street,city_name,city_zip,country_name FROM lives
        INNER JOIN address USING(address_id)
        INNER JOIN situated USING(address_id)
        INNER JOIN cities USING(city_id)
        INNER JOIN located USING(city_id)
        INNER JOIN country USING(country_id)
        WHERE employee_id=v_employeeId
    LOOP
    RETURN next rec;
    END LOOP;
END $$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION f_create_planning(v_planningId TEXT,v_begin TIMESTAMP,v_end TIMESTAMP,v_comment TEXT,v_employeeId TEXT,v_teamId TEXT)
RETURNS TEXT AS $$
DECLARE
    v_id TEXT;
BEGIN
    SELECT planning_id INTO v_id FROM planning WHERE planning_id=v_planningId;
    IF(v_id IS NULL) THEN
        INSERT INTO planning(planning_id,planning_beginning,planning_ending,planning_comment,employee_id,team_id) VALUES($1,$2,$3,$4,$5,$6) RETURNING planning_id INTO v_id;
    END IF;
    RETURN v_id;    
END $$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION f_get_planning_by_id(v_planningId TEXT)
RETURNS planning AS $$
DECLARE
    v_planning planning%ROWTYPE;
BEGIN
    SELECT * INTO v_planning FROM planning WHERE planning_id=v_planningId;
    RETURN v_planning;
END $$ LANGUAGE plpgsql;

CREATE OR REPLACE PROCEDURE p_remove_planning(v_planningId TEXT)
AS $$
BEGIN
    DELETE FROM planning WHERE planning_id = v_planningId;
END $$ LANGUAGE plpgsql;    

CREATE OR REPLACE FUNCTION find_planning_for_employee_at_date(v_employeeId TEXT,v_date DATE)
RETURNS SETOF planning AS $$
BEGIN 
    RETURN QUERY(SELECT * FROM planning where employee_id=v_employeeId AND Date(planning_beginning) = v_date);
END $$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION find_plannings_for_employee_between_date(v_employeeId TEXT,v_begin TIMESTAMP,v_end TIMESTAMP)
RETURNS SETOF planning AS $$
BEGIN 
    RETURN QUERY(SELECT * FROM planning where employee_id=v_employeeId AND (
                 (DATE(planning_beginning) = DATE(v_begin) AND DATE(planning_ending)=DATE(v_end)) AND(
                    (v_begin::time <= planning_beginning::time AND v_end::time >= planning_ending::time) OR
                     (planning_beginning::time <= v_begin::time AND  planning_ending::time >= v_end::time)
                 )));
END $$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION f_create_team(
    v_teamId TEXT,
    v_name TEXT,
    v_leaderId TEXT,
    v_description TEXT DEFAULT NULL,
    v_parentId TEXT DEFAULT NULL)
RETURNS TEXT AS $$
DECLARE 
    v_id TEXT;
BEGIN
    SELECT team_id INTO v_id FROM team WHERE team_id=v_teamId ;
    IF(v_id IS NULL) THEN
        INSERT INTO team(team_id,team_name,employee_id,team_description,team_id_parent) VALUES($1,$2,$3,$4,$5) RETURNING team_id INTO v_id;
    END IF;
    RETURN v_id;
END $$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION find_team_by_name(v_name TEXT) 
RETURNS team AS $$
DECLARE
    v_team team%ROWTYPE;
BEGIN
    SELECT * INTO v_team from team WHERE team_name=v_name;
    RETURN v_team;
END $$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION find_team_by_id(v_id TEXT) 
RETURNS team AS $$
DECLARE
    v_team team%ROWTYPE;
BEGIN
    SELECT * INTO v_team from team WHERE team_id=v_id;
    RETURN v_team;
END $$ LANGUAGE plpgsql;

CREATE OR REPLACE PROCEDURE p_append_employee_to_team(v_teamId TEXT,v_employeeId TEXT) AS $$
BEGIN

    IF((SELECT COUNT(*) FROM team WHERE team_id= v_teamId) !=0
       AND (SELECT COUNT(*) FROM employee WHERE employee_id=v_employeeId)!=0
       AND (SELECT count(*) FROM isMember WHERE team_id=v_teamId AND employee_id=v_employeeId)=0)
     THEN
        INSERT INTO isMEmber(team_id,employee_id) VALUES($1,$2);
     END IF;
END $$ LANGUAGE plpgsql ;

CREATE TYPE employeeId AS(
    employee_id TEXT
);
CREATE OR REPLACE FUNCTION find_employeeIds_of_team(v_teamId TEXT)
RETURNS SETOF employeeId AS $$
DECLARE
    teams record;
    rec employeeId;
BEGIN
    FOR rec IN (SELECT employee_id FROM employee 
        INNER JOIN isMember USING(employee_id)
        WHERE team_id=v_teamId AND employee_isDeleted=false)
    LOOP
    RETURN next rec;
    END LOOP;
END $$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION find_subTeamName_of_team(v_teamId TEXT)
RETURNS SETOF TEXT AS $$
DECLARE
    teamName record;
    rec TEXT;
BEGIN
    FOR rec IN (
        SELECT team_name from team
        WHERE team_id_parent=v_teamId AND team_id != v_teamId
    ) LOOP
    RETURN NEXT rec;
    END LOOP;
END $$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION find_subTeamId_of_team(v_teamId TEXT)
RETURNS SETOF TEXT AS $$
DECLARE
    v_team_id record;
    rec TEXT;
BEGIN
    FOR rec IN (
        SELECT team_id from team
        WHERE team_id_parent=v_teamId AND team_id != v_teamId
    ) LOOP
    RETURN NEXT rec;
    END LOOP;
END $$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION find_AllTeam_root_Id()
RETURNS SETOF TEXT AS $$
DECLARE
    teamId record;
    rec TEXT;
BEGIN
    FOR rec IN (
        SELECT team_id from team
        WHERE team_id_parent=team_id OR team_id = NULL
    ) LOOP
    RETURN NEXT rec;
    END LOOP;
END $$ LANGUAGE plpgsql;
------------------------------------------------------------------------------------------------





CREATE OR REPLACE PROCEDURE p_remove_employee_to_team(v_teamId TEXT,v_employeeId TEXT) AS $$
BEGIN
    DELETE FROM isMember WHERE team_id=v_teamId AND employee_id=v_employeeId;
END $$ LANGUAGE plpgsql ;



-- REMOVE TEAM only if there is no employee in there
CREATE OR REPLACE PROCEDURE p_remove_team(v_teamId TEXT) AS $$
DECLARE
    v_team team%ROWTYPE;
BEGIN
    SELECT * INTO v_team FROM team where team_id=v_teamId;
    
    IF(v_team.team_id != v_team.team_id_parent) THEN
        DELETE FROM isMember WHERE team_id=v_team.team_id; -- todo: move team to a deletedTeam table
    END IF;
    
    IF((SELECT count(*) FROM isMember WHERE team_id=v_team.team_id)=0) THEN
        DELETE FROM team WHERE team_id=v_team.team_id;
    END IF;
END $$ LANGUAGE plpgsql ;

CREATE OR REPLACE FUNCTION find_teamName_where_leaderIs(v_employeeId TEXT) 
RETURNS SETOF TEXT AS $$
DECLARE
    teamName record;
    rec TEXT;
BEGIN
    FOR rec IN (
        SELECT team_name from team
        WHERE employee_id=v_employeeId AND (team_id = team_id_parent OR team_id_parent IS NULL)
    ) LOOP
    RETURN NEXT rec;
    END LOOP;
END $$ LANGUAGE plpgsql;



CREATE OR REPLACE PROCEDURE p_update_team(v_teamId TEXT,v_description TEXT,v_idLeader TEXT) AS $$
BEGIN
    UPDATE TEAM SET team_description=v_description,employee_id=v_idLeader
    WHERE team_id=v_teamId;
END $$ LANGUAGE plpgsql;


CREATE OR REPLACE PROCEDURE p_append_account_for_employee(v_employeeId TEXT, v_login TEXT,v_password TEXT) AS $$
BEGIN
    IF( (SELECT employee_id FROM employee WHERE employee_id=v_employeeId) IS NOT NULL AND 
         (SELECT employee_login FROM employee WHERE employee_login=v_login) IS NULL ) THEN
        UPDATE employee SET employee_login=v_login,employee_password=v_password WHERE employee_id=v_employeeId;
    END IF;
END $$ LANGUAGE plpgsql;


CREATE TYPE appUser AS(
    employee_id TEXT,
    login TEXT,
    password TEXT
);

CREATE OR REPLACE FUNCTION find_user_by_id(v_id TEXT)
RETURNS appUser AS $$
DECLARE
    v_user appUser;
BEGIN
    SELECT employee_id,employee_login,employee_password INTO v_user FROM find_employee_by_id(v_id);
    RETURN v_user;
END $$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION find_user_by_login(v_login TEXT)
RETURNS appUser AS $$
DECLARE
    v_user appUser;
BEGIN
    SELECT employee_id,employee_login,employee_password INTO v_user FROM employee WHERE employee_login=v_login AND employee_isDeleted=false;
    RETURN v_user;
END $$ LANGUAGE plpgsql;

CREATE OR REPLACE PROCEDURE p_update_password(v_login text,v_password TEXT) AS $$
BEGIN
    UPDATE employee SET employee_password=v_password WHERE employee_login=v_login;
END $$ LANGUAGE PLPGSQL;