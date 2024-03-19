- Ich habe die Beziehung Projektleiter_leitet_Projekt die Kardinalität [1,*] gegeben, da ein Projekt'LEITER' muss mindestens ein Projekt leiten (meine Meinung nach).
- Ich habe das Attribut Telefonnummer vom Kunden nicht als Primärschlüssel modelliert, da die Nummer nur einmalig (und nicht EINDEUTIG) vorkommen soll.
Es würde auch später mehr Sinn machen, Der Kunde hat schon den Primärschlüssel vom Nutzer vererbt, deshlab braucht die Entität keine weitere Schlüssel.
Einmalig könnte auch ein Hinweis sein, um zu zeigen, dass das Attribut nicht mehrwertig ist.
- Ich habe [0,*] als Kardinalität der Beziehung Spezialist_arbeitet_an_projekt gewählt, da ein Spezialist an beliebig vielen Projekten arbeiten kann.
- Ich habe die Attribute "fachliche Kompetenz" und "weitere Kompetenzen" vom Spezialisten bzw. Designer als Multivalued-Attribute modelliert, da ein Spezialist/Designer mehrere Kompetenzen haben kann. (*)
- Ich habe das Attribut "Name" für die Programmiersprache hinzugefügt, da eine Entity mindestens ein Attribut haben soll.

Zu "Bewertung":
- Ich habe sie erstmal als Beziehung modelliert, habe aber am Ende meine Meinung geändert, da ich für das optionale Attribut "Text" (also jetzt eine Entity) eine hat-Beziehung erstellen sollte
d.h bekommt jetzt die Beziehung Bewertung_hat_text eine [0,1] Kardinalität und umgekehrt [1,1], um die Kriterien von den optionalen Attribute zu sichern.
- Bei kunde_erfasst_Bewertung habe ich die Kardinalität [0,*] gewählt, da der Kunde beliebig viele Bewertungen erfassen kann. 
Umgekehrt wird eine Bewertung von einer einzigen Person erfasst -> [1,1]
- das Gleiche bei bewertung_gehört_zu_projekt 


- Zum Bepunktung-Attribut wird später in der Implementierung eine Bedingung zu den Werten aufgesetzt.
- Ich habe für jede hinzugefügte Entität ein Attribut beigefügt, da eine Entity mindestens ein Attribut haben soll.

Zu der ternären Beziehung zwischen Entwickler, Designer und Spezialist:
- Die Implimentierung von "entweder oder" im ERM ist gar nicht möglich.
- Der Pfeil ist umgekehrt, da die IST-Beziehung injuktiv ist und die Entität "Spezialist" zur Generalisierung dient. 
Also ein Entwickler bzw. Designer müssen Spezialisten sein aber ein Spezialist muss nicht unbedingt Entwickler oder Designer sein.


Zu (*) :
Ich weiß nicht, ob wir andere Schreibweisen für die optionale/multivalued Attribute benutzen können, deshalb habe ich wie bei der Vorlesung für jedes Attribut eine Hat-Beziehung + Entity erstellt:
- bei "weitere Kompetenzen" habe ich als Kardinalität [0,*] gewählt, da ein Designer beliebig viele Kompetenzen haben kann und die Kompetenzen können zu beliebig vielen Designern gehören.
- das Gleiche gilt bei "fachliche Kompetenz" aber ein Spezialist soll mindestens eine fachliche Kompetenz haben, deshalb habe ich die Kardinalität [1,*] gewählt.