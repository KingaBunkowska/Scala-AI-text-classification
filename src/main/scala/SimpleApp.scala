import org.apache.spark.sql.SparkSession

object SimpleApplication {
  def main(args: Array[String]): Unit = {
    // Spark config
    val spark = SparkSession.builder()
      .appName("Simple Application")
      .master("local")
      .getOrCreate()

    val dataset = new Dataset(spark)
    dataset.load("dataset/AI_Human_text.csv")

    val (train, test) = dataset.split(0.3, 42)

    val model = new Model(spark)

    model.train(train)
    print(model.evaluate(test))
    Screen.setModel(model,model.evaluate(test))
    Screen.main(args)
    spark.stop()
  }
}