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
