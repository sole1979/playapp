package services


import models.Library._

import repository.Repository

import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ServiceImpl @Inject()(repo: Repository){

  //-----BOOKS-------
  sealed trait ServiceError extends Product with Serializable

  case class UserDoesNotExist(val id: Int) extends ServiceError
  case class UserHasBookStill(val id: Int) extends ServiceError
  case class BookDoesNotExist(val isbn: Int) extends ServiceError
  case class BookInUsersHand(val id: Int) extends ServiceError

  sealed trait Ok extends Product with Serializable
  case class DeletingUser(val id: Int) extends Ok
  //------

  def getBooks(): Future[Seq[Book]] = repo.readAllBook()

  def getBook(isbn: Int): Future[Option[Book]] = repo.readBook(isbn)

  def postBook(book: Book): Future[Int] = {
    getBook(book.isbn)
      .flatMap {
        case Some(_) => repo.updateBook(book)
        case None => repo.createBook(book)
      }
  }

   def delBook(isbn: Int): Future[Either[ServiceError, Int]] = {
    getBook(isbn).map {
        case None => Left(BookDoesNotExist(isbn))
        case Some(bookForDel) => if (bookForDel.users_id == 1) {
          repo.deleteBook(bookForDel)
          Right(isbn)
        } else Left(BookInUsersHand(bookForDel.users_id))
    }
  }

  //------USERS---------
  def getUsers(): Future[Seq[User]] = repo.readAllUsers()

  def getUser(id: Int): Future[Option[User]] = repo.readUser(id)

  def postUser(user: User): Future[Int] = {
    getUser(user.id)
      .flatMap {
        case Some(_) => repo.updateUser(user)
        case None => repo.createUser(user)
      }
  }

//  def delThrowable(user: User) = {
//    //i don't Exam for Delete user=(1, Library)
//    case class UserHasBookStill() extends Throwable()
//    case class UserDoesNotExist() extends Throwable()
//
//    for {
//      f1 <- repo.readUser(user.id)
//      f2 <- f1 match { //Exam User Does  Not Exist
//        case None => Future.failed(UserDoesNotExist())
//        case Some(user) => repo.booksByUser(user.id)
//      }
//      f4 <- if (f2.isEmpty) repo.deleteUser(user) else Future.failed(UserHasBookStill()) //Exam User dosn't have book
//    } yield f4
//  }

//  def delGuru(user: User) = {
//    for {
//      maybeUser <- repo.readUser(user.id)
//      books <- repo.booksByUser(user.id)
//
//      result = maybeUser match {
//        case None => Left(UserDoesNotExist(user.id))
//        case Some(_) =>
//          if (books.isEmpty) {
//            repo.deleteUser(user)
//            Right(DeletingUser(user.id))
//          }
//          else Left(UserHasBookStill(user.id))
//      }
//    } yield result
//  }

  def delUserPure(id: Int): Future[Either[ServiceError, Int]] = {
    repo.readUser(id).flatMap { maybeUser =>
      repo.booksByUser(id)
        .map { books =>
          (maybeUser, books) match {
            case (None, _) => Left(UserDoesNotExist(id))
            case (Some(user), books) =>
              if (books.isEmpty) {
                repo.deleteUser(user)
                Right(user.id)
              } else Left(UserHasBookStill(user.id))
          }
        }
    }
  }


  //-------COMMON FUNC-----
  def usersBooks(id: Int): Future[Seq[Book]] = repo.booksByUser(id)

  def allUsersBooks(): Future[Seq[(Int, Int)]] = repo.booksAndUsers()

  def whereBook(isbn: Int): Future[Seq[(Int, String)]] = repo.bookWhere(isbn)
}
