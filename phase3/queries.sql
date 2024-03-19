-- Geben Sie genau die Projekte (alle Attribute) aus, die mindestens drei Aufgaben mit hoher Priorität haben
SELECT p.* FROM projekt p, aufgabe a
WHERE p.ID = a.Projekt_id AND a.Prioritaet = 'hoch' COLLATE NOCASE
GROUP BY p.ID
HAVING count(*) >= 3;

-- Geben Sie genau das Projekt mit der besten Durchschnittsbewertung aus.
SELECT  p.* FROM projekt p, bewertung b
WHERE b.Projekt_id = p.ID
GROUP BY p.ID
ORDER BY AVG(b.Bepunktung) DESC
LIMIT 1;

-- Geben Sie in alphabetischer Reihenfolge die E-Mail-Adressen genau der Nutzer aus, die noch
-- kein Projekt in Auftrag gegeben haben.
SELECT DISTINCT k.Email FROM kunde k, projekt p
WHERE k.Email NOT IN (
    SELECT pro.Kunde_id FROM projekt pro
    )
ORDER BY Email;



