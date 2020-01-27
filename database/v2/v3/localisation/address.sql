/**
 * Test if Tuple (street, number) already exist, if not insert the new tuple
 * return the id of tuple
 * fixture test :  SELECT * FROM f_create_address('rue saint-gilles','412');  
 */
CREATE OR REPLACE FUNCTION f_create_address(v_street TEXT,v_number TEXT)
RETURNS INT AS $$
DECLARE
    v_address_id INT;
BEGIN
    SELECT address_id INTO v_address_id FROM address WHERE address_street=LOWER(TRIM(v_street)) AND address_number=LOWER(TRIM(v_number));
    IF(v_address_id IS NULL) THEN
        INSERT INTO address(address_street,address_number) VALUES ($1,$2) RETURNING address_id INTO v_address_id; 
    END IF;
    return v_address_id;
END $$ LANGUAGE plpgsql;   

/**
 * Transform all entries into lower case and trimmed
 */
CREATE OR REPLACE FUNCTION ft_address_entriesToLowerCase()
RETURNS trigger AS $$
BEGIN
    new.address_street=LOWER(TRIM(new.address_street));
    new.address_number=LOWER(TRIM(new.address_number));
    RETURN new;
END $$ LANGUAGE plpgsql;
CREATE TRIGGER t_address_entriesToLowerCase
BEFORE INSERT OR UPDATE ON address
FOR EACH ROW
EXECUTE PROCEDURE ft_address_entriesToLowerCase();

/**
 * Update an addres
 * fixture test : CALL p_update_address(1,'rue saint-gilles','348');
 */
CREATE OR REPLACE PROCEDURE p_update_address(v_address_id INT,v_street TEXT,v_number TEXT) AS $$
BEGIN
    UPDATE address SET address_street=v_street, address_number=v_number WHERE address_id=v_address_id;
END $$ LANGUAGE plpgsql;