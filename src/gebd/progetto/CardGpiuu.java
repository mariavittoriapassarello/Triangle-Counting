package gebd.progetto;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.api.java.function.PairFunction;

import scala.Tuple2;


/*
 * data in input una stringa nella forma p1,p2,p3,p4 restituisce tutte le
 * possibili coppie pi,pj dove i !=j senza ripetizione con valore 1
 */
public class CardGpiuu implements PairFunction<String, String, ArrayList<String>> {

	

	public Tuple2<String,ArrayList<String>> call(String Numeri){
		List<Tuple2<String, ArrayList<String>>> output = new ArrayList<Tuple2<String, ArrayList<String>>>();
		String[] Vertici = Numeri.split(",");
		ArrayList<String> Vicini = new ArrayList<String>();
		//Vicini.add(VerticiGradi[1]);
		
		for(int i = 1; i< Vertici.length;i++) {

					Vicini.add(Vertici[i]);
		}
;
				

		
		
		return new Tuple2<String, ArrayList<String>>(Vertici[0],Vicini);
		
		
		
		
		
		
	}
}