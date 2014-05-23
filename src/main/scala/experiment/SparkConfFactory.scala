package experiment

import org.apache.spark.{SparkConf, SparkContext}

/**
 * Created by Kostiantyn_Kudriavts on 4/2/2014.
 */
trait SparkConfFactory extends java.io.Serializable {

  def getConf(args: Array[String]): SparkContext = {
    if( args.length > 3 && "local".equals( args(args.length-2) ) )
      getLocalSetup(args(args.length-2), args(args.length-1))
    else
      getClusterSetup()
  }

  def getClusterSetup() : SparkContext = {
    println("cluster setup")

    val conf = new SparkConf()
      .setMaster("local")
      .setAppName("My Spark application")
      .set("spark.executor.memory", "4g")
    //   .set("spark.cores.max", numCores) // maximum number of cores available for cluster (for standalone or Mesos instalation)
    val sc = new SparkContext(conf)
    return sc
  }

  // D:\GitHub\SparkBegining\src\main\resources\input.txt d:\GitHub\spark-0.9.0-incubating-bin-hadoop2\ target/SparkBegining-1.0-SNAPSHOT.jar
  def getLocalSetup(sparkInstalation: String, jar: String) : SparkContext = {
    println("local setup")

    val sc = new SparkContext("local", "Example App", sparkInstalation,List(jar))
    // sc.addFile()  - add jar
    return sc
  }


}
