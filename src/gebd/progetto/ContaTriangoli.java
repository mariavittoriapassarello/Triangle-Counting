package gebd.progetto;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;

import scala.Tuple2;

public class ContaTriangoli {
	public static void main(String[] args) {

		Logger.getLogger("org").setLevel(Level.ERROR);
		Logger.getLogger("akka").setLevel(Level.ERROR);

		Logger.getLogger("org").setLevel(Level.ERROR);
		Logger.getLogger("akka").setLevel(Level.ERROR);
		SparkConf sc = new SparkConf();
		sc.setAppName("Conta Triangoli");
		sc.setMaster("local[*]");
		JavaSparkContext jsc = new JavaSparkContext(sc);

		// ******INIZIO******
		JavaRDD<String> dGrafo1 = jsc.textFile("data/gowallaconvirgole.txt");
//		JavaRDD<String> dGrafo = jsc.textFile("data/p2p-Gnutella04.txt");
//		JavaRDD<String> dGrafo = jsc.textFile("data/grafo8nodi.txt");

        //usare questo pezzo di codice se i vertici di ogni arco sono separati da virgola per passare da un grafo indiretto ad un grafo diretto
//		JavaRDD<String> dGrafo1 = dGrafo.map(x -> new String(x.split(",")[1] + "," + x.split(",")[0])).union(dGrafo);

		// **PREPARAZIONE DEL GRAFO**
		//creo coppia kv con chiave ogni vertice e valore con numero 1
		JavaPairRDD<String, Integer> dGrado_0 = dGrafo1
				.mapToPair(x -> new Tuple2<String, Integer>(x.split(",")[0], 1));

		//creo coppia kv con chiave vertice e valore grado
		JavaPairRDD<String, Integer> dGrado_1 = dGrado_0.reduceByKey((x, y) -> x + y);

		//creo coppia kv con chiave vertice e valore grado in formato string string
		JavaPairRDD<String, String> dGrado_2 = dGrado_1
				.mapToPair(z -> new Tuple2<String, String>(z._1.split(",")[0], Integer.toString(z._2)));

		//creo una coppia kv con chiave il vertice in entrata e valore l' arco
		JavaPairRDD<String, String> dChiaveArco0 = dGrafo1
				.mapToPair(x -> new Tuple2<String, String>(x.split(",")[0], x));

		//creo una coppia kv con chiave il vertice in uscita e valore l' arco
		JavaPairRDD<String, String> dChiaveArco1 = dGrafo1
				.mapToPair(x -> new Tuple2<String, String>(x.split(",")[1], x));

		//in output ottengo una coppia che contiene in chiave l' arco e in valore il grado del vertice in entrata
		JavaPairRDD<String, String> dArcoG0 = dChiaveArco0.join(dGrado_2)
				.mapToPair(w -> new Tuple2<String, String>(w._2._1, w._2._2));
		//in output ottengo una coppia che contiene in chiave l' arco e in valore il grado del vertice in uscita
		JavaPairRDD<String, String> dArcoG1 = dChiaveArco1.join(dGrado_2)
				.mapToPair(w -> new Tuple2<String, String>(w._2._1, w._2._2));

		//interseco per chiave i due output
		JavaPairRDD<String, Tuple2<String, String>> dArcoGradi_0 = dArcoG0.join(dArcoG1);

		JavaRDD<String> dArcoGradi_1 = dArcoGradi_0.map(x -> new String(x._1 + "," + x._2._1 + "," + x._2._2));
		
		// **MAP1**
		JavaRDD<String> prova0 = dArcoGradi_1.filter(x-> Integer.parseInt(x.split(",")[2]) < Integer.parseInt(x.split(",")[3]));
		JavaRDD<String> prova1 = dArcoGradi_1.filter(x-> Integer.parseInt(x.split(",")[2]) == Integer.parseInt(x.split(",")[3])).filter(x-> Integer.parseInt(x.split(",")[0]) < Integer.parseInt(x.split(",")[1]));
		JavaRDD<String> dMap1_1 = prova0.union(prova1);
		
		// **PREPARAZIONE AL REDUCE 1**

		//creo coppia kv con chiave vertice in entrata e relativo grado e valore tutti i vertici vicini con accanto relativi grado
		//in pratica è l' insieme Gamma_u in cui ogni elemento ha il grado alla sua destra
		JavaPairRDD<String, String> dGammaPiu_0 = dArcoGradi_1
				.mapToPair(x -> new Tuple2<String, String>(x.split(",")[0] + "," + x.split(",")[2],
						x.split(",")[1] + "," + x.split(",")[3]))
				.reduceByKey((x, y) -> x + "," + y);

		JavaRDD<String> dGammaPiu_1 = dGammaPiu_0
				.map(x -> new String(x._1 + "," + x._2));

		//ecco la prima interfaccia complessa ottengo l' insime dGammaPiu_u per ogni u è l' input del reduce1
		JavaPairRDD<String, ArrayList<String>> dGammaPiu_2 = dGammaPiu_1
				.mapToPair(new TrovaGammaPiu());

		// **REDUCE1**
		
		JavaPairRDD<String, ArrayList<String>> dReduce1_0 = dGammaPiu_2.filter(x -> x._2.size() / 2 >= 2);

		JavaRDD<String> dReduce1_1 = dReduce1_0.map(
				x -> new String(x._1 + "," + x._2.toString().replace("[", "").replace("]", "").replaceAll(" ", "")));

		// **MAP2**

		//do in input gli archi tali che u "<" v e a questi aggiungo un $ a destra valore della tupla e questo mi ricorerà dopo che 
		//u e v creano un arco
		JavaPairRDD<String, String> dMap2_0 = dMap1_1
				.mapToPair(x -> new Tuple2<String, String>(x.split(",")[0] + "," + x.split(",")[1], "$"));

		
		JavaPairRDD<String, String> dMap2_1 = dReduce1_1.flatMapToPair(new Map2());

		// **REDUCE2**

		//questo output aggrega alle tuple che hanno stessa chiave ovvero stessa coppia xi,xj i vertici u
		JavaPairRDD<String, String> dReduce2_0 = dMap2_1.reduceByKey((x, y) -> x + "," + y);

		//questo output seleziona gli output del passo precedente che hanno chiave che è un arco
		JavaPairRDD<String, String> dReduce2_1 = dReduce2_0.join(dMap2_0).mapToPair(x -> new Tuple2<String, String>(x._1, x._2._1));
		
		//JavaPairRDD<String, String> dReduce2_2 = dReduce2_1.mapToPair(x -> new Tuple2<String, String>(x._1, x._2._1));

		// **MAP3**
		JavaPairRDD<String, String> dMap3_0 = dReduce2_1.flatMapToPair(new Map3());

		// **REDUCE3**

		JavaPairRDD<String, String> dReduce3_0 = dMap3_0.reduceByKey((x, y) -> x + "," + y);
		
		JavaPairRDD<String,Integer> dReduce3_1 = dReduce3_0.mapToPair(new NumeroArchi());

		Integer numeroTriangoli = dReduce3_1.values().reduce((x, y) -> x + y);

		System.out.println(numeroTriangoli);

     	Scanner scan;
		System.out.println("Premi invio per concludere l'esecuzione");
		scan = new Scanner(System.in);
		scan.next();


	}

}
