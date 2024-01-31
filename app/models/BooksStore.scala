package models

import scala.collection.mutable

object BooksStore {
  case class BookRare(ISBN: Int, title:  String, descr: String)

  val books = mutable.Map.empty[Int, BookRare]

  books(0) = BookRare(0, "zero", "empty")
  books.put(1, BookRare(1, "one", "First"))
  books += 2 -> BookRare(2, "two", "Second")


}
