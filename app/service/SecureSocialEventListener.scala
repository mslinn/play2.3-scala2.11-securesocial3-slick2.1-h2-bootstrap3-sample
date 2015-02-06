package service

import models.BasicUser
import securesocial.core._
import play.api.mvc.{ Session, RequestHeader }
import play.api.Logger

class SecureSocialEventListener extends EventListener[BasicUser] {
  def onEvent(event: Event[BasicUser], request: RequestHeader, session: Session): Option[Session] = {
    val eventName = event match {
      case LoginEvent(u) => "login"
      case LogoutEvent(u) => "logout"
      case SignUpEvent(u) => "signup"
      case PasswordResetEvent(u) => "password reset"
      case PasswordChangeEvent(u) => "password change"
    }

    Logger.info(s"traced $eventName event for user ${event.user}")

    None
  }
}
