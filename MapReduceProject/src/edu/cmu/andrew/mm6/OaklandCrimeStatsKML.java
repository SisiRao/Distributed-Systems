package edu.cmu.andrew.mm6;

import java.io.IOException;
import java.util.ArrayList;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
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

public class OaklandCrimeStatsKML extends Configured implements Tool{

    /**
     * Mapper class emits <key, value> 
     * if find a match, will emit <"Total number of assault", one>
     * */
    public static class OaklandCrimeStatsMap extends Mapper<LongWritable, Text, Text, Text> {
        private final static Text coordinates = new Text();
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
            // The eight column specifies the latitude.
            // The ninth column specifies the longitude.
            float x = Float.parseFloat(data[0]);
            float y = Float.parseFloat(data[1]);
            // the coordinates used in kml file
            String lat = data[data.length-2];
            String lon = data[data.length-1];

            // check if the crime occurred within
            // 200 meters of 3803 Forbes Avenue in Oakland. 
            // This location has the (X,Y) coordinates of (1354326.897,411447.7828).
            // 1 feet = 0.3048 meter
            // Use the coordinates in meters and the Pythagorean theorem to check.
            boolean inRange = Math.pow(((x-1354326.897)* 0.3048), 2)  + Math.pow(((y-411447.7828) * 0.3048), 2) <= Math.pow(200, 2);

            // check crime type and write to intermediate result 
            if (line.contains("ASSAULT") && inRange) {
                word.set("Assault");
                String result = lat + "," + lon;
                coordinates.set(result);
                context.write(word, coordinates);
            }
        }
    }
    
    
    public static class OaklandCrimeStatsReducer extends Reducer<Text, Text, Text,Text>    
    {
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException
        {
                ArrayList<String> positions = new ArrayList<>();
                // collect all crime coordinates
                for(Text value: values)
                {
                    positions.add(value.toString());
                }
                
                String content = buildKML(key.toString(),positions);
                
             // emit (value = kml file content)
            context.write(null, new Text(content));
        }

        private String buildKML(String key, ArrayList<String> positions) {

            StringBuilder sb = new StringBuilder();
            int count = 1;
            // write to kml file
            for(String entry : positions) {
                String[] position = entry.split(",");
                sb.append("<Placemark>\n" +
                          " <name>"+ count +"</name>\n" + 
                          " <description>"+ key + "</description>\n"+ 
                          " <styleUrl>#style1</styleUrl>\n" +
                          " <Point>\n" + 
                          " <coordinates>"+ position[1] + ","+ position[0]+",0.00</coordinates>\n" +
                          " </Point>\n" + 
                          "</Placemark>\n");  
                count++;
            }
            
            String content = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" + 
                    "<kml xmlns=\"http://earth.google.com/kml/2.2\">\n" + 
                    "<Document>\n" + 
                    " <Style id=\"style1\">\n" + 
                    " <IconStyle>\n" + 
                    " <Icon>\n" + 
                    " <href>https://lh3.googleusercontent.com/MSOuW3ZjC7uflJAMstcykSOEOwI_cVz96s2rtWTN4-Vu1NOBw80pTqrTe06R_AMfxS2=w170</href>\n" + 
                    " </Icon>\n" + 
                    " </IconStyle>\n" + 
                    " </Style>\n" + 
                    sb.toString() +
                    "</Document>\n" + 
                    "</kml>";
            
            return content;
        }
            
    }
    
    public int run(String[] args) throws Exception  {
        
        Job job = new Job(getConf());
        job.setJarByClass(OaklandCrimeStatsKML.class);
        job.setJobName("Oakland Crime Stats KML");
        
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        
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
        int result = ToolRunner.run(new OaklandCrimeStatsKML(), args);
        System.exit(result);
    }
}
