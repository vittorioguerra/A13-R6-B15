# Testing_Challenge_Game (by UNINA)- Ver. A13-R6-B15
Web App a supporto dell'insegnamento del Software Testing attraverso la Gamification. 

Questa Web App è descritta anche nel seguente articolo scientifico: 
Anna Rita Fasolino, Caterina Maria Accetto, Porfirio Tramontana:
"Testing Robot Challenge: A Serious Game for Testing Learning". Gamify@ISSTA 2024: 26-29, https://doi.org/10.1145/3678869.368568

# Descrizione 
The Testing_Challenge_Game Web Application was developed with the contributions of the students of the University of Naples Federico II in Italy, enrolled in the Software Architecture Design (SAD) courses held by the Prof. Anna Rita Fasolino in the academic years 2022-23, 2023-24, 2024-25.
It has  an extremely ambitious goal: to enhance the importance of testing, a discipline often mistreated and not explored in depth in university courses, through the innovative strategy of gamification which, as the term suggests, consists in using elements borrowed from games but in non-playful contexts. The result of the application of this mechanism was the design and subsequent development of the interactive game: "Man vs Automated Testing Tools challenges" which sees the students, from here on called players, compete, with tests designed using the JUnit framework, against robots (Randoop or EvoSuite) capable of automatically generating such tests; the challenge can be considered won by the participant capable of completing a certain coverage objective.

# Stato del progetto
Il Progetto è in fase di sviluppo ed evoluzione. Questo progetto è stato forkato dal Repository Master sito in: https://github.com/Testing-Game-SAD-2023/A13

# Contributori del Branch/ Fork corrente 
Vittorio Guerra – M63000000

Fabrizio Imparato – M63001758

Tommaso Rivieccio – M63001845


# Funzionalità dell'Applicazione 
L'applicazione prevede due tipi di utente (giocatore ed amministratore) che dovranno preliminarmente registrarsi al gioco con ruoli diversi. 
Il giocatore può:
- Giocare una Sfida di Testing contro i Robot Evosuite o Randoop
- Giocare una Sfida Multi-livello (Scalata) contro i Robot Evosuite o Randoop
- Allenarsi nella scrittura di Test JUnit per testare classi di codice in Java
- Visualizzare il proprio Profilo Utente, accedendo ai propri achievement (traguardi raggiunti)

L'amministratore del Gioco può:
- Caricare nuove classi in Java su cui i giocatori potranno allenarsi e sfidare i Robot
- Visualizzare l'elenco dei Giocatori iscritti e relativi traguardi


# Screenshot dell'applicazione e Video dimostrativi delle funzionalità principali.
### Funzionalità di aggiunta di test da parte di più robot per una singola classe
https://github.com/user-attachments/assets/c6dae664-7c64-49f5-b168-c45c2a2bd1b0


# Come iniziare
1. Assicurarsi di aver correttamente clonato sul proprio workspace, il repository A13 di riferimento; si consiglia, innanzitutto, di 
scaricare, al seguente indirizzo https://git-scm.com/downloads, ed installare, sul proprio computer, Git (per una guida completa all'installazione fare riferimento al seguente indirizzo: https://github.com/git-guides/install-git).
Una volta aver completato l'installazione, sarà possibile clonare i repository GitHub sulla propria macchina:
<pre>
1) Aprire Git Bash (shell di Git)
2) Posizionarsi nella cartella all'interno della quale si desidera aggiungere il repository clonato
3) Andare alla pagina del repository che si intende clonare -> https://github.com/Testing-Game-SAD-2023/A13
4) Cliccare sul bottone: "< > Code" e copiare l' URL
5) Utilizzare il comando:
     git clone URL COPIATO
6) Premere invio
</pre>
![How to clone repository by URL](https://github.com/Testing-Game-SAD-2023/A13/blob/main/Immagini_installazione/git_clone.png)

2. Assicurarsi di avere Java e Maven installati correttamente sulla propria macchina e di aver settato le variabili d'ambiente.


# Come installare

### Step 1
Scaricare e installare Docker Desktop per il proprio sistema operativo: se già installato correttamente sulla macchina passare al Passo 2.

NOTA: sebbene il Windows Subsystem for Linux wsl2, una funzionalità che consente di eseguire un ambiente Linux all'interno del sistema operativo Windows garantendo la compatibilità tra Docker Desktop e Windows, normalmente venga installato e aggiornato durante l'installazione di Docker Desktop, vi sono casi in cui questo step non venga effettuato correttamente in maniera automatica (all'apertura di Docker è presente un messaggio di errore "WSL Error"), bisogna quindi installare manualmente wsl tramite i seguenti step:
<pre>
a) avviare il prompt dei comandi
b) digitare wsl --install e premere invio
c) digitare wsl --update e premere invio
d) riavviare la macchina
</pre>
1) Procedere all'installazione di Docker Desktop: https://www.docker.com/products/docker-desktop/
2) All'avvio di Docker Desktop nella sezione Settings -> General controllare che sia spuntata l'opzione "Use the WSL 2 based engine"

