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

select * from find_all_groupName() 