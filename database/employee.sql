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
    RETURN new;
END $$ LANGUAGE plpgsql;
CREATE TRIGGER t_update_employee
BEFORE UPDATE ON employee
FOR EACH ROW
EXECUTE PROCEDURE ft_update_employee();

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

