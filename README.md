# Triangle-Counting

## Introduzione
Lo scopo di questo progetto è di formulare un algoritmo che, dato un grafo restituisce il numero q<sub>3</sub> di 3-cliques esistenti al suo interno. Facciamo riferimento all'articolo "*Clique Counting in MapReduce: Algorithms and Experiments*", limitatamente al caso k=3 , e implementiamo secondo il paradigma *Map-Reduce* lo pseudocodice dell'algoritmo. Il codice è stato scritto con *Java* e *Spark* e viene utilizzato come strumento di supporto il database NoSQL *Neo4J*.


## Il dataset  
Dove trovarlo: https://snap.stanford.edu/data/loc-Gowalla.html  
Il grafo modella una struttura dati con relazioni indirette, e ha 196591 nodi, 950327 archi e 2273138 triangoli. Il sito però, rende disponibile il grafo diretto, quindi possiede 1900654 archi e i nodi di ogni arco sono separati da uno spazio.



## Presentazione dei file
Nella cartella *Github* si trovano i seguenti file/classi:  

| File        | Descrizione           |
|:---------- |:------------- |
| `GowallaNodi.csv` | file contenente i nodi del grafo |
| `GowallaArchi.csv` | file contenente gli archi del grafo -indiretto- |
| `Gowalla.txt` | file contenente gli archi del grafo diretto  |
| `ArcoGradiGowalla_pt1.txt` | prima suddivisione del file contenente gli archi e i gradi dei rispettivi nodi  |
| `ArcoGradiGowalla_pt2.txt` | seconda suddivisione del file contenente gli archi e i gradi dei rispettivi nodi |

| Classe        | Descrizione           |
|:---------- |:------------- |
| `Arco.java` | classe *wrapper* utilizzata per la preparazione dell'input per l'applicazione |
| `ContaTriangoli.java` | classe *main* dell'applicazione che lavora esclusivamente con *Spark* |
| `ContaTriangoli_NeoSpark.java` | classe *main* dell'applicazione che lavora congiuntamrnte con *Spark* e *Neo4j* |
| `Map2.java` | interfaccia che implementa Map2 |
| `Map3.java` | interfaccia che implementa Map3 |
| `Card.java` | interfaccia che conta le coppie |



## Indicazioni per l'uso
Per una maggiore leggibilità del grafo, con il seguente codice abbiamo posto come elemento separatore dei due nodi la virgola:

```
JavaRDD<String> gowalla = jsc.textFile("data/Gowalla_edges.txt");
gowalla = gowalla.map(x->new String(x.split("	")[0]+ "," + x.split("	")[1]));
gowalla.saveAsTextFile("Gowalla");
```
Per la creazione del grafo su *Neo4J* si richiede un file contenente la lista dei nodi del grafo. Per ottenerla, utilizziamo il seguente codice:

```
JavaRDD<String> dGrafo = jsc.textFile("data/Gowalla.txt");	
JavaRDD<String> dNodi = dGrafo.map(x -> new String(x.split(",")[0],1)).reduceByKey((x,y)->x+y).map(x -> new String(x._1 + "," + x._1));
dNodi.saveAsTextFile("GowallaNodi");
```
Con il seguente codice, facendo uso della classe wrapper, abbiamo ottenuto la lista di archi tali da rendere il grafo indiretto:
```
JavaRDD<String> dGrafo = jsc.textFile("data/Gowalla.txt");	
JavaRDD<Arco> dArco = dGrafo.map(x -> new Arco(x.split(",")[0],x.split(",")[1]));
List<Arco> A = dArco.collect();
List<String> Grafo = new ArrayList<String>();
 for (Arco a : A) {
  if (Integer.parseInt(a.getIdEntrata()) < Integer.parseInt(a.getIdUscita())) {
  Grafo.add(a.getIdEntrata() + "," + a.getIdUscita());
 }
JavaRDD<String> dGrafo1 = jsc.parallelize(Grafo);
dGrafo1.saveAsTextFile("GowallaArchi");
```
Una volta ottenuti i file `GowallaNodi.txt` e `GowallaArchi.txt`, li abbiamo trasformati in file `.csv` e utilizzando la libreria `apoc` di *Neo4j* li abbiamo caricati sul software.

