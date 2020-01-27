 ---------------------------------------------------------------
 --        Script Oracle.  
 ---------------------------------------------------------------


------------------------------------------------------------
-- Table: Address
------------------------------------------------------------
CREATE TABLE public.address(
    address_id       SERIAL NOT NULL ,
    address_street   VARCHAR (50) NOT NULL ,
    address_number   VARCHAR (50) NOT NULL  ,
    CONSTRAINT address_PK PRIMARY KEY (address_id)
)WITHOUT OIDS;

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

-- Transform all entries into lower case and trimmed
CREATE OR REPLACE FUNCTION ft_create_address()
RETURNS trigger AS $$
BEGIN
    new.address_street=LOWER(TRIM(new.address_street));
    new.address_number=LOWER(TRIM(new.address_number));
    RETURN new;
END $$ LANGUAGE plpgsql;
CREATE TRIGGER t_create_address
BEFORE INSERT OR UPDATE ON address
FOR EACH ROW
EXECUTE PROCEDURE ft_create_address();

/**
 * Update an addres
 * fixture test : CALL p_update_address(1,'rue saint-gilles','348');
 */
CREATE OR REPLACE PROCEDURE p_update_address(v_address_id INT,v_street TEXT,v_number TEXT) AS $$
BEGIN
    UPDATE address SET address_street=v_street, address_number=v_number WHERE address_id=v_address_id;
END $$ LANGUAGE plpgsql;



------------------------------------------------------------
-- Table: Cities
------------------------------------------------------------
CREATE TABLE public.cities(
    city_id     SERIAL NOT NULL ,
    city_name   VARCHAR (50) NOT NULL ,
    city_zip    VARCHAR (50) NOT NULL  ,
    CONSTRAINT cities_PK PRIMARY KEY (city_id)
)WITHOUT OIDS;

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
CREATE OR REPLACE FUNCTION ft_create_city()
RETURNS trigger AS $$
BEGIN
    new.city_name=LOWER(TRIM(new.city_name));
    new.city_zip=LOWER(TRIM(new.city_zip));
    RETURN new;
END $$ LANGUAGE plpgsql;
CREATE TRIGGER t_create_city
BEFORE INSERT OR UPDATE ON cities
FOR EACH ROW
EXECUTE PROCEDURE ft_create_city();

CREATE OR REPLACE PROCEDURE p_update_city(v_id INT,v_city TEXT,v_zip TEXT) AS $$
BEGIN
    UPDATE cities SET city_name=v_city, city_zip=v_zip WHERE city_id=v_id;
END $$ LANGUAGE plpgsql;

------------------------------------------------------------
-- Table: situated (links address and city)
------------------------------------------------------------
CREATE TABLE public.situated(
    city_id      INT  NOT NULL ,
    address_id   INT  NOT NULL  ,
    CONSTRAINT situated_PK PRIMARY KEY (city_id,address_id)
)WITHOUT OIDS;

-- make the link between city and address
CREATE OR REPLACE PROCEDURE link_address_city(v_idAddress INT,v_idCity INT) AS $$
BEGIN
    IF ((SELECT address_id FROM situated WHERE address_id=v_idAddress AND city_id=v_idCity) IS NULL) THEN
        INSERT INTO situated(address_id,city_id) VALUES($1,$2);
    END IF;
END $$ LANGUAGE plpgsql;

-------------------------------------------------------------------

------------------------------------------------------------
-- Table: country
------------------------------------------------------------
CREATE TABLE public.country(
    country_id     SERIAL NOT NULL ,
    country_name   VARCHAR (50) NOT NULL UNIQUE ,
    CONSTRAINT country_PK PRIMARY KEY (country_id)
)WITHOUT OIDS;

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
CREATE OR REPLACE FUNCTION ft_create_country()
RETURNS trigger AS $$
BEGIN
    new.country_name=LOWER(TRIM(new.country_name));
    return new;
END $$ LANGUAGE plpgsql;
CREATE TRIGGER t_create_country
BEFORE INSERT OR UPDATE ON country
FOR EACH ROW
EXECUTE PROCEDURE ft_create_country();

------------------------------------------------------------
-- Table: located
------------------------------------------------------------
CREATE TABLE public.located(
    country_id   INT  NOT NULL ,
    city_id      INT  NOT NULL  ,
    CONSTRAINT located_PK PRIMARY KEY (country_id,city_id)
)WITHOUT OIDS;

--Make the link between city and country
CREATE OR REPLACE PROCEDURE link_city_country(v_idCity INT,v_idCountry INT) AS $$
BEGIN
    IF ((SELECT city_id FROM located WHERE city_id=v_idcity AND country_id=v_idCountry) IS NULL) THEN
        INSERT INTO located(city_id,country_id) VALUES($1,$2);
    END IF;
END $$ LANGUAGE plpgsql;



/**
 * Create a postal address
 */
CREATE OR REPLACE PROCEDURE p_create_postalAddress(
    v_street TEXT,v_number TEXT,
    v_city TEXT,v_zip TEXT,
    v_country TEXT)
AS $$
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
END $$ LANGUAGE plpgsql;



