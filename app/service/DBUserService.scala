package service

import models.User
import models.Tables._
import org.joda.time.DateTime
import play.api.Logger
import securesocial.core._
import securesocial.core.providers.{ UsernamePasswordProvider, MailToken }
import scala.concurrent.Future
import securesocial.core.services.{ UserService, SaveMode }

class DBUserService extends UserService[User] {
  val logger = Logger("service.DBUserService")

  def find(providerId: String, userId: String): Future[Option[BasicProfile]] = {
//    if (logger.isDebugEnabled) logger.debug("users = %s".format(users))
    Users.findByIdentityId(userId)
  }

  def save(user: Identity) = Users.save(user)

  // Since we're not using username/password login, we don't need the methods below
  def findByEmailAndProvider(email: String, providerId: String) = {
    Users.findByEmailAndProvider(email, providerId)
  }

  def save(token: Token) {
    Tokens.save(token)
  }

  def findToken(tokenId: String) = {
    Tokens.findById(tokenId)
  }

  def deleteToken(uuid: String) {
    Tokens.delete(uuid)
  }

  def deleteExpiredTokens() {
    Tokens.deleteExpiredTokens(DateTime.now())
  }
  def link(current: Identity, to: Identity) = ???
}
