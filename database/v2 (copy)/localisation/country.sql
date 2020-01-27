/**
 * if country not exist , append it then return id
 * Fixture test : SELECT * FROM f_create_country('BelGique');
 */
CREATE OR REPLACE FUNCTION f_create_country(v_name TEXT)
RETURNS INT AS $$
DECLARE
    v_id INT;
BEGIN
    SELECT country_id INTO v_id FROM country WHERE country_name=LOWER(TRIM(v_name));
    IF (v_id IS NULL) THEN
        INSERT INTO country(country_name) VALUES($1) RETURNING country_id INTO v_id;
    END IF;
    RETURN v_id;
END $$ LANGUAGE plpgsql;

-- Transform all entries into lower case and trimmed
CREATE OR REPLACE FUNCTION ft_country_entryToLowerCase()
RETURNS trigger AS $$
BEGIN
    new.country_name=LOWER(TRIM(new.country_name));
    return new;
END $$ LANGUAGE plpgsql;
CREATE TRIGGER t_country_entryToLowerCase
BEFORE INSERT OR UPDATE ON country
FOR EACH ROW
EXECUTE PROCEDURE ft_country_entryToLowerCase();

