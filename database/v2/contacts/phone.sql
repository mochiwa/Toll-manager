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



