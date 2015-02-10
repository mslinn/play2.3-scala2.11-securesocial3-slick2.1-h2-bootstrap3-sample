package models

import org.joda.time.DateTime
import play.api.Play
import play.api.libs.Crypto
import scalikejdbc._
import scalikejdbc.async._
import scalikejdbc.async.FutureImplicits._
import securesocial.core._
import securesocial.core.providers.{UsernamePasswordProvider, MailToken}
import securesocial.core.services.SaveMode

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class BasicUser(id: Long, basicProfiles: Seq[BasicProfile])

object BasicUser extends SQLSyntaxSupport[BasicUser] {

  override val columnNames = Seq("id")

  lazy val bu = BasicUser.syntax

  def db(bu: SyntaxProvider[BasicUser])(rs: WrappedResultSet): BasicUser = db(bu.resultName)(rs)

  def db(bu: ResultName[BasicUser])(rs: WrappedResultSet): BasicUser = {
    BasicUser(rs.long(bu.id), List.empty[BasicProfile])
  }

  def create(basicProfile: BasicProfile)(implicit session: AsyncDBSession = AsyncDB.sharedSession): Future[BasicUser] = {

    def createBasicUser(implicit session: AsyncDBSession): Future[BasicUser] = {
      withSQL(insert.into(BasicUser).append(sqls"DEFAULT VALUES").returningId).updateAndReturnGeneratedKey().future().map(BasicUser(_, List.empty[BasicProfile]))
    }

    AsyncDB.localTx { implicit tx =>
      createBasicUser.flatMap(link(_, basicProfile))
    }
  }

  def find(basicProfile: BasicProfile)(implicit session: AsyncDBSession = AsyncDB.sharedSession): Future[Option[BasicUser]] = {
    withSQL(
      select
        .from(BasicUser as bu)
        .leftJoin(BasicProfileDB as BasicProfileDB.bp)
        .on(bu.id, BasicProfileDB.bp.column("basic_user_id"))
        .where.eq(BasicProfileDB.bp.userId, basicProfile.userId)
    ).one(BasicUser.db(bu)).toMany(BasicProfileDB.opt(BasicProfileDB.bp)).map { (basicUser, basicProfiles) =>
      basicUser.copy(basicProfiles = basicProfiles)
    }.single().future()
  }

  def link(current: BasicUser, to: BasicProfile)(implicit session: AsyncDBSession = AsyncDB.sharedSession): Future[BasicUser] = {
    // create a new BasicProfile in the DB and link it to this BasicUser
    BasicProfileDB.create(to, current.id).map { basicProfile =>
      current.copy(basicProfiles = current.basicProfiles :+ basicProfile)
    }
  }

  private def findBasicProfileWithUsernamePassword(basicUser: BasicUser): Option[BasicProfile] = {
    basicUser.basicProfiles.find(_.providerId == UsernamePasswordProvider.UsernamePassword)
  }

  def passwordInfoFor(basicUser: BasicUser): Future[Option[PasswordInfo]] = {
    Future.successful {
      findBasicProfileWithUsernamePassword(basicUser).flatMap(_.passwordInfo)
    }
  }

  def updatePasswordInfo(basicUser: BasicUser, passwordInfo: PasswordInfo): Future[Option[BasicProfile]] = {
    findBasicProfileWithUsernamePassword(basicUser).fold(Future.successful[Option[BasicProfile]](None)) { basicProfile =>
      BasicProfileDB.updatePasswordInfo(basicProfile, passwordInfo).map(Some(_))
    }
  }

  def save(basicProfile: BasicProfile, mode: SaveMode): Future[BasicUser] = {
    mode match {
      case SaveMode.SignUp =>
        BasicUser.create(basicProfile)
      case SaveMode.LoggedIn =>
        // todo: do I need to update the user?
        BasicUser.find(basicProfile).flatMap { maybeBasicUser =>
          maybeBasicUser.fold(Future.failed[BasicUser](new Exception("User not found")))(Future.successful)
        }
      case SaveMode.PasswordChange =>
        // todo: do I need to update the user?
        BasicUser.find(basicProfile).flatMap { maybeBasicUser =>
          maybeBasicUser.fold(Future.failed[BasicUser](new Exception("User not found")))(Future.successful)
        }
    }
  }

}


object BasicProfileDB extends SQLSyntaxSupport[BasicProfile] {

  override val tableName = "basic_profile"

  override val columns = Seq("user_id", "provider_id", "first_name", "last_name", "full_name", "email",
    "avatar_url", "auth_method", "token", "secret", "access_token", "token_type", "expires_in", "refresh_token",
    "hasher", "password", "basic_user_id")

  lazy val bp = BasicProfileDB.syntax


  def db(bp: SyntaxProvider[BasicProfile])(rs: WrappedResultSet): BasicProfile = db(bp.resultName)(rs)

  def db(bp: ResultName[BasicProfile])(rs: WrappedResultSet): BasicProfile = {
    val maybeOAuth1Info = for {
      token <- rs.stringOpt(bp.column("token"))
      secret <- rs.stringOpt(bp.column("secret"))
    } yield OAuth1Info(token, secret)

    val maybeOAuth2Info = rs.stringOpt(bp.column("access_token")).map { accessToken =>
      OAuth2Info(
        accessToken,
        rs.stringOpt(bp.column("token_type")),
        rs.intOpt(bp.column("expires_in")),
        rs.stringOpt(bp.column("refresh_token"))
      )
    }

    val maybePasswordInfo = rs.stringOpt(bp.column("password")).map { password =>
      PasswordInfo(rs.string(bp.column("hasher")), password, Play.current.configuration.getString("application.secret"))
    }

    BasicProfile(
      providerId = rs.string(bp.providerId),
      userId = rs.string(bp.userId),
      firstName = rs.stringOpt(bp.firstName),
      lastName = rs.stringOpt(bp.lastName),
      fullName = rs.stringOpt(bp.fullName),
      email = rs.stringOpt(bp.email),
      avatarUrl = rs.stringOpt(bp.avatarUrl),
      authMethod = AuthenticationMethod(rs.string(bp.authMethod)),
      oAuth1Info = maybeOAuth1Info,
      oAuth2Info = maybeOAuth2Info,
      passwordInfo = maybePasswordInfo
    )
  }

