Understanding Spark: collection of samples
==========================================

Available samples
-----------------
1. SimpleApp - my first Spark app;
2. WordCount - word count example w/ saving result to sequence file and compression, to run it correctly it must be called as java -Djava.library.path=/usr/lib/hadoop/lib/native
3. AvroSample - exmaple of writing/reading Avro files in Spark

Architecture overview
----------------------
RunJob is a main class who run any sample (calling required one via reflection):
java -Djava.library.path=/usr/lib/hadoop/lib/native -jar SparkBegining-1.0-SNAPSHOT-shaded.jar experiment.samples.AvroSample hdfs://10.25.9.155:8020/user/hue/output-spark hdfs://10.25.9.155:8020
