package service

import models.Tables._
import models.User
import org.joda.time.DateTime
import play.api.Logger
import securesocial.core._
import securesocial.core.providers.MailToken
import securesocial.core.services.UserService
import scala.concurrent.Future

class DBUserService extends UserService[User] {
  val logger = Logger("service.DBUserService")

  def find(providerId: String, userId: String): Future[Option[BasicProfile]] = {
//    if (logger.isDebugEnabled) logger.debug("users = %s".format(users))
    Users.findByIdentityId(userId, providerId)
  }

  def findByEmailAndProvider(email: String, providerId: String) =
    Users.findByEmailAndProvider(email, providerId)

  def save(token: MailToken) = MailTokens.save(token)

  def findToken(tokenId: String) = MailTokens.findById(tokenId)

  def deleteToken(uuid: String) = MailTokens.delete(uuid)

  def deleteExpiredTokens() = MailTokens.deleteExpiredTokens(DateTime.now())

  def link(current: Identity, to: Identity) = ???
}
