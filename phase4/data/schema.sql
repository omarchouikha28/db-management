-- ENTITÄTEN --

-- Nutzer-Tabelle
CREATE TABLE IF NOT EXISTS nutzer(
    Email varchar(255) NOT NULL COLLATE NOCASE,
    Password varchar(9) NOT NULL CHECK (Password <> ''),
    primary key(Email)
);

-- Trigger to validate the user
CREATE TRIGGER validate_nutzer BEFORE INSERT ON nutzer
    BEGIN
        SELECT
            CASE
                WHEN NOT (
                    NEW.Email <> ''
                    AND LOWER(NEW.Email) GLOB '*[@]*[.]*'
                    AND LOWER(SUBSTR(NEW.Email, 1, INSTR(NEW.Email, '@')-1)) NOT GLOB '*[^A-Za-z0-9]*'
                    AND LOWER(
                            SUBSTR(
                                NEW.Email, INSTR(NEW.Email, '@')+1, INSTR(NEW.Email, '.')-1 - INSTR(NEW.Email, '@')
                            )
                        ) NOT GLOB '*[^A-Za-z0-9]*'
                    AND LOWER(SUBSTR(NEW.Email, INSTR(NEW.Email, '.')+1)  NOT GLOB '*[^A-Za-z]*')
                    ) THEN RAISE(ABORT,'Invalid email address')

                WHEN NOT(
                    NEW.Password NOT GLOB '*[^ -~]*'
--                     AND NEW.Password REGEXP('^(?=.*[A-Z])(?=(?:.*\d){2,}).{4,9}$')
                    AND NEW.Password GLOB '*[A-Z]*'
                    AND NEW.Password GLOB '*[0-9]*[0-9]*'
                    AND LENGTH(NEW.Password) >= 4
                    AND LENGTH(NEW.Password) <= 9
                    AND LOWER(NEW.Password) NOT GLOB '*a[13579]*'
                    AND LOWER(NEW.Password) NOT GLOB '*e[13579]*'
                    AND LOWER(NEW.Password) NOT GLOB '*i[13579]*'
                    AND LOWER(NEW.Password) NOT GLOB '*o[13579]*'
                    AND LOWER(NEW.Password) NOT GLOB '*u[13579]*'
                    ) THEN RAISE(ABORT,'Invalid Password')
            END;
    END;

