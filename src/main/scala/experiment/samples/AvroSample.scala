package experiment.samples

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.hadoop.mapreduce.Job
import org.apache.avro.specific.SpecificDatumWriter
import org.apache.avro.file.DataFileWriter
import java.io.File
import experiment.JobSample
import experiment.avro._
import org.apache.hadoop.fs.{Path, FileSystem}
import java.net.URI
import org.apache.hadoop.mapred.JobConf

/**
 * Created by Kostiantyn_Kudriavts on 4/4/2014.
 */
class AvroSample extends JobSample {


  override def runSample(args: Array[String]): Unit = {
    def output = args(0)

    val conf = new SparkConf().setMaster("local").setAppName("DemoSpark")
    conf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    conf.set("spark.kryo.registrator", "experiment.kryo.MyKryoRegistrator")
    def sc = new SparkContext(conf)
    val job = new Job()

    val record1 = new TestRecord()
    record1.setField1("value1")
    record1.setField2(2014);
    val record2 = new TestRecord()
    record2.setField1("value2");
    record2.setField2(2012);
    record2.setField3(77777);


    def userDatumWriter = new SpecificDatumWriter[TestRecord]()
    val dataFileWriter = new DataFileWriter[TestRecord](userDatumWriter)


    val fs = FileSystem.get(new URI(args(1)), new JobConf()); //  hdfs://10.25.9.155:8020  (namenode conenction)
    val filenamePath = new Path(output); // TODO: it makes sense to check if this file exist before writing

    dataFileWriter.create(record1.getSchema(), fs.create(filenamePath))
    dataFileWriter.append(record1)
    dataFileWriter.append(record2)
    dataFileWriter.close()

    // after that let's read this file
    val contentOfTheFile = sc.newAPIHadoopFile(
      output,
      classOf[org.apache.avro.mapreduce.AvroKeyInputFormat[TestRecord]],
      classOf[org.apache.avro.mapred.AvroKey[TestRecord]],
      classOf[org.apache.hadoop.io.NullWritable],
      job.getConfiguration)

    // print to console
    println( "Print only records after 2013 from " + contentOfTheFile.count() + " available records")
    contentOfTheFile.filter( tuple => tuple._1.datum().getField2 > 2013 ).map( rec => println(rec._1.datum().getField1) );
  }
}
