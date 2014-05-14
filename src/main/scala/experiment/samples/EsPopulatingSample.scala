package experiment.samples

// important! this import is vital for method reduceByKey
import org.apache.spark.SparkContext._
import experiment.{JobSample, SparkConfFactory}
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.io._
import org.apache.spark.rdd.RDD
import org.elasticsearch.hadoop.mr.EsOutputFormat

/**
 * Created by Kostiantyn_Kudriavts on 5/6/2014.
 */
// TODO: need to test
class EsPopulatingSample extends SparkConfFactory with JobSample {

  override def runSample(args: Array[String]): Unit = {

      val conf = new Configuration()
      conf.set("es.resource", "words/wordscount")

      val input = args(0) // D:\GitHub\SparkBegining\src\main\resources\input.txt

      val sc = getConf(args)

      val file = sc.textFile(input)

      val filteredWords = file
        .flatMap(line => line.split("\\s"))
        .filter(word => word.trim.length > 0)

      var totalCounter: Long = filteredWords.count()

      val words =
        filteredWords.map{ word =>
            (word, 1)
        }
        .reduceByKey(_ + _)

      val mapWritableRDD : RDD[(NullWritable, MapWritable)] = words.map {
          wordPair =>
              val mw = new MapWritable()
              mw.put(new Text("word"), new Text(wordPair._1))
              mw.put(new Text("length"), new LongWritable(wordPair._1.getLength))
              mw.put(new Text("count"), new LongWritable(wordPair._2))
              mw.put(new Text("frequency"), new DoubleWritable(wordPair._2/totalCounter))
              (NullWritable.get(), mw)
      }

      mapWritableRDD.saveAsNewAPIHadoopFile(
        // this path is ignored for ElasticSearch output format, but required by method signature
        "/some/random/directory",
        classOf[NullWritable],
        classOf[MapWritable],
        classOf[EsOutputFormat],
        conf
      )
  }
}
