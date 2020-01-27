CREATE OR REPLACE FUNCTION f_create_planning(v_planningId TEXT,v_begin TIMESTAMP,v_end TIMESTAMP,v_comment TEXT,v_employeeId TEXT,v_teamId TEXT)
RETURNS TEXT AS $$
DECLARE
	v_id TEXT;
BEGIN
	SELECT planning_id INTO v_id FROM planning WHERE planning_id=v_planningId;
	IF(v_id IS NULL) THEN
		INSERT INTO planning(planning_id,planning_beginning,planning_ending,planning_comment,employee_id,team_id) VALUES($1,$2,$3,$4,$5,$6) RETURNING planning_id INTO v_id;
	END IF;
	RETURN v_id;	
END $$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION f_get_planning_by_id(v_planningId TEXT)
RETURNS planning AS $$
DECLARE
	v_planning planning%ROWTYPE;
BEGIN
	SELECT * INTO v_planning FROM planning WHERE planning_id=v_planningId;
	RETURN v_planning;
END $$ LANGUAGE plpgsql;

CREATE OR REPLACE PROCEDURE p_remove_planning(v_planningId TEXT)
AS $$
BEGIN
	DELETE FROM planning WHERE planning_id = v_planningId;
END $$ LANGUAGE plpgsql;    

CREATE OR REPLACE FUNCTION find_planning_for_employee_at_date(v_employeeId TEXT,v_date DATE)
RETURNS SETOF planning AS $$
BEGIN 
	RETURN QUERY(SELECT * FROM planning where employee_id=v_employeeId AND Date(planning_beginning) = v_date);
END $$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION find_plannings_for_employee_between_date(v_employeeId TEXT,v_begin TIMESTAMP,v_end TIMESTAMP)
RETURNS SETOF planning AS $$
BEGIN 
	RETURN QUERY(SELECT * FROM planning where employee_id=v_employeeId AND (
				 (DATE(planning_beginning) = DATE(v_begin) AND DATE(planning_ending)=DATE(v_end)) AND(
				 	(v_begin::time <= planning_beginning::time AND v_end::time >= planning_ending::time) OR
					 (planning_beginning::time <= v_begin::time AND  planning_ending::time >= v_end::time)
				 )));
END $$ LANGUAGE plpgsql;
