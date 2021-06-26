package gebd.progetto;

import java.util.ArrayList;
import java.util.List;

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
		sc.setAppName("Analizza Incidenti Stradali");
		sc.setMaster("local[*]");
		JavaSparkContext jsc = new JavaSparkContext(sc);

		SparkSession spark = SparkSession.builder().appName("Esempio uso Dataframe").config(jsc.getConf())
				.getOrCreate();

		// ******INIZIO******
		JavaRDD<String> dGrafo = jsc.textFile("data/Gowalla_edges.txt");
//		JavaRDD<String> dGrafo = jsc.textFile("data/p2p-Gnutella04.txt");
//		JavaRDD<String> dGrafo = jsc.textFile("data/grafo8nodi.txt");

		JavaRDD<Arco> dPreparaGrafo = dGrafo.map(b -> new Arco((b.split("	")[0]), b.split("	")[1]));
		List<Arco> alpha = dPreparaGrafo.collect();
		List<String> Grafo1 = new ArrayList<String>();
		for (Arco a : alpha) {
			Grafo1.add(a.getIdEntrata() + "," + a.getIdUscita());
		}
		JavaRDD<String> dGrafo1 = jsc.parallelize(Grafo1);

//		JavaRDD<String> dGrafo1 = dGrafo.map(x -> new String(x.split(",")[1] + "," + x.split(",")[0])).union(dGrafo);

//		JavaPairRDD<String, String> dKVertice1VVertice2 = dGrafo1
//				.mapToPair(x -> new Tuple2<String, String>(x.split(",")[0], x.split(",")[1]));

