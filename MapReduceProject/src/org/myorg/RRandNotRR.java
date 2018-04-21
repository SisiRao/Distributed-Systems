package org.myorg;
import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.*;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.*;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
/**
 * 95702 Distributed Systems
 *
 * Project 5
 * MapReduce couting total number of words contains "rr" and does not contain "rr"
 *
 * Andrew ID: sisir
 * @author Sisi Rao
 */
public class RRandNotRR extends Configured implements Tool{

    /**
     * Mapper class that emits <word, one> if find a match
     */
    public static class TotalWordsMap extends Mapper<LongWritable, Text, Text, IntWritable>
    {
            private final static IntWritable one = new IntWritable(1);
            private Text word = new Text();
            
            /**
             * map function
             * @param key the key word received
             * @param value the value received
             * @param context the intermediate result
             * */
            @Override
            public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException
            {
                String line = value.toString();
                StringTokenizer tokenizer = new StringTokenizer(line);
                while(tokenizer.hasMoreTokens())
                {
                    String newWord = tokenizer.nextToken(); 
                    if (newWord.contains("rr")) {  // words contain 'rr'
                        word.set("rr");
                        context.write(word, one);
                    } else { // words do not contain 'rr'
                        word.set("notrr");
                        context.write(word, one);
                    }
                }
            }
    }
    
    /**
     * Reducer Class takes in intermediate results <key, list<values>> and aggregate result for output
     */
    public static class TotalWordsReducer extends Reducer<Text, IntWritable, Text, IntWritable>    
    {
        /**
         * reducer function
         * @param key the key word received
         * @param values the iterable values received
         * @param context the result
         * */    
        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException
            {
                int sum = 0;
                for(IntWritable value: values)
                {
                        sum += value.get();
                }
                context.write(key, new IntWritable(sum));
            }
            
    }
    
    /**
     * create a mapreduce job
     * @param args class to run and system arguments
     * @return if success return 0 else return 1
     * */  
    public int run(String[] args) throws Exception  {
        
        Job job = new Job(getConf());
        job.setJarByClass(RRandNotRR.class);
        job.setJobName("rrandnotrr");
        
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        
        job.setMapperClass(TotalWordsMap.class);
        job.setReducerClass(TotalWordsReducer.class);
        
        
        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);
        
        
        FileInputFormat.setInputPaths(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        
        boolean success = job.waitForCompletion(true);
        return success ? 0: 1;
    }
    
    /**
     * main method
     * @param args system arguments
     * */  
    public static void main(String[] args) throws Exception {
        int result = ToolRunner.run(new RRandNotRR(), args);
        System.exit(result);
    }
}
