# Planificator de task-uri multithreaded

## Scop
Folosirea și acomodarea cu sincronizarea de thread-uri folosind Java Threads, mecanismele Thread.wait(), Syncronized etc.

## Implementare
In acest repository se afla 3 clase:
- MyDispacher - in aceasta clasa am implementat metoda addTask() care, in functie de tipul algoritmului,
va planifica task-urile conform cerintei;
- MyHost - un host va avea 4 atribute: un priority queue pentru stocarea task-urilor in ordinea prioritatii,
o variabila care retine task-ul curent, o variabila care retine timpul ramas pana la golirea cozii si una
pentru a retine ultima data cand a fost inceput un task de catre host.
  - Metoda run(): host-ul va astepta pana este intrerupt/pana intampina un task in coada de prioritati. Acesta va "rula" task-ul, simuland acest lucru prin folosirea metodei wait() (notify-ul va fi dat din metoda addTask() din aceeasi clasa). Daca wait-ul s-a terminat devreme (left > 0), inseamna ca task-ul curent a fost preemptat, deci va fi adaugat inapoi in coada de prioritati. Atlfel, apeleaza metoda finish() pe task, iar host-ul va trece la urmatorul task.
  - Metoda addTask(): host-ul va adauga direct task-ul in coada de prioritati, iar daca exista deja un task care
ruleaza si acesta trebuie preemptat, va apela notify() pe thread-ul care ruleaza task-ul curent.
  - Metoda getWorkLeft(): returneaza timpul ramas pana la golirea cozii de prioritati, calculand si adaugand
si timpul ramas pentru task-ul curent.

Ideea pentru calcularea timpului ramas pana la golirea cozii de prioritati este urmatoarea:
- cand un task este adaugat in coada, adaug durata lui de rulare automat
- daca acesta devine task-ul curent, scad din workLeft durata lui de rulare, deoarece aceasta va fi calculata din nou in metoda getWorkLeft()
- daca task-ul este preemptat, atunci trebuie sa adun la loc darata ramas de rulare la workLeft (altfel, atunci cand el redevine task-curent, workLeft va fi mai mic decat ar trebui)
    TaskComparator - comparator pentru priority queue-ul din clasa MyHost, care compara task-urile dupa prioritate