//		JavaPairRDD<String, String> dGammau = dKVertice1VVertice2.reduceByKey((x, y) -> x + "," + y);

		JavaPairRDD<String, Integer> dKVerticeVUno = dGrafo1
				.mapToPair(x -> new Tuple2<String, Integer>(x.replaceAll("()", "").split(",")[0], 1));

		JavaPairRDD<String, Integer> dKVerticeVGrado = dKVerticeVUno.reduceByKey((x, y) -> x + y);

		JavaPairRDD<String, String> dKVerticeVGrado_1 = dKVerticeVGrado
				.mapToPair(z -> new Tuple2<String, String>(z._1.split(",")[0], Integer.toString(z._2)));

		JavaPairRDD<String, String> dChiaveArco = dGrafo1
				.mapToPair(x -> new Tuple2<String, String>(x.split(",")[0], x));

		JavaPairRDD<String, String> dChiaveArco2 = dGrafo1
				.mapToPair(x -> new Tuple2<String, String>(x.split(",")[1], x));

		JavaPairRDD<String, String> jj = dChiaveArco.join(dKVerticeVGrado_1)
				.mapToPair(w -> new Tuple2<String, String>(w._2._1, w._2._2));
		JavaPairRDD<String, String> ii = dChiaveArco2.join(dKVerticeVGrado_1)
				.mapToPair(w -> new Tuple2<String, String>(w._2._1, w._2._2));

		JavaPairRDD<String, Tuple2<String, String>> dArcoGradi_0 = jj.join(ii);

		JavaRDD<String> dArcoGradi_1 = dArcoGradi_0.map(x -> new String(x._1 + "," + x._2._1 + "," + x._2._2));

		// **MAP1**

		JavaRDD<Arco> dArco = dArcoGradi_1
				.map(b -> new Arco((b.split(",")[0]), b.split(",")[1], b.split(",")[2], b.split(",")[3]));

		List<Arco> a = dArco.collect();
		List<String> GrafoRidotto = new ArrayList<String>();

		for (Arco i : a) {
			if (Integer.parseInt(i.getDegreeEntrata()) < Integer.parseInt(i.getDegreeUscita())) {
				GrafoRidotto.add(i.getIdEntrata() + "," + i.getIdUscita());
			}
			if (Integer.parseInt(i.getDegreeEntrata()) == Integer.parseInt(i.getDegreeUscita())
					&& Integer.parseInt(i.getIdEntrata()) < Integer.parseInt(i.getIdUscita())) {
				GrafoRidotto.add(i.getIdEntrata() + "," + i.getIdUscita());
			}

		}
		JavaRDD<String> dGrafoRidotto = jsc.parallelize(GrafoRidotto);

		// **REDUCE1**

		JavaPairRDD<String, String> dVerticeConGradoViciniConGradi = dArcoGradi_1
				.mapToPair(x -> new Tuple2<String, String>(x.split(",")[0] + "," + x.split(",")[2],
						x.split(",")[1] + "," + x.split(",")[3]))
				.reduceByKey((x, y) -> x + "," + y);

		JavaRDD<String> dVerticeConGradoViciniConGradi_1 = dVerticeConGradoViciniConGradi
				.map(x -> new String(x._1 + "," + x._2));

		JavaPairRDD<String, ArrayList<String>> duGammaPiu = dVerticeConGradoViciniConGradi_1
				.mapToPair(new TrovaGammaPiuGradi());

		JavaPairRDD<String, ArrayList<String>> duGammaPiu_Ridotto = duGammaPiu.filter(x -> x._2.size() / 2 >= 2);

		JavaRDD<String> duGammaPiu_Ridotto_2 = duGammaPiu_Ridotto.map(
				x -> new String(x._1 + "," + x._2.toString().replace("[", "").replace("]", "").replaceAll(" ", "")));

		// **MAP2**

		JavaPairRDD<String, String> dMap2_0 = dGrafoRidotto
				.mapToPair(x -> new Tuple2<String, String>(x.split(",")[0] + "," + x.split(",")[1], "$"));

		JavaPairRDD<String, String> dMap2_1 = duGammaPiu_Ridotto_2.flatMapToPair(new Map2());

		// **REDUCE2**

		JavaPairRDD<String, String> dReduce2_0 = dMap2_1.reduceByKey((x, y) -> x + "," + y);

		JavaPairRDD<String, Tuple2<String, String>> dReduce2_1 = dReduce2_0.join(dMap2_0);
		JavaPairRDD<String, String> dReduce2_2 = dReduce2_1.mapToPair(x -> new Tuple2<String, String>(x._1, x._2._1));

		// **MAP3**
		JavaPairRDD<String, String> dMap3_0 = dReduce2_2.flatMapToPair(new Map3());

		// **REDUCE3**

		JavaPairRDD<String, String> dReduce3_0 = dMap3_0.reduceByKey((x, y) -> x + "," + y);
		JavaRDD<String> dReduce3_1 = dReduce3_0.map(x -> new String(x._1 + "," + x._2));
		JavaPairRDD<String, ArrayList<String>> dReduce3_2 = dReduce3_1.mapToPair(new CardGpiuu());

		JavaPairRDD<String, Integer> dReduce3_3 = dReduce3_2
				.mapToPair(x -> new Tuple2<String, Integer>(x._1, ((x._2.size() / 2))));

		Integer cliqueCount = dReduce3_3.map(z -> z._2).reduce((x, y) -> x + y);

		System.out.println(cliqueCount);

		// ******LE STAMPE******

		// System.out.println(dGrafo1.collect());
		// System.out.println(dKVertice1VVertice2.collect());

		// System.out.println(dKVerticeVGrado.collect());
		// System.out.println(dChiaveArco.collect());
		// System.out.println(dChiaveArco2.collect());

		// System.out.println("jj"+ jj.collect());
		// System.out.println("jj1"+ jj1.collect());
		// System.out.println("jj1"+ jj1.keys().collect());
		// System.out.println("ii1"+ ii1.collect());
		// System.out.println("ii1"+ ii1.keys().collect());

		// System.out.println(dKV1_VV2_GV2.collect());
		// System.out.println("dDesiderio2"+ dDesiderio2.collect());
		// System.out.println("Desiderio: " + desiderio3.collect());
		// System.out.println("Coppie Map 1 senza Classe Wrapper:" +
		// System.out.println(dGrafoRidotto.collect());
		// System.out.println(dGrafoRidotto.count());
		// System.out.println(dR1_0.collect());
		// System.out.println(dVerticeViciniConGradi.collect());
		// System.out.println(dR1_1.collect());
		// System.out.println(dVerticeConGradoViciniConGradi_2.collect());
		// System.out.println(duGammaPiu.collect());
		// System.out.println(duGammaPiu_Ridotto.collect());
		// System.out.println(dMap2_0.collect());
		// System.out.println(dMap2_1.collect());

		// System.out.println("Chiavi reduce 1: " + dReduce1.collect());
		// System.out.println("Reduce 1: " + dReduce2.collect());
		// System.out.println(dMap2_1.take(20));
		// System.out.println(dMap2_2.collect());
		// System.out.println(dMap2_4.collect());
		// System.out.println("Primo if: " + dMap2_0.collect());
		// System.out.println("Secondo if: " + dMap2_4.collect());
		// System.out.println(dReduce2_0.collect());
		// System.out.println(dReduce2_0.values().collect());
		// System.out.println(dReduce2_1.collect());
		// System.out.println(dReduce2_2.collect());
		// System.out.println(dMap3_0.collect());
		// System.out.println(dReduce3_0.take(20));
		// System.out.println(dReduce3_2.take(20));
		// System.out.println(dReduce3_3.take(20));

		// GRAPHFRAME

	}

}
