package github.progetto;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import scala.Tuple2;

public class Map2 implements PairFlatMapFunction<String, String, String> {
	public Iterator<Tuple2<String, String>> call(String t){
		
		
	String[] v = t.split(",");
		
	List<Tuple2<String, String>> output = new ArrayList<Tuple2<String, String>>();
	
		for(int i = 2; i<v.length;i = i+2) {
			for(int j = i+2; j<v.length; j = j+2) 	{
				if(Integer.parseInt(v[i])< Integer.parseInt(v[j])) {
					output.add(new Tuple2<String, String> (v[i-1]+","+ v[j-1], v[0]));
				}
				
				if(Integer.parseInt(v[i]) == Integer.parseInt(v[j])) {
					if(Integer.parseInt(v[i-1])<Integer.parseInt(v[j-1])) {
						output.add(new Tuple2<String, String> (v[i-1]+","+ v[j-1], v[0]));
					}
					if(Integer.parseInt(v[i-1])>Integer.parseInt(v[j-1])) {
						output.add(new Tuple2<String, String> (v[j-1]+","+v[i-1],v[0]));
					}
				}
				
				if(Integer.parseInt(v[i])> Integer.parseInt(v[j])) {
					output.add(new Tuple2<String, String> (v[j-1]+","+ v[i-1], v[0]));
				}
				
			}
		
			
		}
		return output.iterator();
	}
	
}
				