/**
 * Test if Tuple (name, zip) already exist, if not insert the new tuple
 * return the id of tuple
 * fixture test : SELECT * from f_create_city('Liege','4000');
 */
CREATE OR REPLACE FUNCTION f_create_city(v_name TEXT,v_zip TEXT)
RETURNS INT AS $$
DECLARE
    v_id INT;
BEGIN
    SELECT city_id INTO v_id FROM cities WHERE city_name=LOWER(TRIM(v_name)) AND city_zip=LOWER(TRIM(v_zip)); 
    IF (v_id IS NULL) THEN
        INSERT INTO cities(city_name,city_zip) VALUES($1,$2) RETURNING city_id INTO v_id;
    END IF;
    return v_id;
END $$ LANGUAGE plpgsql;

-- Transform all entries into lower case and trimmed
CREATE OR REPLACE FUNCTION ft_city_entriesToLowerCase()
RETURNS trigger AS $$
BEGIN
    new.city_name=LOWER(TRIM(new.city_name));
    new.city_zip=LOWER(TRIM(new.city_zip));
    RETURN new;
END $$ LANGUAGE plpgsql;
CREATE TRIGGER t_city_entriesToLowerCase
BEFORE INSERT OR UPDATE ON cities
FOR EACH ROW
EXECUTE PROCEDURE ft_city_entriesToLowerCase();

CREATE OR REPLACE PROCEDURE p_update_city(v_id INT,v_city TEXT,v_zip TEXT) AS $$
BEGIN
    UPDATE cities SET city_name=v_city, city_zip=v_zip WHERE city_id=v_id;
END $$ LANGUAGE plpgsql;