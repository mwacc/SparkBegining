package experiment.samples

import org.apache.spark.SparkConf
import experiment.{JobSample, SparkConfFactory}


/**
 * Created by Kostiantyn_Kudriavts on 3/21/2014.
 */
class SimpleApp extends SparkConfFactory with JobSample {

  override def runSample(args: Array[String]): Unit = {
    val logFile = args(0)

    val sc = getConf(args)

    // hdfs:///user/hue/input.txt
    val logData = sc.textFile(logFile, 2).cache()
    val numAs = logData.filter(line => line.contains("a")).count()
    val numBs = logData.filter(line => line.contains("addop")).count()
    println("Lines with 'a': %s, Lines with 'addop': %s".format(numAs, numBs))
  }
}
