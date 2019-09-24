package test
import com.alibaba.fastjson.{JSON, JSONObject}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SQLContext

import scala.collection.mutable
object tag {
  def main(args: Array[String]): Unit = {

    var list: List[String] = List()

    val conf = new SparkConf().setAppName(this.getClass.getName)
      .setMaster("local[*]")
    val sc = new SparkContext(conf)
    val sQLContext = new SQLContext(sc)

    val log: RDD[String] = sc.textFile("D:\\json.txt")

    val logs: mutable.Buffer[String] = log.collect().toBuffer

    for (i <- 0 until logs.length) {
      val str: String = logs(i).toString

      val jsonparse: JSONObject = JSON.parseObject(str)

      val status = jsonparse.getIntValue("status")
      if (status == 0) return ""

      val regeocodeJson = jsonparse.getJSONObject("regeocode")
      if (regeocodeJson == null || regeocodeJson.keySet().isEmpty) return ""

      val poisArray = regeocodeJson.getJSONArray("pois")
      if (poisArray == null || poisArray.isEmpty) return null

      val buffer = collection.mutable.ListBuffer[String]()

      for (item <- poisArray.toArray) {
        if (item.isInstanceOf[JSONObject]) {
          val json = item.asInstanceOf[JSONObject]
          buffer.append(json.getString("type"))
        }
      }

      list :+= buffer.mkString(";")
    }

    val res2 = list.flatMap(x => x.split(";")).map(x => (x, 1))
      .groupBy(x => x._1)
      .mapValues(x => x.length)

    res2.foreach(x => println(x))
  }
}
