CREATE TABLE items(
   product_code VARCHAR (50) PRIMARY KEY,
   name VARCHAR (50) NOT NULL,
   quantity integer NOT NULL
);

CREATE TABLE carts(
   cart_id SERIAL PRIMARY KEY,
   label VARCHAR (50) NOT NULL,
   data VARCHAR(50) NOT NULL
);

CREATE TABLE items_in_cart(
   cart_id integer NOT NULL,
   product_code VARCHAR (50),
   quantityInCart integer NOT NULL,
   CONSTRAINT PK_Person PRIMARY KEY (cart_id, product_code)
);
