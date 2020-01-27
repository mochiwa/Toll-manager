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
CREATE OR REPLACE FUNCTION f_create_role(v_name TEXT,v_description TEXT,v_target TEXT)
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
BEGIN
	INSERT INTO hasRules(group_id,rule_id) VALUES ($1,$2);
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
