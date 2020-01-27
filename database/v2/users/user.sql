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