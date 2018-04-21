package edu.cmu.andrew.mm6;

import java.io.IOException;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class OaklandCrimeStats extends Configured implements Tool{

    /**
     * Mapper class emits <key, value> 
     * if find a match, will emit <"Total number of assault", one>
     * */
    public static class OaklandCrimeStatsMap extends Mapper<LongWritable, Text, Text, IntWritable> {
        private final static IntWritable one = new IntWritable(1);
        private Text word = new Text();

        @Override
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException
        {
            // ignore the first header row
            String line = value.toString();
            if (line.startsWith("X")) {
                return;
            }

            String[] data = line.split("\\s"); 

            // get information from the data
            // The first two columns (X,Y) represent State Plane (projected, rectilinear) coordinates (measured
            // in feet) specifying the location of the crime.
            // The third column is the time.
            // The fourth column is a street address.
            // The fifth column is the type of offense (aggravated assault, Robbery, Rape, Etc.)
            // The sixth column is the date.
            // The seventh column is the 2000 census tract. 
            float x = Float.parseFloat(data[0]);
            float y = Float.parseFloat(data[1]);

            // check if the crime occurred within
            // 200 meters of 3803 Forbes Avenue in Oakland. 
            // This location has the (X,Y) coordinates of (1354326.897,411447.7828).
            // 1 feet = 0.3048 meter
            // Use the coordinates in meters and the Pythagorean theorem to check.
            boolean inRange = Math.pow(((x-1354326.897)* 0.3048), 2)  + Math.pow(((y-411447.7828) * 0.3048), 2) <= Math.pow(200, 2);

            // check crime type and write to intermediate result 
            if (line.contains("ASSAULT") && inRange) {
                word.set("Total number of assault");
                context.write(word, one);
            }
        }
    }
    
    
    public static class OaklandCrimeStatsReducer extends Reducer<Text, IntWritable, Text, IntWritable>    
    {
        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException
        {
                int sum = 0;
                for(IntWritable value: values)
                {
                        sum += value.get();
                }
                
             // emit (key = mykeyword, value = sum of number)
            context.write(key, new IntWritable(sum));
        }
            
    }
    
    public int run(String[] args) throws Exception  {
        
        Job job = new Job(getConf());
        job.setJarByClass(OaklandCrimeStats.class);
        job.setJobName("Oakland Crime Stats");
        
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        
        job.setMapperClass(OaklandCrimeStatsMap.class);
        job.setReducerClass(OaklandCrimeStatsReducer.class);
        
        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);
        
        
        FileInputFormat.setInputPaths(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        
        boolean success = job.waitForCompletion(true);
        return success ? 0: 1;
    }

    public static void main(String[] args) throws Exception {
        int result = ToolRunner.run(new OaklandCrimeStats(), args);
        System.exit(result);
    }
}
