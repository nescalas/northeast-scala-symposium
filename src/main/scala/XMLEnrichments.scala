package nescala

import scala.xml.{Text, Null, Attribute, Elem}

/** Enrichments for XML stuff.
  */
object XMLEnrichments {

  /** Enriched scala.xml.Elem
    *
    * @param e the wrapped element
    */
  implicit class RichElem(val e: Elem) {
    /** Simplified method for adding an attribute.
      *
      * @param name  attribute name
      * @param value attribute value
      *
      * @return the new element
      */
    def attr(name: String, value: String): Elem = {
      e % Attribute(None, name, Text(value), Null)
    }
  }

}
