package service

import models.{TokenDB, BasicProfileDB, BasicUser}
import play.api.Logger
import securesocial.core._
import securesocial.core.providers.MailToken
import securesocial.core.services.{UserService, SaveMode}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class DBUserService extends UserService[BasicUser] {

  override def find(providerId: String, userId: String): Future[Option[BasicProfile]] = {
    BasicProfileDB.find(providerId, userId)
  }

  override def findByEmailAndProvider(email: String, providerId: String): Future[Option[BasicProfile]] = {
    BasicProfileDB.findByEmailAndProvider(email, providerId)
  }

  override def deleteToken(uuid: String): Future[Option[MailToken]] = {
    TokenDB.deleteToken(uuid)
  }

  override def link(current: BasicUser, to: BasicProfile): Future[BasicUser] = {
    BasicUser.link(current, to)
  }

  override def passwordInfoFor(user: BasicUser): Future[Option[PasswordInfo]] = {
    BasicUser.passwordInfoFor(user)
  }

  override def save(profile: BasicProfile, mode: SaveMode): Future[BasicUser] = {
    BasicUser.save(profile, mode)
  }

  override def findToken(token: String): Future[Option[MailToken]] = {
    TokenDB.findToken(token)
  }

  override def deleteExpiredTokens(): Unit = {
    TokenDB.deleteExpiredTokens().foreach(identity)
  }

  override def updatePasswordInfo(user: BasicUser, info: PasswordInfo): Future[Option[BasicProfile]] = {
    BasicUser.updatePasswordInfo(user, info)
  }

  override def saveToken(token: MailToken): Future[MailToken] = {
    TokenDB.saveToken(token)
  }

}
