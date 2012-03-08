# script créé le : Thu Dec 23 15:23:26 CET 2010 ;

# use  VOTRE_BASE_DE_DONNEE ;

DROP TABLE IF EXISTS COMMANDE ;

CREATE TABLE COMMANDE (id_commande INT NOT NULL,
date_commande DATETIME,
PRIMARY KEY (id_commande) ) ENGINE=InnoDB;

DROP TABLE IF EXISTS PRODUIT ;

CREATE TABLE PRODUIT (id_produit INT NOT NULL,
libelle_produit VARCHAR(30),
PRIMARY KEY (id_produit) ) ENGINE=InnoDB;

DROP TABLE IF EXISTS compose ;

CREATE TABLE compose (id_commande INT NOT NULL,
id_produit INT NOT NULL,
quantite INT,
PRIMARY KEY (id_commande,
 id_produit) ) ENGINE=InnoDB;

ALTER TABLE compose ADD CONSTRAINT FK_compose_id_commande FOREIGN KEY (id_commande) REFERENCES COMMANDE (id_commande);

ALTER TABLE compose ADD CONSTRAINT FK_compose_id_produit FOREIGN KEY (id_produit) REFERENCES PRODUIT (id_produit);

