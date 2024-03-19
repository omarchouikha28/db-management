SQL VERSION: 3.40.0

NOTE:

- Ich habe alle Transaktionen mit //transactional "annotiert"

- Ich habe alle Methoden im Code mit Kommentare erklärt

- Ich habe eine ProgrammierspracheController Klassen erstellt als Hilfsklasse für die (post) addEntwickler methode.

- Für die addProjekt Methode (ProjektController Klasse):

  	*) Man muss für die Projekt-Tabelle eine Projektleiter_id eingeben und die ist gar nicht vorhanden
	-> meine Lösung ist einen default Wert "omar@chouikha.de" zu setzen. (Alternative: NOT NULL vom Projekt.Projektleiter_id zu entfernen. Aber wir dürfen die Datenbank gar nicht bearbeiten.)
	
	*) Die Kunde_id ist eigentlich die ID vom authentifizierten Nutzer.

- Die Curl-Befehele sind per Windows cmd nutzbar.