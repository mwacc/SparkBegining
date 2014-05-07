package experiment.samples

// important! this import is vital for method reduceByKey
import org.apache.spark.SparkContext._
import experiment.{JobSample, SparkConfFactory}
import org.apache.spark.util.Vector

/**
 * Created by Kostiantyn_Kudriavts on 5/7/2014.
 *
 * Algorithm from: http://en.wikibooks.org/wiki/Data_Mining_Algorithms_In_R/Clustering/K-Means
 */
class KMeans extends SparkConfFactory with JobSample {

  // convert a string of type: "0.00 0.00 0.00 ..." to a vector of doubles
  def lineToDoubles(line: String): Vector = {
    new Vector(line.split(' ').map(_.toDouble))
  }

  def average(points: Seq[Vector]) : Vector = {
    points.reduce(_+_) / points.length
  }

  // Return the index of the closest centroid to given point.
  // Calculated by finding minimum Euclidean Distance.
  def closestCentroid(point: Vector, centroids: Seq[Vector]): Int = {
    var index = 0
    var bestIndex = 0
    var closest = Double.PositiveInfinity

    for (i <- 0 until centroids.length) {
      val tempDist = point.squaredDist(centroids(i))
      if (tempDist < closest) {
        closest = tempDist
        bestIndex = i
      }
    }

    bestIndex
  }

  override def runSample(args: Array[String]): Unit = {
    /*
    val input = "hdfs://10.25.9.155:8020/user/hue/input.txt"
    val output= "hdfs://10.25.9.155:8020/user/hue/output-spark"
     */
    val input = args(0) // D:\GitHub\SparkBegining\src\main\resources\input.txt
    val K = args(1).toInt // K - number of clusters
    val maxIter = args(2).toInt

    val sc = getConf(args)

    val points = sc.textFile(input).map( lineToDoubles _ )  // convert text content to Vector-presentation
    points.cache() // cache to optimize iterative nature of algorithm

    // awesome! Spark provides sampling method
    var centroids = points.takeSample(false, K, scala.util.Random.nextInt)
    // iterations
    for (iter <- (1 until maxIter)) {
      println("Start iteration #"+iter)

      // for every point, find the closest centroid: <ClosestCentroidId, Point>
      val closest = points.map (point => (closestCentroid(point, centroids), point))
      // calculate new centroids + add difference to old centroids
      centroids = closest.groupByKey().map {case(i, points) =>
        average(points)
      }.collect()
    }

    println("Centroids: " )
    for( c : Vector <- centroids.take( centroids.length ) ) {
      println( "  " + c.toString() )
    }
  }

}
