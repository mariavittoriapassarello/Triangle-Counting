package gebd.progetto;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import org.apache.spark.api.java.function.PairFunction;

import scala.Tuple2;

public class NumeroArchi implements PairFunction<Tuple2<String, String>, String, Integer> {

	public Tuple2<String, Integer> call(Tuple2<String, String> t)throws Exception{
		
		
		String v = t._2;
		String[] numbers = v.split(",");
		int numeroArchi = 0;
		List<Tuple2<String, Integer>> output = new ArrayList<Tuple2<String, Integer>>();

	
		for(String number: numbers) {
		   numeroArchi ++;
			
		}
			
		return new Tuple2<String,Integer> (t._1, numeroArchi/2);
	}

	
}
