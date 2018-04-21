package edu.cmu.andrew.mm6;

import java.io.IOException; 
import java.util.Iterator;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer; 
import org.apache.hadoop.mapred.Reporter;

public class RapesPlusRobberiesReducer extends MapReduceBase implements Reducer<Text, Text, Text, Text> {
    @Override
    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, 
                    Text> output,Reporter reporter) throws IOException {
        int robbery = 0;
        int rape = 0;
        
        // from the list of values, aggregate the results and calculate
        // the total number of robbery and rape
        while (values.hasNext())
        {
                String[] tmp = values.next().toString().split(",");
                robbery += Integer.parseInt(tmp[0]);
                rape += Integer.parseInt(tmp[0]);
        }
       
        int total = robbery + rape;
        String result = total+ " (" + rape + " + " + robbery + ")";
        
        // emit (key = "", value = total (rape + robbery))
        output.collect(new Text(""), new Text(result));
    }

}
