VERSION: 3.40.0

- Die Bedingungen für die many to many relationships sind nicht erfüllbar:

Der Spezialist muss mindestens eine fachliche Kompetenz haben.
Die spezialist_hat_fachliche_kompetenz Tabelle bekommt die Email vom Spezialisten als Foreign Key und sie ist auch Teil eines Primärschlüssels.
Es ist dann unmöglich einen Spezialisten ohne fachliche Kompetenz(en) zu erstellen bzw. eine fachliche Kompetenz ohne Spezialist zu erstellen.
Diese Bedingung ist mit reinem SQL auf keinen Fall erfüllbar.

Das Gleiche gilt auch für "beherrscht" (entwickler_beherrscht_programmiersprache)

Eine mögliche Lösung dafür ist: 
- Diese Bedingungen im Backend zu implementieren


KRITISCHE ENTSCHEIDUNG:
- bei Kunde habe ich eine CHECK Anweisung auf die Telefonnummer angewendet: Die sollte im Format +Nummer oder Vorwahl-Nummer (+491783750568 oder 0178-3750568)
- bei ist_mentor_von: Email1 ist die Email vom Mentor und Email2 ist die Email von einem beliebigen Nutzer (solange er kein Mentor ist)