  def opt(bp: SyntaxProvider[BasicProfile])(rs: WrappedResultSet): Option[BasicProfile] = {
    Some(db(bp)(rs))
  }

  def create(basicProfile: BasicProfile, basicUserId: Long)(implicit session: AsyncDBSession = AsyncDB.sharedSession): Future[BasicProfile] = {
    withSQL(
      insert
        .into(BasicProfileDB)
        .namedValues(
          column.providerId -> basicProfile.providerId,
          column.userId -> basicProfile.userId,
          column.firstName -> basicProfile.firstName,
          column.lastName -> basicProfile.lastName,
          column.fullName -> basicProfile.fullName,
          column.email -> basicProfile.email,
          column.avatarUrl -> basicProfile.avatarUrl,
          column.authMethod -> basicProfile.authMethod.method,
          column.column("token") -> basicProfile.oAuth1Info.map(_.token),
          column.column("secret") -> basicProfile.oAuth1Info.map(_.secret),
          column.column("access_token") -> basicProfile.oAuth2Info.map(_.accessToken),
          column.column("expires_in") -> basicProfile.oAuth2Info.flatMap(_.expiresIn),
          column.column("refresh_token") -> basicProfile.oAuth2Info.flatMap(_.refreshToken),
          column.column("token_type") -> basicProfile.oAuth2Info.flatMap(_.tokenType),
          column.column("hasher") -> basicProfile.passwordInfo.map(_.hasher),
          column.column("password") -> basicProfile.passwordInfo.map(_.password),
          column.column("basic_user_id") -> basicUserId
        )
    ).update().future().map { _ =>
      basicProfile
    }
  }

  def updatePasswordInfo(basicProfile: BasicProfile, passwordInfo: PasswordInfo)(implicit session: AsyncDBSession = AsyncDB.sharedSession): Future[BasicProfile] = {
    withSQL(
      update(BasicProfileDB)
        .set(
          column.column("hasher") -> passwordInfo.hasher,
          column.column("password") -> passwordInfo.password
        )
        .where.eq(bp.userId, basicProfile.userId)
    ).update().future().map { _ =>
      basicProfile.copy(passwordInfo = Some(passwordInfo))
    }
  }

  def find(providerId: String, userId: String)(implicit session: AsyncDBSession = AsyncDB.sharedSession): Future[Option[BasicProfile]] = {
    withSQL(
      select
        .from[BasicProfile](BasicProfileDB as bp)
        .where.eq(bp.providerId, providerId).and.eq(bp.userId, userId)
    ).map(BasicProfileDB.db(bp)).single().future()
  }

  def findByEmailAndProvider(email: String, providerId: String)(implicit session: AsyncDBSession = AsyncDB.sharedSession): Future[Option[BasicProfile]] = {
    withSQL(
      select
        .from[BasicProfile](BasicProfileDB as bp)
        .where.eq(bp.email, email).and.eq(bp.providerId, providerId)
    ).map(BasicProfileDB.db(bp)).single().future()
  }

}

object TokenDB extends SQLSyntaxSupport[MailToken] {

  override val tableName = "token"

  override val columns = Seq("uuid", "email", "creation_time", "expiration_time", "is_sign_up")

  lazy val t = TokenDB.syntax


  def db(t: SyntaxProvider[MailToken])(rs: WrappedResultSet): MailToken = db(t.resultName)(rs)

  def db(t: ResultName[MailToken])(rs: WrappedResultSet): MailToken = {
    MailToken(
      uuid = rs.string(t.uuid),
      email = rs.string(t.email),
      creationTime = rs.jodaDateTime(t.creationTime),
      expirationTime = rs.jodaDateTime(t.expirationTime),
      isSignUp = rs.boolean(t.isSignUp)
    )
  }

  def deleteToken(uuid: String)(implicit session: AsyncDBSession = AsyncDB.sharedSession): Future[Option[MailToken]] = {
    withSQL(
      delete.from(TokenDB).where.eq(t.uuid, uuid)
    ).update().future().map(_ => None)
  }

  def findToken(uuid: String)(implicit session: AsyncDBSession = AsyncDB.sharedSession): Future[Option[MailToken]] = {
    withSQL(
      select
        .from[MailToken](TokenDB as t)
        .where.eq(t.uuid, uuid)
    ).map(TokenDB.db(t)).single().future()
  }

  def deleteExpiredTokens(implicit session: AsyncDBSession = AsyncDB.sharedSession): Future[Int] = {
    // todo: verify logic
    withSQL(
      delete.from(TokenDB).where.lt(t.expirationTime, DateTime.now())
    ).update().future()
  }

  def saveToken(token: MailToken)(implicit session: AsyncDBSession = AsyncDB.sharedSession): Future[MailToken] = {
    withSQL(
      insert
        .into(TokenDB)
        .namedValues(
          column.uuid -> token.uuid,
          column.email -> token.email,
          column.creationTime -> token.creationTime,
          column.expirationTime -> token.expirationTime,
          column.isSignUp -> token.isSignUp
        )
    ).update().future().map { _ =>
      token
    }
  }

}