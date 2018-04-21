package edu.cmu.andrew.mm6;
import java.io.IOException;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;


public class RapesPlusRobberiesMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text> {
    
    @Override
    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
        
        // Get line from input file. This was passed in by Hadoop as value.
        // We have no use for the key (file offset) so we are ignoring it.
        String line = value.toString();
        int robbery = 0;
        int rape = 0;
        // Count the number of ROBBERY and RAPE
        if (line.contains("ROBBERY")) {
            robbery = 1;
        }
        if (line.contains("RAPE")) {
            rape = 1;
        }
        
        // write to intermediate value
        // reduce will be called with
        // (keyword, string) 
        // the key is "robberyandrape"
        // the string contains the value of number of ROBBERY and RAPE separated by comma
        if (rape != 0 || robbery != 0) {
            String result = "" + robbery + "," + rape;
            output.collect(new Text("robberyandrape"), new Text(result));
        }
    }

}
