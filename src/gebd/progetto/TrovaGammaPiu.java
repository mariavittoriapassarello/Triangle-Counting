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
public class TrovaGammaPiu implements PairFunction<String, String, ArrayList<String>> {

	

	public Tuple2<String,ArrayList<String>> call(String Numeri){
		List<Tuple2<String, ArrayList<String>>> output = new ArrayList<Tuple2<String, ArrayList<String>>>();
		String[] VerticiGradi = Numeri.split(",");
		ArrayList<String> Vicini = new ArrayList<String>();
		//Vicini.add(VerticiGradi[1]);
		
		for(int i = 3; i< VerticiGradi.length;i = i+2) {
				
			
				int G1 = Integer.parseInt(VerticiGradi[1]);
				if( G1 < Integer.parseInt(VerticiGradi[i])) {
					Vicini.add(VerticiGradi[i-1]);
					Vicini.add(VerticiGradi[i]);
				}
					if( G1 == Integer.parseInt(VerticiGradi[i]) & Integer.parseInt(VerticiGradi[0]) < Integer.parseInt(VerticiGradi[i-1])) {
						Vicini.add(VerticiGradi[i-1]);
						Vicini.add(VerticiGradi[i]);
					}		
					
					output.add(new Tuple2<String, ArrayList<String>>(VerticiGradi[0],Vicini));
				
			}
		
		
		return new Tuple2<String, ArrayList<String>>(VerticiGradi[0],Vicini);
		
		
		
		
		
		
	}
}