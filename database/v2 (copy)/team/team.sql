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
        WHERE team_id=v_teamId)
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