package experiment.kryo

import com.esotericsoftware.kryo.Kryo
import experiment.avro.TestRecord
import org.apache.spark.serializer.KryoRegistrator

/**
 * Created by Kostiantyn_Kudriavts on 4/4/2014.
 *
 *
 * The problem is that spark uses java serialization requiring serialized
 * objects to implement Serializable, AvroKey doesn't.
 * That's why @see <a href="http://spark.incubator.apache.org/docs/latest/tuning.html#data-serialization">Kryo</a> is used here
 */
class MyKryoRegistrator extends KryoRegistrator {
  override def registerClasses(kryo: Kryo) {
    kryo.register(classOf[TestRecord])
  }
}

