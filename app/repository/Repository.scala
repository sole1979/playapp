package repository

import com.google.inject.ImplementedBy
import models.Library._

import scala.concurrent.Future

@ImplementedBy(classOf[PostgresImpl])
trait Repository {
  def readAllBook(): Future[Seq[Book]]
  def readBook(isbn: Int): Future[Option[Book]]
  def updateBook(book: Book): Future[Int]
  def createBook(book: Book): Future[Int]
  def deleteBook(book: Book): Future[Int]
  //---------
  def readAllUsers(): Future[Seq[User]]
  def readUser(id: Int): Future[Option[User]]
  def updateUser(user: User): Future[Int]
  def createUser(user: User): Future[Int]
  def deleteUser(user: User): Future[Int]
  //----------
  def booksByUser(id: Int): Future[Seq[Book]]
  def booksAndUsers(): Future[Seq[(Int, Int)]]
  def bookWhere(isbn: Int): Future[Seq[(Int, String)]]
}
