
import play.api.mvc._
//import play.api.filters.gzip.GzipFilter
import controllers.CustomRoutesService
import java.lang.reflect.Constructor
import securesocial.core.RuntimeEnvironment
import service.{ SecureSocialEventListener, DBUserService }
import models.User

object Global extends WithFilters(LoggingFilter) with play.api.GlobalSettings {

  object MyRuntimeEnvironment extends RuntimeEnvironment.Default[User] {
    override lazy val routes = new CustomRoutesService
    override lazy val userService = new DBUserService
    override lazy val eventListeners = List(new SecureSocialEventListener)
  }

  /** An implementation that checks if the controller expects a RuntimeEnvironment and passes the instance to it if required.
   * This can be replaced by any DI framework to inject it differently.
   * @param controllerClass
   * @tparam A
   * @return */
  override def getControllerInstance[A](controllerClass: Class[A]): A = {
    val instance = controllerClass.getConstructors.find { c =>
      val params = c.getParameterTypes
      params.length == 1 && params(0) == classOf[RuntimeEnvironment[User]]
    }.map {
      _.asInstanceOf[Constructor[A]].newInstance(MyRuntimeEnvironment)
    }
    instance.getOrElse(super.getControllerInstance(controllerClass))
  }
}
