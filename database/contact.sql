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
