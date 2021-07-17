package github.progetto;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import scala.Tuple2;

public class Map3 implements PairFlatMapFunction<Tuple2<String, String>, String, String> {
	
	public Iterator<Tuple2<String, String>> call(Tuple2<String, String> t) throws Exception {
		List<Tuple2<String, String>> output = new ArrayList<Tuple2<String, String>>();
		String v = t._2;
		String [] w = v.split(",");
		for (int i = 0; i<w.length; i++) {
			output.add(new Tuple2<String, String> (w[i], t._1));
			
		}
		
		return output.iterator();
			
	}
}
