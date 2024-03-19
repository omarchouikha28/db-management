-- Nutzer
INSERT INTO nutzer VALUES ('oMAr@chouikha.de','UqWeR5tz3');
INSERT INTO nutzer VALUES ('max@mustermann.de','mU8t5R');
INSERT INTO nutzer VALUES ('lablebi@harr.tn','MwY0X5r');
INSERT INTO nutzer VALUES ('makarouna@thon.tn','MwY0X5r');
INSERT INTO nutzer VALUES ('sauce@blanche.tn','MwY0X5r');

-- Kunde
INSERT INTO kunde VALUES('oMaR@chouikha.de','+491783850697');
INSERT INTO kunde VALUES('mAx@mustermann.de','+491783850689');
INSERT INTO kunde VALUES('sauce@blanche.tn','+491783850683');

-- NOTE: das sind die Kunde, die kein Projekt in Auftrag gegeben haben
INSERT INTO kunde VALUES ('lablebi@harr.tn','+491783850533');
INSERT INTO kunde VALUES ('makarouna@thon.tn','+491783850543');

-- Projektleiter
INSERT INTO projektleiter VALUES('omar@chouikha.de','480');
INSERT INTO projektleiter VALUES('max@mustermann.de','580');
INSERT INTO projektleiter VALUES('lableBi@harr.tn','580');

-- Projekt
INSERT INTO projekt VALUES(1,'Projekt_1', date('2022-12-05'),'mAX@mustermann.de','omar@chouikha.de');
INSERT INTO projekt VALUES(2,'Projekt_2', date('2022-12-05'),'omar@chouikha.de','max@mustermann.de');
INSERT INTO projekt VALUES(3,'Projekt_3', date('2022-12-05'),'omar@chouikha.de','max@mustermann.de');
INSERT INTO projekt VALUES(4,'Projekt_4', date('2022-12-05'),'sauce@blanche.tn','max@mustermann.de');
INSERT INTO projekt VALUES(5,'Projekt_5', date('2022-12-03'),'omar@chouikha.de','max@mustermann.de');

-- Bewertung
INSERT INTO bewertung VALUES(1,5,1,'max@mustermann.de');
INSERT INTO bewertung VALUES(2,8,2,'omAr@chouikha.de');
INSERT INTO bewertung VALUES(3,8,2,'oMar@chouikha.de');
INSERT INTO bewertung VALUES(4,8,2,'omaR@chouikha.de');

-- Text
INSERT INTO text VALUES (1,'good project',1);
INSERT INTO text VALUES (2,'reaaaallly good project',2);

-- Aufgabe
INSERT INTO aufgabe VALUES (1,'In Bearbeitung',date('2022-12-05'),'Niedrig','aufgabe1',1);
-- Projekt 2 hat 5 Aufgaben
INSERT INTO aufgabe VALUES (2,'Beendet',date('2022-12-05'),'Hoch','aufgabe2',2);
INSERT INTO aufgabe VALUES (3,'In Bearbeitung',date('2022-12-05'),'Hoch','aufgabe3',2);
INSERT INTO aufgabe VALUES (4,'Unassigned',date('2022-12-05'),'hOch','aufgabe4',2);
INSERT INTO aufgabe VALUES (5,'Beendet',date('2022-12-05'),'HoCh','aufgabe5',2);
INSERT INTO aufgabe VALUES (6,'Beendet',date('2022-12-05'),'HocH','aufgabe6',2);
-- Projekt 3 hat 3 Aufgaben
INSERT INTO aufgabe VALUES (7,'Beendet',date('2022-12-05'),'Hoch','aufgabe7',3);
INSERT INTO aufgabe VALUES (8,'In Bearbeitung',date('2022-12-05'),'Hoch','aufgabe8',3);
INSERT INTO aufgabe VALUES (9,'Unassigned',date('2022-12-05'),'hOch','aufgabe9',3);

-- Fachliche_Kompetenz
INSERT INTO fachliche_kompetenz VALUES (1, 'KompetenzUNO');
INSERT INTO fachliche_kompetenz VALUES (2, 'KompetenzDOS');
INSERT INTO fachliche_kompetenz VALUES (3, 'KompetenzTRES');
INSERT INTO fachliche_kompetenz VALUES (4, 'KompetenzCUATRO');

-- Spezialist
INSERT INTO spezialist VALUES('omar@chouikha.de', 'available');
INSERT INTO spezialist VALUES('max@mustermann.de','available');
INSERT INTO spezialist VALUES('lablebi@harr.tn', 'available');
INSERT INTO spezialist VALUES('makarouna@thon.tn', 'unavailable');
INSERT INTO spezialist VALUES('sauCe@blanche.tn', 'unavailable');

-- Entwickler
INSERT INTO entwickler VALUES ('harra100', 'lablebi@harr.tn');
INSERT INTO entwickler VALUES ('thonn100', 'makarouna@thon.tn');

-- Designer
INSERT INTO designer VALUES ('omar@chouikha.de','Grafik');
INSERT INTO designer VALUES ('max@mustermann.de','Digital');

-- Weitere_Kompetenzen
INSERT INTO weitere_kompetenzen VALUES(1, 'KompetenzUNO');
INSERT INTO weitere_kompetenzen VALUES(2, 'KompetenzDOS');

-- Alias
INSERT INTO alias VALUES (1,'omcho100','omar@chouikha.de');
INSERT INTO alias VALUES (2,'maxim100','max@mustermann.de');

-- Programmiersprache
INSERT INTO programmiersprache VALUES (1, 'JAVA');
INSERT INTO programmiersprache VALUES (2, 'JavaScript');
INSERT INTO programmiersprache VALUES (3, 'C++');
INSERT INTO programmiersprache VALUES (4, 'C#');

-- Arbeitet_an
INSERT INTO arbeitet_an VALUES ('omar@chouikha.de', 1);
INSERT INTO arbeitet_an VALUES ('max@mustermann.de', 2);

-- Ist_mentor_von
INSERT INTO ist_mentor_von VALUES ('omar@chouikha.de','max@mustermann.de');
INSERT INTO ist_mentor_von VALUES ('max@mustermann.de','lablebi@harr.tn');

-- Spezialist_hat_fachliche_kompetenz
INSERT INTO spezialist_hat_fachliche_kompetenz VALUES ('omar@chouikha.de', 1);
INSERT INTO spezialist_hat_fachliche_kompetenz VALUES ('max@mustermann.de', 2);
INSERT INTO spezialist_hat_fachliche_kompetenz VALUES ('labLebi@harr.tn', 3);
INSERT INTO spezialist_hat_fachliche_kompetenz VALUES ('makarouna@thon.tn', 2);

-- Beherrscht
INSERT INTO beherrscht VALUES ('harra100', 1, 3);
INSERT INTO beherrscht VALUES ('thonn100', 2, 2);
INSERT INTO beherrscht VALUES ('thOnn100', 4, 2);
INSERT INTO beherrscht VALUES ('harRA100', 4, 2);

-- Designer_hat_weitere_kompetenzen
INSERT INTO designer_hat_weitere_kompetenzen VALUES ('omar@chouikha.de', 1);
INSERT INTO designer_hat_weitere_kompetenzen VALUES ('max@mustermann.de', 2);

