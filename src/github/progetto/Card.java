package github.progetto;

import org.apache.spark.api.java.function.PairFunction;

import scala.Tuple2;

public class Card implements PairFunction<Tuple2<String, String>, String, Integer> {

	public Tuple2<String, Integer> call(Tuple2<String, String> t)throws Exception{
		
		
		String v = t._2;
		String[] numbers = v.split(",");
		int numeroArchi = 0;

		for(String number: numbers) {
		   numeroArchi ++;
			
		}
			
		return new Tuple2<String,Integer> (t._1, numeroArchi/2);
	}

	
}
