package models

import scala.collection.mutable

object BooksStore {
  case class Book(ISBN: Int, title:  String, descr: String)


  case class BookeInMapForJson(ISBN: Int, description: String)
  case class BookeInMap(description: String) {
    def toCaseClass(isbn: Int) = new BookeInMapForJson(isbn, description)
  }

  val books = mutable.Map.empty[Int, Book]

  books(0) = Book(0, "zero", "empty")
  books.put(1, Book(1, "one", "First"))
  books += 2 -> Book(2, "two", "Second")


}
