package controllers

import javax.inject._

import akka.actor._
import play.api.mvc._

import scala.concurrent.duration._
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.{ExecutionContext, Future, Promise}
import java.util.Random

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject() (actorSystem: ActorSystem)(implicit exec: ExecutionContext) extends Controller {

  val holas = List("Hola", "Hello", "Namasti", "Olá", "Nomoshkar", "Selamat Pagi", "Selamat Siang", "Selamat Malam", "Ahalan", "Zdravstvuyte/Priviet", "Konnichi wa")
  val rand = new Random(System.currentTimeMillis())

  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }


  def delayedEcho(delay: Long) = Action.async { request =>
    getFutureMessage(delay milliseconds).map { msg => Ok(msg) }
  }


  private def getFutureMessage(delayTime: FiniteDuration): Future[String] = {
    val promise: Promise[String] = Promise[String]()
    actorSystem.scheduler.scheduleOnce(delayTime) {
      promise.success(holas(rand.nextInt(holas.length)))
    }
    promise.future
  }


}