## Due strade alternative 

Abbiamo scelto di seguire due strategie che forniscono in maniera diversa l'input per l'algoritmo *Clique Counting*. La prima genera l'input direttamente con *Spark*, mentre nella seconda è *Neo4j* che svolge la parte iniziale di preparazione per l'input dell'applicazione. Successivamente, una volta terminato l'algoritmo, *Neo4j* è stato usato nuovamente come strumento per validare i risultati ottenuti.

1. **Preparazione dell'input con *Spark***:  
Dopo aver caricato il file `Gowalla.txt` sull'applicazione `ContaTriangoli.java`, inizia la parte di codice che ha lo scopo di produrre in output una lista in cui in ogni riga si trova il generico elemento {u, v, d(u), d(v)}. 
Per fare ciò, abbiamo eseguito i seguenti passaggi:

| Passaggio        | Descrizione           |
|:---------- |:------------- |
| `Calcolo di:(NODO;GRADO)` | Al grafo diretto abbiamo applicato una funzione lambda che restituisce un oggetto in cui in chiave si trova il nodo in entrata, e in valore l'intero **1**; successivamente, mediante una `reduceByKey`, abbiamo ottenuto una lista in cui vi è in chiave il nodo, e in valore il suo grado; per semplicità, abbiamo poi convertito quest'ultimo in una stringa. |
| `Calcolo di:(ARCO;GRADI)` | Abbiamo poi creato due liste differenti in cui entrambe hanno come chiave l'arco, e come valore rispettivamente il grado del nodo in entrata e il grado del nodo in uscita. Con un join, intersecando per chiave le due liste, abbiamo ottenuto una lista in cui in chiave si trova l'arco, e in valore i gradi dei relativi nodi. Sempre per comodità, abbiamo poi convertito questo oggetto in una lista di stringhe. |


2. **Preparazione dell'input con *Neo4j***:
Dopo aver creato il grafo su *Neo4j*, abbiamo eseguito i seguenti passaggi: 

| Passaggio        | Descrizione           |
|:---------- |:------------- |
| `Calcolo di:(NODO;GRADO)` | Abbiamo eseguito una query per assegnare come attributo ad ogni nodo del grafo il relativo grado. Questa operazione è stata ottimizzata utilizzando il comando `node.degree()` della libreria `apoc`. | 
| `Calcolo di:(ARCO,GRADI)` | Per esportare la lista in cui il generico elemento è del tipo {u, v, d(u), d(v)}, abbiamo dato come argomento del comando `export.csv.query()` della libreria `apoc` la query che permette di ottenere questo oggetto. Il file `ArcoGradi.csv` risultante sarà l'input del codice contenuto nel file `ContaTriangoli_NeoSpark.java`(per permetterne il caricamento su *GitHub* è stato suddiviso nei due file `ArcoGradi_pt1.txt` e `ArcoGradi_pt2.txt`).|


## Implementazione dell'algoritmo con *Java* e *Spark*
L'algoritmo è diviso in tre round MapReduce:

**Round 1:**

**Map1:** data in input la lista di stringhe contenente ogni arco con accanto i gradi dei relativi nodi, con un'operazione di `filter` abbiamo selezionato gli archi che hanno grado del nodo in entrata strettamente minore del grado del nodo in uscita e abbiamo salvato questo oggetto nella `JavaRDD` di stringhe `dMap1_0`.
Successivamente, con una seconda operazione di `filter`, abbiamo selezionato gli archi che hanno grado del nodo in entrata uguale al grado del nodo in uscita e abbiamo eseguito un ulteriore `filter` che seleziona, di questi, solamente quelli che possiedono etichetta numerica del nodo in entrata inferiore a quella del nodo in uscita; abbiamo poi salvato questo oggetto nella `JavaRDD` di stringhe `dMap1_1`. Infine, abbiamo unito i due oggetti per ottenere tutti gli archi (u,v) tali che u &pr; v. Con lo scopo di ottenere &Gamma;<sup>+</sup>(u), abbiamo eseguito una `reduceByKey` sull'output precedente.


**Reduce 1**:













