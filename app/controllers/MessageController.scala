package controllers

import models.User
import play.api.Routes
import play.api.libs.json.Json
import play.api.mvc.Action
import securesocial.core._

case class Message(value: String)

class MessageController(override implicit val env: RuntimeEnvironment[User]) extends SecureSocial[User] {
  implicit val fooWrites = Json.writes[Message]

  def ???!(msg: String): Nothing = throw new RuntimeException(msg)

  def index() = UserAwareAction { implicit request =>
      val user = request.user
      Ok(views.html.index("hi user " + user, ""))
  }

  def getMessage = Action {
    Ok(Json.toJson(Message("Hello from Scala")))
  }

  def javascriptRoutes = Action { implicit request =>
    import securesocial.controllers.routes.javascript.LoginPage
    Ok(Routes.javascriptRouter("jsRoutes")(LoginPage.login)).as(JAVASCRIPT)
  }

  def search = index()
  //  UserAwareAction {
  //    implicit request =>
  //      val query = request.getQueryString("query")
  //
  //  }

  def search2(query: String) = index()
}
