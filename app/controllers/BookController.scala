package controllers

import scala.concurrent.ExecutionContext.Implicits.global
import javax.inject._

import play.api.libs.json.{Format, JsString, Json}
import play.api.mvc._
import models.BooksStore._
import models.Library._
//import play.api.mvc.Results.{BadRequest, NoContent}
import services.ServiceImpl


@Singleton
class BookController @Inject()(val serviceImpl: ServiceImpl, val controllerComponents: ControllerComponents) extends BaseController {

  implicit val bookJson = Json.format[BookRare]
  implicit val bookJsonNew = Json.format[Book]
  implicit val userJson = Json.format[User]

  def getBookAll(): Action[AnyContent] = Action.async { implicit request =>
    serviceImpl.getBooks().map {
      seqBook => if (seqBook.nonEmpty) Ok(Json.toJson(seqBook)) else NotFound("Books Library is Empty")
    }.recover {
      case _ => BadRequest(Json.toJson("Unknown Error DB"))
    }
  }

  def getBookByISBN(isbn: Int): Action[AnyContent] = Action.async { implicit request =>
    serviceImpl.getBook(isbn).map {
      case Some(book) => Ok(Json.toJson(book))
      case None => NotFound("Book doesn't exist")
    }.recover {
      case _ => BadRequest(Json.toJson("Unknown Error DB"))
    }
  }

  def postBookContr: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    val book = request.body.asJson.get.as[Book]
    serviceImpl.postBook(book).map {
      quantityPosted => Ok(Json.obj("posted Books" -> quantityPosted))
    }.recover {
      case _ => BadRequest(Json.toJson("Unknown Error DB"))
    }
  }

  def deleteBook(ISBN: Int): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    serviceImpl.delBook(ISBN).map {
      case Right(isbn) => Ok(Json.obj("deleted" -> isbn))
      case Left(error) => error match {
        case serviceImpl.BookDoesNotExist(isbnPrint) => NotFound(s"Book $isbnPrint doesn't exist")
        case serviceImpl.BookInUsersHand(idPrint) => NotFound(s"Book In Users id=$idPrint Hand")
        }
      }.recover {
      case _ => BadRequest(Json.toJson("Unknown Error DB"))
    }
  }
//----For Users-------
def getUserAll(): Action[AnyContent] = Action.async { implicit request =>
  serviceImpl.getUsers().map {
    seqUser => if (!seqUser.isEmpty) Ok(Json.toJson(seqUser)) else NotFound("Users List is Empty")
  }.recover {
    case _ => BadRequest(Json.toJson("Unknown Error DB"))
  }
}

  def getUserById(id: Int): Action[AnyContent] = Action.async { implicit request =>
    serviceImpl.getUser(id).map {
      case Some(user) => Ok(Json.toJson(user))
      case None => NotFound("User doesn't exist")
    }.recover {
      case _ => BadRequest(Json.toJson("Unknown Error DB"))
    }
  }

  def postUserContr: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    val user = request.body.asJson.get.as[User]
    serviceImpl.postUser(user).map {
      quantityPosted => Ok(Json.obj("postedUsers" -> quantityPosted))
    }.recover {
      case _ => BadRequest(Json.toJson("Unknown Error DB"))
    }
  }

  def deleteUser(id: Int): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    serviceImpl.delUserPure(id).map {
      case Right(idTest) => Ok(Json.obj("deleted" -> idTest))
      case Left(error) => error match {
        case serviceImpl.UserDoesNotExist(idPrint) => NotFound(s"User $idPrint doesn't exist")
        case serviceImpl.UserHasBookStill(idPrint) => NotFound(s"User id=$idPrint Has Book Still")
      }
    }.recover {
      case _ => BadRequest(Json.toJson("Unknown Error DB"))
    }
  }

}
