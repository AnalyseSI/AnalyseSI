# script créé le : Thu Dec 23 15:22:52 CET 2010 ;

# use  VOTRE_BASE_DE_DONNEE ;

DROP TABLE IF EXISTS SOCIETE ;

CREATE TABLE SOCIETE (id_societe INT NOT NULL,
nom_societe VARCHAR(30),
PRIMARY KEY (id_societe) ) ENGINE=InnoDB;

DROP TABLE IF EXISTS EMPLOYE ;

CREATE TABLE EMPLOYE (id_employe INT NOT NULL,
nom_employe VARCHAR(30),
id_societe INT,
PRIMARY KEY (id_employe) ) ENGINE=InnoDB;

ALTER TABLE EMPLOYE ADD CONSTRAINT FK_EMPLOYE_id_societe FOREIGN KEY (id_societe) REFERENCES SOCIETE (id_societe);

