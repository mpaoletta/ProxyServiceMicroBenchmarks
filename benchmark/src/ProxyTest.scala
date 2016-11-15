package io.redbee.proxytest

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class ProxyTest extends Simulation {

	val users = envIntOrElse("GATLING_USERS", 1000)
	val delay = envIntOrElse("GATLING_DELAY", 1000)
	val variation = envIntOrElse("GATLING_DELAY_VARIATION", 50)
	val repetitions = envIntOrElse("GATLING_REPETITIONS", 10)
	val rampUpSeconds = envIntOrElse("GATLING_RAMP_UP", 10)
	val protocolName = envOrElse("GATLING_PROTOCOL", "direct")
	val gatlingScenarioName = envOrElse("GATLING_SCENARIO", "simple")
	val protocolUrl = envOrElse(s"GATLING_${protocolName.toUpperCase}_URL", "http://localhost:9000")
	val hiccupDelay = envIntOrElse("GATLING_HICCUP_DELAY", 2000)
	val hiccupRepetitions = envIntOrElse("GATLING_HICCUP_REPETITIONS", 3)

	val protocol = protoHttpProtocol.baseURL(protocolUrl)

	println(s"Usando protocolo $protocolName en $protocolUrl")
	println("Configuracion:") 
	gatlingEnv.foreach(kv => println(s"- ${kv._1}: ${kv._2}"))

	val simplestScenario = scenario("SimplestTest" + protocolName.capitalize).repeat(repetitions, "n") {
		exec(http("delay")
			.get("/delay/" + delay))
	}

	val simpleScenario = scenario("SimpleTest" + protocolName.capitalize).repeat(repetitions, "n") {
		exec(http("delay")
			.get(s"/delay/$delay/$variation"))
	}

	def scenarioWithSlowdown(scenarioName: String, slowdownDelay: Long, slowdownRepetitions: Int) = scenario(scenarioName + protocolName.capitalize).repeat(repetitions, "n") {
		exec(http("delay")
			.get(s"/delay/$delay/$variation"))
	}.repeat(slowdownRepetitions, "m") {
		exec(http("delay")
			.get(s"/delay/$slowdownDelay"))		
	}.repeat(repetitions, "o") {
		exec(http("delay")
			.get(s"/delay/$delay/$variation"))
	}

	val scenarioWithHiccup = scenarioWithSlowdown("WithHiccup")
	val scenarioWithDramaticFailure = scenarioWithSlowdown("withDramaticFailure", 3600000, 30)

	val scn = gatlingScenarioName match {
		case "simplest" => simplestScenario
		case "simple" => simpleScenario
		case "hiccup" => scenarioWithHiccup
		case "dramaticFailure" => scenarioWithDramaticFailure
	}



	setUp(scn.inject(rampUsers(users) over (rampUpSeconds seconds))).protocols(protocol)

	def protoHttpProtocol = http
		.inferHtmlResources()
		.acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
		.acceptLanguageHeader("en-US,en;q=0.5")
		.userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.11; rv:33.0) Gecko/20100101 Firefox/33.0")


	def envIntOrElse(name: String, default: Int): Int = envOrElse(name, default.toString).toInt
	def envOrElse(name: String, default: String): String = sys.env.get(name).getOrElse(default)
	def gatlingEnv: Map[String, String] = sys.env.filter(kv => kv._1.startsWith("GATLING_"))

}