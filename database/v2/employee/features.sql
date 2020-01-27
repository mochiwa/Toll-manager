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
