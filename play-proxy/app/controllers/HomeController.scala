package controllers

import javax.inject._

import play.api._
import play.api.libs.ws.WSClient
import play.api.mvc._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject() (ws: WSClient, conf: Configuration)(implicit ec: ExecutionContext) extends Controller {

  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def delayedEcho(millis: Long) = Action.async {implicit request =>

    val url = conf.getString("backend.url").getOrElse("http://localhost:9000/delay/") + millis

    ws.url(url).withRequestTimeout(2 seconds).get.map { response =>
      Ok(response.body)
    }

  }

  def echoWithRandomDelay(millis: Long, percentage: Int) = Action.async {implicit request =>

    val url = conf.getString("backend.url").getOrElse("http://localhost:9000/delay/") + millis + "/" + percentage

    ws.url(url).withRequestTimeout(5 seconds).get.map { response =>
      Ok(response.body)
    }

  }



}
