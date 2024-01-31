package models

object Library {
  case class Book(isbn: Int, title: String, users_id: Int)
  case class User(name: String, id: Int = 0 )

}
