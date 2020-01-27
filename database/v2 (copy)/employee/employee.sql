/**
 * Fixtures test :SELECT * FROM f_create_employee('aasa','11.11.11-111.12','jOhn','does',TO_DATE('1993-11-20','yyyy-mm-dd'));
 */
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


CREATE OR REPLACE PROCEDURE p_delete_employee(v_employeeId TEXT) AS $$
BEGIN
    UPDATE employee SET employee_isDeleted=true WHERE employee_id=v_employeeId;
END $$ LANGUAGE plpgsql;


/****
FEATURES:

SELECT * FROM f_create_employee('a','99.99.99-999-001','doe','john','1993-12-27');
SELECT * FROM f_create_employee('b','99.99.99-999-002','eric','johnson','1993-12-27');
SELECT * FROM f_create_employee('c','99.99.99-999-003','pierre','cros','1993-12-27');
SELECT * FROM f_create_employee('d','99.99.99-999-004','gens','dark','1993-12-27');


***/