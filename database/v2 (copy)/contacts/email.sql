CREATE OR REPLACE FUNCTION f_append_email(v_employeeId TEXT,v_email TEXT)
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


CREATE OR REPLACE FUNCTION find_emails(v_employeeId TEXT)
RETURNS SETOF email AS $$
BEGIN
	RETURN QUERY (
		SELECT * FROM email where employee_id=v_employeeId
	);
END $$ LANGUAGE plpgsql;


CREATE OR REPLACE PROCEDURE p_dislink_employee_email(v_email TEXT)
AS $$
BEGIN
	DELETE FROM email WHERE email_value=v_email;
END $$ LANGUAGE plpgsql;

-- TRIGGER employee must have atleast one phone ,then if delete last phone known it canceled the process
CREATE OR REPLACE FUNCTION ft_oneEmailRequired()
RETURNS TRIGGER AS $$
BEGIN
    IF((SELECT count(*) FROM email WHERE employee_id=old.employee_id) = 0 ) THEN
		INSERT INTO email(employee_id,email_id,email_value) VALUES
			(old.employee_id,old.email_id,old.email_value);
    END IF;
	RETURN OLD;
END $$ LANGUAGE plpgsql;
CREATE TRIGGER t_oneEmailRequired
AFTER DELETE OR UPDATE ON email
FOR EACH ROW
EXECUTE PROCEDURE ft_oneEmailRequired();


