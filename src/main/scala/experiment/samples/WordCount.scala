package experiment.samples

// important! this import is vital for method reduceByKey
import org.apache.spark.SparkContext._
import org.apache.hadoop.io.{Text, IntWritable}
import experiment.{JobSample, SparkConfFactory}

/**
 * Created by Kostiantyn_Kudriavts on 4/2/2014.
 *
 * It requires compression codec to be available on java.library.path. So, it must be run with command:
 *   java -Djava.library.path=/usr/lib/hadoop/lib/native -jar ...
 * Programmatic changing doesn't have effect, because the property is read very early and cached.
 * However, it might be change via reflection if required, but I found passing it during start up more fair
 */
class WordCount extends SparkConfFactory with JobSample {


  override def runSample(args: Array[String]): Unit = {
    /*
    val input = "hdfs://10.25.9.155:8020/user/hue/input.txt"
    val output= "hdfs://10.25.9.155:8020/user/hue/output-spark"
     */
    val input = args(0) // D:\GitHub\SparkBegining\src\main\resources\input.txt
    val output = args(1)

    val sc = getConf(args)

    val file = sc.textFile(input)
    val counts = file
      .flatMap(line => line.split("\\s"))
      .filter(word => word.trim.length > 0)
      .map( word => (word, 1) )
      .reduceByKey(_ + _)
      .map(item => item.swap)  // swap tuples
      .sortByKey(false);  // desc


    // save as sequence file
    //counts.saveAsSequenceFile(output)

    // save as compressed sequence file (w/ snappy)
    counts.saveAsSequenceFile(output, Some(classOf[org.apache.hadoop.io.compress.SnappyCodec]));

    sc.sequenceFile(output, classOf[IntWritable], classOf[Text])
      .filter(t => t._1.get() > 50)
      .saveAsTextFile("hdfs://10.25.9.155:8020/user/hue/output-spark2");
  }
}