-- Kunde-Tabelle
CREATE TABLE IF NOT EXISTS kunde(
    Email varchar(255) NOT NULL COLLATE NOCASE,
    Telefonnummer varchar(15) NOT NULL UNIQUE CHECK (Telefonnummer <> '' AND Telefonnummer NOT GLOB '*[^0-9+-]*'),
    primary key(Email),
    foreign key(Email) references nutzer(Email) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Projektleiter-Tabelle
CREATE TABLE IF NOT EXISTS projektleiter(
    Email varchar(255) NOT NULL COLLATE NOCASE,
    Gehalt FLOAT NOT NULL CHECK (Gehalt > 0 AND Gehalt IS ROUND(Gehalt ,2)),
    primary key(Email),
    foreign key(Email) references nutzer(Email) ON DELETE CASCADE ON UPDATE CASCADE
);


-- Spezialist-Tabelle
CREATE TABLE IF NOT EXISTS spezialist(
    Email varchar(255) NOT NULL COLLATE NOCASE,
    Verfuegbarkeitsstatus Text NOT NULL CHECK(Verfuegbarkeitsstatus <> '' AND Verfuegbarkeitsstatus NOT GLOB '*[^ -~]*'),
    primary key(Email),
    foreign key(Email) references nutzer(Email) ON DELETE CASCADE ON UPDATE CASCADE
);


-- Projekt-Tabelle
CREATE TABLE IF NOT EXISTS projekt(
    ID INTEGER PRIMARY KEY AUTOINCREMENT,
    Projektname varchar(80) NOT NULL CHECK (Projektname <> '' AND Projektname NOT GLOB '*[^ -~]*'),
    Projektdeadline DATE NOT NULL CHECK (Projektdeadline is date("Projektdeadline")),
    Kunde_id varchar(255) NOT NULL COLLATE NOCASE,
    Projektleiter_id varchar(255) NOT NULL COLLATE NOCASE,
    foreign key(Kunde_id) references kunde(Email) ON DELETE CASCADE ON UPDATE CASCADE,
    foreign key(Projektleiter_id) references projektleiter(Email) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Bewertung-Tabelle
CREATE TABLE IF NOT EXISTS bewertung(
    ID INTEGER PRIMARY KEY AUTOINCREMENT,
    Bepunktung smallint NOT NULL check(Bepunktung IN (1,2,3,4,5,6,7,8,9)),
    Projekt_id int NOT NULL,
    Kunde_id varchar(255) NOT NULL COLLATE NOCASE,
    foreign key(Kunde_id) references kunde(Email) ON DELETE CASCADE ON UPDATE CASCADE,
    foreign key(Projekt_id) references projekt(ID) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Ein Kunde darf das gleiche Projekt höchstens 3 Mal bewerten
CREATE TRIGGER bewertung_limit BEFORE INSERT ON bewertung
    BEGIN
        SELECT
               CASE
                   WHEN EXISTS(
                       SELECT count(*) AS counter
                       FROM bewertung
                       WHERE LOWER(bewertung.Kunde_id) = LOWER(NEW.Kunde_id) AND bewertung.Projekt_id = NEW.Projekt_id
                       GROUP BY LOWER(bewertung.Kunde_id)
                       HAVING counter = 3
                   ) THEN RAISE(ABORT,'Ein Kunde darf das gleiche Projekt höchstens 3 Mal bewerten')
               END;
    END;

-- Text-Tabelle
CREATE TABLE IF NOT EXISTS text(
    ID INTEGER PRIMARY KEY AUTOINCREMENT,
    Bewertungstext TEXT NOT NULL CHECK (Bewertungstext <> '' AND Bewertungstext NOT GLOB '*[^ -~]*'),
    Bewertung_id smallint NOT NULL,
    foreign key(Bewertung_id) references bewertung(ID) ON DELETE CASCADE ON UPDATE CASCADE
);


-- Aufgabe-Tabelle
CREATE TABLE IF NOT EXISTS aufgabe(
    ID INTEGER PRIMARY KEY AUTOINCREMENT,
    Status varchar(20) default 'in bearbeitung' check(LOWER(Status) in('in bearbeitung', 'beendet', 'unassigned')),
    Deadline DATE NOT NULL CHECK (Deadline is date("Deadline")),
    Prioritaet varchar(20) default 'niedrig' check(LOWER(Prioritaet) in ('hoch', 'mittel', 'niedrig')),
    Beschreibung TEXT NOT NULL CHECK (Beschreibung <> '' AND Beschreibung NOT GLOB '*[^ -~]*'),
    Projekt_id int NOT NULL,
    foreign key(Projekt_id) references projekt(ID) ON DELETE CASCADE ON UPDATE CASCADE
);

-- fachliche_kompetenz-Tabelle
CREATE TABLE IF NOT EXISTS fachliche_kompetenz(
    ID int,
    Kompetenz varchar(255) NOT NULL CHECK (Kompetenz <> '' AND Kompetenz NOT GLOB '*[^a-zA-Z]*'),
    primary key(ID)
);

-- Designer-Tabelle
CREATE TABLE IF NOT EXISTS designer(
    Email varchar(255) NOT NULL COLLATE NOCASE,
    Spezifikation varchar(20) default 'digital' check(LOWER(Spezifikation) in ('digital', 'grafik')),
    primary key(Email),
    foreign key(Email) references spezialist(Email) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Trigger to ensure that a specialist cannot be a designer when he's already a developer
CREATE TRIGGER designer_is_valid BEFORE INSERT ON designer
BEGIN
    SELECT
        CASE
            WHEN EXISTS(
                    SELECT * FROM entwickler
                    WHERE LOWER(NEW.Email) = LOWER(entwickler.Email)
                )
            THEN RAISE(ABORT,'This user is already a developer.')
        END;
END;


-- weitere_kompetenzen-Tabelle
CREATE TABLE IF NOT EXISTS weitere_kompetenzen(
    ID int,
    Kompetenz varchar(255) NOT NULL CHECK (Kompetenz <> '' AND Kompetenz NOT GLOB '*[^a-zA-Z]*'),
    primary key(ID)
);

-- Alias-Tabelle
CREATE TABLE IF NOT EXISTS alias(
    ID int,
    Aliastext varchar(255) NOT NULL CHECK (Aliastext <> '' AND Aliastext NOT GLOB '*[^ -~]*'),
    Email varchar(255) NOT NULL COLLATE NOCASE,
    primary key(ID),
    foreign key(Email) references designer(Email) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Entwickler-Tabelle
CREATE TABLE IF NOT EXISTS entwickler(
    Kuerzel varchar(8) NOT NULL check(
        length(Kuerzel) = 8 AND Kuerzel GLOB '[A-Za-z][A-Za-z][A-Za-z][A-Za-z][A-Za-z][0-9][0-9][0-9]'
        ) COLLATE NOCASE,
    Email varchar(255) NOT NULL COLLATE NOCASE,
    primary key(Kuerzel),
    foreign key(Email) references spezialist(Email) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Trigger to ensure that the specialist isn't already a designer
CREATE TRIGGER entwickler_is_valid BEFORE INSERT ON entwickler
BEGIN
    SELECT
        CASE
            WHEN EXISTS(
                    SELECT * FROM designer
                    WHERE LOWER(NEW.Email) = LOWER(designer.Email)
                )
            THEN RAISE(ABORT,'This user is already a designer.')
        END;
END;


-- Programmiersprache-Tabelle
CREATE TABLE IF NOT EXISTS programmiersprache(
    ID INTEGER PRIMARY KEY AUTOINCREMENT,
    Name varchar(255) NOT NULL CHECK (Name <> '' AND Name NOT GLOB '*[^ -~]*')
);

-- Eine Programmiersprache kann nicht gelöscht werden, solange diese von jemandem beherrscht wird.
CREATE TRIGGER delete_prog BEFORE DELETE ON programmiersprache
    BEGIN
        SELECT
               CASE
                   WHEN EXISTS(
                            SELECT * from beherrscht
                            WHERE OLD.ID = beherrscht.Programmiersprache_id
                    ) THEN RAISE(ABORT,'Kann nicht gelöscht werden, solange die Sprache von jemandem beherrscht wird')
               END;
    END;


-- RELATIONEN --

-- arbeitet_an-Tabelle
CREATE TABLE IF NOT EXISTS arbeitet_an(
    Email varchar(255) NOT NULL COLLATE NOCASE,
    Projekt_id int,
    primary key(Email, Projekt_id),
    foreign key(Email) references spezialist(Email) ON DELETE CASCADE ON UPDATE CASCADE,
    foreign key(Projekt_id) references projekt(ID) ON DELETE CASCADE ON UPDATE CASCADE
);


-- ist_mentor_von-Tabelle
-- NOTE: Email1 ist die Email vom Mentor und Email2 ist die Email von einem beliebigen Nutzer (solange er kein Mentor ist)
CREATE TABLE IF NOT EXISTS ist_mentor_von(
    Email1 varchar(255) NOT NULL COLLATE NOCASE,
    Email2 varchar(255) NOT NULL COLLATE NOCASE,
    primary key(Email1, Email2),
    foreign key(Email1) references spezialist(Email) ON DELETE CASCADE ON UPDATE CASCADE,
    foreign key(Email2) references spezialist(Email) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Ein Mentor darf nicht (von einem anderen Mentor) betreut werden.
CREATE TRIGGER mentor_betreut_mentor BEFORE INSERT ON ist_mentor_von
    BEGIN
        SELECT
               CASE
                   WHEN EXISTS(
                        SELECT * FROM ist_mentor_von
                        WHERE LOWER(NEW.Email2) = LOWER(ist_mentor_von.Email1)
                   ) THEN RAISE(ABORT,'Ein Mentor darf nicht (von einem anderen Mentor) betreut werden.')
               END;
    END;

-- Spezialist_hat_fachliche_kompetenz-Tabelle
CREATE TABLE IF NOT EXISTS spezialist_hat_fachliche_kompetenz(
    Email varchar(255) NOT NULL COLLATE NOCASE,
    Kompetenz_id int,
    primary key(Email, Kompetenz_id),
    foreign key(Email) references spezialist(Email) ON DELETE CASCADE ON UPDATE CASCADE,
    foreign key(Kompetenz_id) references fachliche_kompetenz(ID) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Beherrscht-Tabelle
CREATE TABLE IF NOT EXISTS beherrscht(
    Kuerzel varchar(8) NOT NULL COLLATE NOCASE,
    Programmiersprache_id int,
    Erfahrungsstufe INTEGER default 1 check(Erfahrungsstufe IN(1,2,3)),
    primary key(Kuerzel, Programmiersprache_id),
    foreign key(Kuerzel) references entwickler(Kuerzel) ON DELETE CASCADE ON UPDATE CASCADE,
    foreign key(Programmiersprache_id) references programmiersprache(ID) ON DELETE CASCADE ON UPDATE CASCADE
);


-- designer_hat_weitere_kompetenzen-Tabelle
CREATE TABLE IF NOT EXISTS designer_hat_weitere_kompetenzen(
    Email varchar(255) NOT NULL COLLATE NOCASE,
    Kompetenz_id int,
    primary key(Email, Kompetenz_id),
    foreign key(Email) references designer(Email) ON DELETE CASCADE ON UPDATE CASCADE,
    foreign key(Kompetenz_id) references weitere_kompetenzen(ID) ON DELETE CASCADE ON UPDATE CASCADE
);