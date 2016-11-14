package io.redbee.proxytest

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class ProxyTest extends Simulation {

	val users = envIntOrElse("GATLING_USERS", 1000)
	val delay = envIntOrElse("GATLING_DELAY", 1000)
	val repetitions = envIntOrElse("GATLING_REPETITIONS", 10)
	val rampUpSeconds = envIntOrElse("GATLING_RAMP_UP", 10)
	val protocolName = envOrElse("GATLING_PROTOCOL", "direct")
	val protocolUrl = envOrElse(s"GATLING_${protocolName.toUpperCase}_URL", "http://localhost:9000")
	val protocol = protoHttpProtocol.baseURL(protocolUrl)

	val scn = scenario("ProxyTest" + protocolName.capitalize).repeat(repetitions, "n") {
		exec(http("delay")
			.get("/delay/" + delay))
	}
	setUp(scn.inject(rampUsers(users) over (rampUpSeconds seconds))).protocols(protocol)

	def protoHttpProtocol = http
		.inferHtmlResources()
		.acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
		.acceptLanguageHeader("en-US,en;q=0.5")
		.userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.11; rv:33.0) Gecko/20100101 Firefox/33.0")


	def envIntOrElse(name: String, default: Int): Int = envOrElse(name, default.toString).toInt
	def envOrElse(name: String, default: String): String = sys.props.get(name).getOrElse(sys.env.get(name).getOrElse(default))

}