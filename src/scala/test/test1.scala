package test

import com.alibaba.fastjson.{JSON, JSONObject}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Row, SQLContext}
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
object test1 {

  def main(args: Array[String]): Unit = {

    def main(args: Array[String]): Unit = {
      val conf = new SparkConf().setAppName(this.getClass.getName).setMaster("local[*]")
      val sc = new SparkContext(conf)
      val sqlContext = new SQLContext(sc)
      val log: RDD[String] = sc.textFile("D:\\json.txt")
      val logs: mutable.Buffer[String] = log.collect().toBuffer

      var list: List[List[String]] = List()
      for (i <- 0 until logs.length) {
        val jsonstr: String = logs(i).toString


        val jsonparse: JSONObject = JSON.parseObject(jsonstr)

        val status = jsonparse.getIntValue("status")
        if (status == 0) return ""

        val regeocodeJson = jsonparse.getJSONObject("regeocode")
        if (regeocodeJson == null || regeocodeJson.keySet().isEmpty) return ""

        val poisArray = regeocodeJson.getJSONArray("pois")
        if (poisArray == null || poisArray.isEmpty) return null


        val buffer = collection.mutable.ListBuffer[String]()

        for(item <- poisArray.toArray){
          if(item.isInstanceOf[JSONObject]){
            val json = item.asInstanceOf[JSONObject]
            buffer.append(json.getString("businessarea"))
          }
        }
        val list1: List[String] = buffer.toList
        list:+=list1
      }

      val res1 = list.flatMap(x => x).map(x => (x, 1))
        .groupBy(x => x._1)
        .mapValues(x => x.size)
      res1.foreach(println)

    }
  }

}
