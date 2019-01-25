package com.symbolscope.gic.gol

import spray.json.DefaultJsonProtocol._
import spray.json._

/**
 * format conversion for json
 */
object Json {

  implicit object Format extends JsonFormat[Any] {
    def write(x: Any): JsValue = x match {
      case n: Int => JsNumber(n)
      case s: String => JsString(s)
      case b: Boolean if b => JsTrue
      case b: Boolean => JsFalse
    }

    def read(value: JsValue): Any = value match {
      case JsNumber(n) => n.intValue()
      case JsString(s) => s
      case JsTrue => true
      case JsFalse => false
      case _ => ""
    }
  }

  def mapToJson(x: Map[String, Any]): String = {
    x.toJson.compactPrint
  }

  def jsonToMap(json: String): Map[String, Any] = {
    val ast = json.parseJson
    ast.convertTo[Map[String, Any]]
  }
}
