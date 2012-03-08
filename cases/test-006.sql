# script créé le : Thu Dec 23 15:24:35 CET 2010 ;

# use  VOTRE_BASE_DE_DONNEE ;

DROP TABLE IF EXISTS ANIMATEUR ;

CREATE TABLE ANIMATEUR (id_animateur INT NOT NULL,
nom_animateur VARCHAR(30),
id_groupe INT,
PRIMARY KEY (id_animateur) ) ENGINE=InnoDB;

DROP TABLE IF EXISTS GROUPE ;

CREATE TABLE GROUPE (id_groupe INT NOT NULL,
nom_groupe VARCHAR(30),
id_animateur INT,
PRIMARY KEY (id_groupe) ) ENGINE=InnoDB;

ALTER TABLE ANIMATEUR ADD CONSTRAINT FK_ANIMATEUR_id_groupe FOREIGN KEY (id_groupe) REFERENCES GROUPE (id_groupe);

ALTER TABLE GROUPE ADD CONSTRAINT FK_GROUPE_id_animateur FOREIGN KEY (id_animateur) REFERENCES ANIMATEUR (id_animateur);

