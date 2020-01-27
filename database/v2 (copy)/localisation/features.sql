/** 
 *make the link between city and address
 */
CREATE OR REPLACE PROCEDURE link_address_city(v_idAddress INT,v_idCity INT) AS $$ 
BEGIN
    IF ((SELECT address_id FROM situated WHERE address_id=v_idAddress AND city_id=v_idCity) IS NULL) THEN
        INSERT INTO situated(address_id,city_id) VALUES($1,$2);
    END IF;
END $$ LANGUAGE plpgsql;

--Make the link between city and country
CREATE OR REPLACE PROCEDURE link_city_country(v_idCity INT,v_idCountry INT) AS $$
BEGIN
    IF ((SELECT city_id FROM located WHERE city_id=v_idcity AND country_id=v_idCountry) IS NULL) THEN
        INSERT INTO located(city_id,country_id) VALUES($1,$2);
    END IF;
END $$ LANGUAGE plpgsql;

--Make the link between employee and address
CREATE OR REPLACE PROCEDURE link_employee_address(v_idEmployee TEXT,v_idAddress INT)AS $$ 
BEGIN
    IF((select employee_id FROM employee WHERE employee_id=v_idEmployee) IS NOT NULL
        AND (SELECT address_id FROM address WHERE address_id=v_idAddress) IS NOT NULL
        AND (SELECT COUNT(*) FROM lives WHERE  employee_id=v_idEmployee AND address_id=v_idAddress)=0) THEN
        INSERT INTO lives(employee_id,address_id) VALUES($1,$2);
    END IF;
END $$ LANGUAGE plpgsql;  
-- Remove address from an employee
CREATE OR REPLACE PROCEDURE p_dislink_employee_address(v_employeeID TEXT,v_street TEXT,v_number TEXT)
AS $$
DECLARE
    v_idAddress INT;
BEGIN
    SELECT address_id INTO v_idAddress FROM address WHERE address_street=v_street AND address_number=v_number;
    IF(v_idAddress IS NOT NULL)THEN
        DELETE FROM lives WHERE (address_id=v_idAddress AND employee_id=v_employeeId);
    END IF;
END $$ LANGUAGE plpgsql;
-- TRIGGER employee must have atleast one address ,then if delete last address know it canceled the process
CREATE OR REPLACE FUNCTION ft_oneAddressRequired()
RETURNS TRIGGER AS $$
BEGIN
    IF((SELECT count(*) FROM lives WHERE employee_id=old.employee_id) =0) THEN
        INSERT INTO LIVES(address_id,employee_id) VALUES(old.address_id,old.employee_id);
    END IF;
    RETURN OLD;
END $$ LANGUAGE plpgsql;
CREATE TRIGGER t_oneAddressRequired
AFTER DELETE OR UPDATE ON lives
FOR EACH ROW
EXECUTE PROCEDURE ft_oneAddressRequired();


-- create a complet postal address with linking
CREATE OR REPLACE FUNCTION f_create_postalAddress(
    v_street TEXT,v_number TEXT,
    v_city TEXT,v_zip TEXT,
    v_country TEXT)
    RETURNS INT AS $$
DECLARE
    v_idAddress INT;
    v_idCity INT;
    v_idCountry INT;
BEGIN
    SELECT * INTO v_idCountry FROM f_create_country(v_country);
    SELECT * INTO v_idCity FROM f_create_city(v_city,v_zip);
    SELECT * INTO v_idAddress FROM f_create_address(v_street,v_number);

    IF (v_idAddress IS NULL OR v_idCity IS NULL OR v_idCountry IS NULL) THEN
        ROLLBACK;
    ELSE
        CALL link_address_city(v_idAddress,v_idCity);
        CALL link_city_country(v_idCity,v_idCountry);
    END IF;
    RETURN v_idAddress;
END $$ LANGUAGE plpgsql;

------------------------------------------------------------------------------------------------
--features:
/*
Call p_create_postalAddress('rue saint-gilles','412','liege','4000','belgique');
Call p_create_postalAddress('rue saint-gerond','12','liege','4000','belgique');
Call p_create_postalAddress('rue saint-gilles','10','liege','4000','belgique');
Call p_create_postalAddress('rue saint-gerond','221','liege','4000','belgique');
*/


-- Type that represent a postal address
CREATE TYPE postal_address AS(
    house_number TEXT,
    street TEXT,
    city TEXT,
    zip TEXT,
    country TEXT
);
-- return list of postal address linked to the employeeId
CREATE OR REPLACE FUNCTION find_postal_addresses(v_employeeId TEXT) 
RETURNS SETOF postal_address AS $$
DECLARE
    addresses record;
    rec postal_address;
BEGIN
    FOR rec IN SELECT
         address_number,address_street,city_name,city_zip,country_name FROM lives
        INNER JOIN address USING(address_id)
        INNER JOIN situated USING(address_id)
        INNER JOIN cities USING(city_id)
        INNER JOIN located USING(city_id)
        INNER JOIN country USING(country_id)
        WHERE employee_id=v_employeeId
    LOOP
    RETURN next rec;
    END LOOP;
END $$ LANGUAGE plpgsql;