package com.symbolscope.gic.gol

import spray.json.DefaultJsonProtocol._
import spray.json._

/**
 * format conversion for json
 */
object Json {

  implicit object JsonFormat extends JsonFormat[Any] {
    def write(x: Any) = x match {
      case n: Int => JsNumber(n)
      case s: String => JsString(s)
      case b: Boolean if b => JsTrue
      case b: Boolean => JsFalse
    }

    def read(value: JsValue) = value match {
      case JsNumber(n) => n.intValue()
      case JsString(s) => s
      case JsTrue => true
      case JsFalse => false
    }
  }

  def mapToJson(x: Map[String, Any]): String = {
    x.toJson.compactPrint
  }
}
