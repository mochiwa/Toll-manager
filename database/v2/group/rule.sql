CREATE OR REPLACE FUNCTION f_create_rule(v_name TEXT,v_description TEXT,v_target TEXT)
RETURNS INT AS $$
DECLARE
    v_id INT;
BEGIN
    SELECT rule_id INTO v_id FROM rule WHERE rule_name=v_name AND rule_target=v_target;
    IF(v_id IS NULL) THEN
        INSERT INTO rule(rule_name,rule_description,rule_target) VALUES($1,$2,$3) RETURNING rule_id INTO v_id;
    END IF;
    RETURN v_id;
END $$ LANGUAGE plpgsql; 

CREATE OR REPLACE PROCEDURE p_append_rule_to_group(v_group TEXT ,v_ruleId INT) AS $$
DECLARE
    v_groupId INT;
BEGIN
    SELECT group_id INTO v_groupId FROM groups WHERE group_name=v_group;
    IF((SELECT COUNT(*) FROM hasRules WHERE group_id=v_groupId AND rule_id=v_ruleId)=0 )THEN
        INSERT INTO hasRules(group_id,group_name,rule_id) VALUES (v_groupId,$1,$2);
    END IF;
END $$ LANGUAGE plpgsql; 


CREATE OR REPLACE PROCEDURE p_remove_rule_to_group(v_group TEXT ,v_ruleName TEXT,v_ruleTarget TEXT) AS $$
DECLARE
	v_ruleId INT;
BEGIN
	SELECT rule_id INTO v_ruleId FROM rule WHERE rule_name=v_ruleName AND rule_target=v_ruleTarget;
	IF(v_ruleId IS NOT NULL) THEN
		DELETE FROM hasRules WHERE group_name=v_group AND rule_id=v_ruleId;
	END IF;
END $$ LANGUAGE plpgsql; 