### Step 1B
Nel caso non sia la prima installazione, per la disinstallazione utilizzare "uninstaller.bat" mentre si ha in esecuzione Docker, in questo modo si elimina qualunque file presente su Docker.

### Step 2
Avviare lo script "installer.bat" se si sta usando una distribuzione Windows oppure "installermac.sh" nel caso si utilizzi macOS o una distro di Linux.
Per MacOS - eseguire nella cartella dove è presente il file ”installermac.sh” il comando "chmod +x installermac.sh" per renderlo eseguibile, e poi "./installermac.sh" per eseguirlo.
Tali script dovranno essere avviati unicamnete con Docker in esecuzione, altrimenti l'installazione non partirà. Saranno effettuate le seguenti operazioni:
1) Creazione della rete "global-network" comune a tutti i container.
2) Creazione del volume "VolumeT9" comune ai Task 1 e 9, del volume "VolumeT8" comune ai Task 1 e 8 e del volume "Volume" condiviso tra tutti i Task
3) Creazione dei singoli container in Docker desktop.
4) Esecuzione dei file di installazione nei container del task T8 e T7
5) Avvio dei container
6) Comandi di inizializzazione del database del task T1
   
NOTA: il container relativo al Task 9 ("Progetto-SAD-G19-master") si sospenderà autonomamente dopo l'avvio. Esso viene utilizzato solo per "popolare" il volume "VolumeT9" condiviso con il Task 1.

# Risoluzione di Problemi ricorrenti
Nel corso dell'installazione dello script: "installer.bat", è possibile incappare nell'errore:
<pre>
E: invalid operation update
</pre>
dovuto al modo in cui Windows e Linux memorizzano i caratteri di fine riga (CRLF Windows mentre LF Linux); per risolvere il problema è indispensabile switchare dalla codifica CRLF -> a quella LF.
Un modo rapido prevede di effettuare la modifica manualmente utilizzando come editor di testo Notepad++:
<pre>
Edit => EOL Conversion => Unix(LF) dopodichè salvare
</pre>
![Errore in fase di installazione](https://github.com/Testing-Game-SAD-2023/A13/blob/main/Immagini_installazione/errore.png)
(https://stackoverflow.com/questions/55258430/e-invalid-operation-update-error-while-running-shell-scripts-in-wsl)
# Passi opzionali per esporre l'applicazione su un indirizzo pubblico

# A: Installazione NGROK
Nel caso sia stato già installato basterà avviare il container e collegarsi all'indirizzo fornito in precedenza

 __PASSO A.1__:
Registrazione presso il sito: https://ngrok.com/

 __PASSO A.2__:
Accesso alla Dashboard: https://dashboard.ngrok.com/get-started

 __PASSO A.3__:
Scelta dell'agente (si consiglia Docker).

 __PASSO A.4__:
Inserire il comando nel prompt sostituendo il __*token*__ e il __*dominio statico*__ fornito:

    docker run --net=host -it -e NGROK_AUTHTOKEN=TOKEN ngrok/ngrok:latest http --domain= DOMINIO 80
![How to set ngrok](https://github.com/Testing-Game-SAD-2023/A13/blob/main/Immagini_installazione/ngrok.png)
A questo punto si avrà l'indirizzo pubblico come risposta nel prompt dei comandi.

*__NB__*: il comando può essere copiato direttamente dalla dashboard di Ngrok, si consiglia di utilizzare il dominio di tipo statico

# Breve Descrizione delle Modifiche e delle Nuove Feature aggiunte nel repository corrente
In questa nuova versione, è stato modificato il container T1 per una maggiore generalizzazione nella gestione dei robot. Per evitare problemi legati alla ridondanza dei dati, è stato creato un nuovo volume condiviso tra tutti i container. Questo ha reso necessario l'adozione di un nuovo filesystem, che facilita una gestione più efficace, al momento, delle classi e dei test da parte di più robot.

Inoltre, è stata introdotta una nuova funzionalità nella pagina "Add Class and Test". Ora gli utenti hanno la possibilità di caricare solo una classe, caricare più test contemporaneamente, oppure caricare singoli test. Prima, infatti, era necessario caricare tutti i test associati a una singola classe in un'unica operazione.

Per l'elenco delle modifiche, si può consultare questo link: ![Modifiche effettuate](https://github.com/vittorioguerra/A13-R6-B15/blob/main/Documentazione_2024/Modifiche%20Effettuate.md)

# Documentazione di Progetto della Versione corrente
Di seguito si riporta il link alla cartella con la documentazione completa e i diagrammi: ![Documentazione versione corrente](https://github.com/vittorioguerra/A13-R6-B15/tree/main/Documentazione_2024)
