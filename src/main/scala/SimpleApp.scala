import org.apache.spark.sql.SparkSession

object SimpleApp {
  def main(args: Array[String]) {
    // Spark config
    val spark = SparkSession.builder()
      .appName("Simple Application")
      .master("local")
      .getOrCreate()

    // end spark session
    spark.stop()
  }
}