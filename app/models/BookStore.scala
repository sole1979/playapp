package models

import scala.collection.mutable

class BookStore {

  case class Bookss(ISBN: Long, description: String)

  val bookss = new mutable.ListBuffer[Bookss].empty

  bookss += Bookss(1, "one")
  bookss += Bookss(2, "two")

}
