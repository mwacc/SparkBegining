package experiment

/**
 * Created by Kostiantyn_Kudriavts on 4/4/2014.
 */
object RunJob {

  def main(args: Array[String]) {
    val command = args(0)
    val jobSample = Class.forName(command).newInstance.asInstanceOf[{ def runSample(args: Array[String]): Unit }]
    jobSample.runSample(args.slice(1, args.length))
  }

}
