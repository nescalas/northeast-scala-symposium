package nescala

import mojolly.inflector.Inflector

/** Misc. utilities
  */
object Util {

  /** Given the singular form of a noun and a count, return the appropriate
    * phrase. For instance:
    *
    * {{{
    * singularPlural(1, "box")   // -> "1 box"
    * singularPlural(2, "thing") // -> "2 things"
    * singularPlural(0, "box")   // -> "no boxes"
    * }}}
    *
    * @param count      the count
    * @param noun       the singular noun
    * @param initialCap whether the first word (if non-numeric) should be
    *                   capitalized
    *
    * @return the appropriate phrase
    */
  def singularPlural(count:      Int,
                     noun:       String,
                     initialCap: Boolean = true): String = {
    count match {
      case 0 => {
        val zero = if (initialCap) "No" else "no"
        s"$zero ${Inflector.pluralize(noun)}"
      }
      case 1 => s"1 $noun"
      case _ => s"$count ${Inflector.pluralize(noun)}"
    }
  }
}
