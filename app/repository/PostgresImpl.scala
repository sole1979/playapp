package repository

import slick.jdbc.PostgresProfile.api._
import models.Library._
import com.google.inject.ImplementedBy


class PostgresImpl extends Repository {
  val db = Database.forConfig("mydb")

  class UserTable(tag: Tag) extends Table[User](tag, "users") {
    def name = column[String]("name")

    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

    override def * = (name, id) <> (User.tupled, User.unapply)
  }

  lazy val usersTable = TableQuery[UserTable]

  class BookTable(tag: Tag) extends Table[Book](tag, "books") {
    def isbn = column[Int]("isbn", O.PrimaryKey)

    def title = column[String]("title")

    def users_id = column[Int]("users_id")

    //override def * = (ISBN, title).mapTo[Book]--
    override def * = (isbn, title, users_id) <> (Book.tupled, Book.unapply)

    def user = foreignKey("users_fk", users_id, usersTable)(_.id)
  }

  lazy val booksTable = TableQuery[BookTable]

  //------Book--CRUD-----

  override def readAllBook() = {
    val query = for (res <- booksTable) yield res
    db.run(query.result)
  }

  override def readBook(isbn: Int) = {
    val query = for (res <- booksTable if res.isbn === isbn) yield res
    db.run(query.result.headOption)
  }

  override def createBook(book: Book) = {
    db.run(booksTable += book)
  }

  override def updateBook(book: Book) = {
    db.run(booksTable.filter(_.isbn === book.isbn).update(book))
  }

  override def deleteBook(book: Book) = {
    val query = booksTable.filter(_.isbn === book.isbn).delete
    db.run(query)
  }

  //----------User---CRUD-----------
  override def readAllUsers() = {
    val query = for (res <- usersTable) yield res
    db.run(query.result)
  }

  override def readUser(id: Int) = {
    val query = for (res <- usersTable if res.id === id) yield res
    db.run(query.result.headOption)
  }

  override def createUser(user: User) = {
    db.run(usersTable += user)
  }

  override def updateUser(user: User) = {
    db.run(usersTable.filter(_.id === user.id).update(user))
  }

  override def deleteUser(user: User) = {
    db.run(usersTable.filter(_.id === user.id).delete)
  }

  //-----Query JOIN -------
  override def booksByUser(id: Int) = {
    val usersAndBooks =
      usersTable.
        join(booksTable).on { case (user, book) => user.id === book.users_id }
    // map { case(user, book) => (user.id, book.isbn)}

    val userById =
      usersAndBooks.
        filter { case (user, _) => user.id === id }.
        map { case (_, book) => (book) } //(user.id, book.isbn)}

    db.run(userById.result)
  }

  override def booksAndUsers() = {
    val usersAndBooks =
      usersTable.
        join(booksTable).on { case (user, book) => user.id === book.users_id }.
        map { case (user, book) => (user.id, book.isbn) }

    db.run(usersAndBooks.result)
  }

  override def bookWhere(isbn: Int) = {
    //существует ли этот isbn проверка
    val usersAndBooks =
      usersTable.
        join(booksTable).on { case (user, book) => user.id === book.users_id }

    val bookOnHand = {
      usersAndBooks.
        filter { case (_, book) => book.isbn === isbn }.
        map { case (user, _) => (user.id, user.name) }
    }

    db.run(bookOnHand.result)
  }
}
