package channels.examples
import scala.concurrent.Future
import play.api.libs.ws.WS
import backend.SocketStream
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json._
import channels.Channel

/** This is a simple widget that fetches JSON data from an external endpoint
  * every time the run method is called
  *
  * It parses the time value out of the JSON response and pushes it to the socket channel
  *
  */
class JsonChannel(key: String) extends Channel[Future[Unit]] {

  private val url = "https://vacancy.io/api/v1/stats"

  def handleResponse(users: Int,
                     companies: Int,
                     lastUserSignUp: String,
                     lastCompanySignUp: String): Unit = {
    SocketStream.push("users", users.toString)
    SocketStream.push("companies", companies.toString)
    SocketStream.push("last-user-sign-up", lastUserSignUp)
    SocketStream.push("last-company-sign-up", lastCompanySignUp)
  }

  def run(): Future[Unit] = {
    WS.url(url).get().map { response ⇒
      val json = Json.parse(response.body)
      val users = (json \ "users").as[Int]
      val companies = (json \ "companies").as[Int]
      val lastUserSignUp = (json \ "last_user_sign_up").as[String]
      val lastCompanySignUp = (json \ "last_company_sign_up").as[String]
      // Update the dashboard
      handleResponse(users, companies, lastUserSignUp, lastCompanySignUp)
    }
  }
}
