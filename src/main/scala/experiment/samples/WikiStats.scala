package experiment.samples

import org.apache.spark.SparkContext._
import experiment.{JobSample, SparkConfFactory}

/**
 * Created by Kostiantyn_Kudriavts on 5/23/2014.
 *
 * WikiStats dump from http://dumps.wikimedia.org/other/pagecounts-raw/
 * each line has 4 fields:  projectcode, pagename, pageviews, and bytes (size)
 * Also, the sataset https://aws.amazon.com/datasets/6025882142118545 might be used
 *
 * Inspired by https://aws.amazon.com/articles/Elastic-MapReduce/4926593393724923
 *
 * Run with:
 *  java -jar SparkBegining-1.0-SNAPSHOT-shaded.jar experiment.samples.WikiStats hdfs://10.0.2.15:8020/user/hue/sparkin/pagecounts-20140401-130000.gz 9 cluster cluster
 */
class WikiStats extends SparkConfFactory with JobSample {

  override def runSample(args: Array[String]): Unit = {
    val input = args(0)
    val nodesNum = args(1).toInt // num of partitions on reduce stage, recommended to have 3-4 per CPU
    val sc = getConf(args)  // inherited method from my class to get Hadoop configuration
    val startTime: Long = System.currentTimeMillis();

    val file = sc.textFile(input)

    val originalRecNum =  file.count();
    val enRecords = file.
      map(l => l.split("\\s")).
      filter(l => l.length == 4 && l(0).toLowerCase().startsWith("en"));

    val enRecNum = enRecords.count();
    val reducedList = enRecords.
      map(l => (l(1), l(2).toInt)).
      reduceByKey(_+_, nodesNum)

    val sortedList = reducedList.map(x => (x._2, x._1)).sortByKey(false).take(50)

    println("Computations done in, ms: " + ( System.currentTimeMillis() - startTime ))
    for( tuple <- sortedList ) {
      println( tuple._1 + " was accessed " + tuple._2 + "  times" )
    }
    println( "\nAll records: %d; en records: %d".format(originalRecNum, enRecNum) )
    println( String.format("En recodrs: %.2f %%\n",
      new java.lang.Double( (enRecNum.toDouble/originalRecNum.toDouble)*100) ) )
  }

}
