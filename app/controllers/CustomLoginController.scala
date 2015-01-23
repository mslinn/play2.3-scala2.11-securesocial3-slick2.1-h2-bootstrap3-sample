package controllers

import securesocial.controllers.BaseLoginPage
import play.api.mvc.{ RequestHeader, AnyContent, Action }
import play.api.Logger
import securesocial.core.{ RuntimeEnvironment, IdentityProvider }
import models.User
import securesocial.core.services.RoutesService

class CustomLoginController(implicit override val env: RuntimeEnvironment[User]) extends BaseLoginPage[User] {
  override def login: Action[AnyContent] = {
    Logger.debug("Using CustomLoginController")
    super.login
  }
}

class CustomRoutesService extends RoutesService.Default {
  override def loginPageUrl(implicit req: RequestHeader): String =
    routes.CustomLoginController.login().absoluteURL(IdentityProvider.sslEnabled)
}
