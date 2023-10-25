package controllers

import akka.http.scaladsl.model.StatusCodes.NoContent

import javax.inject._
import play.api._
import play.api.libs.json.{Format, Json}
import play.api.mvc._
import models.BooksStore._
import play.api.mvc.Results.{BadRequest, NoContent}

@Singleton
class BookController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  implicit val bookJson = Json.format[Book]

  def getAll() = Action { implicit request: Request[AnyContent] =>
    if (books.isEmpty) NoContent else Ok(Json.toJson(books))
  }

  def getByISBN(isbn: Int) = Action { implicit request: Request[AnyContent] =>
    books.get(isbn) match {
      case Some(book) => Ok(Json.toJson(book))
      case None => NotFound
    }

  }

  def create = Action { implicit request: Request[AnyContent] =>
    val json = request.body.asJson.get
    val book = json.as[Book]
    if (books.contains(book.ISBN)) BadRequest
    else {
      books.put(book.ISBN, book)
      Redirect(routes.BookController.getAll())
    }
  }

  def edit = Action { implicit request: Request[AnyContent] =>
    val json = request.body.asJson.get
    val book = json.as[Book]
    if (books.contains(book.ISBN)) {
      books.put(book.ISBN, book)  //записую нове значення book для ключа, який в наявностi
      Redirect(routes.BookController.getAll())
    }
    else {
      BadRequest                  //для вiдсутнього значення
    }
  }

  def delete(ISBN: Int) = Action { implicit request: Request[AnyContent] =>
    if (books.contains(ISBN)) {
      books.remove(ISBN)
      Redirect(routes.BookController.getAll())
    } else BadRequest

  }
}
