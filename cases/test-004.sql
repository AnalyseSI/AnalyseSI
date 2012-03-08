# script créé le : Thu Dec 23 15:16:07 CET 2010 ;

# use  VOTRE_BASE_DE_DONNEE ;

DROP TABLE IF EXISTS ETUDIANT ;

CREATE TABLE ETUDIANT (id_etudiant INT NOT NULL,
nom_etudiant VARCHAR(30),
PRIMARY KEY (id_etudiant) ) ENGINE=InnoDB;

DROP TABLE IF EXISTS LANGUE ;

CREATE TABLE LANGUE (id_langue INT NOT NULL,
nom_langue VARCHAR(30),
PRIMARY KEY (id_langue) ) ENGINE=InnoDB;

DROP TABLE IF EXISTS NIVEAU ;

CREATE TABLE NIVEAU (id_niveau INT NOT NULL,
nom_niveau VARCHAR(30),
PRIMARY KEY (id_niveau) ) ENGINE=InnoDB;

DROP TABLE IF EXISTS parle ;

CREATE TABLE parle (id_etudiant INT NOT NULL,
id_langue INT NOT NULL,
id_niveau INT NOT NULL,
PRIMARY KEY (id_etudiant,
 id_langue,
 id_niveau) ) ENGINE=InnoDB;

ALTER TABLE parle ADD CONSTRAINT FK_parle_id_etudiant FOREIGN KEY (id_etudiant) REFERENCES ETUDIANT (id_etudiant);

ALTER TABLE parle ADD CONSTRAINT FK_parle_id_langue FOREIGN KEY (id_langue) REFERENCES LANGUE (id_langue);

ALTER TABLE parle ADD CONSTRAINT FK_parle_id_niveau FOREIGN KEY (id_niveau) REFERENCES NIVEAU (id_niveau);

