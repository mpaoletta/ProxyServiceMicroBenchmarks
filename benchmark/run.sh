#!/usr/bin/env scala

import scala.sys.process._
import java.io.File

val reportName = sys.env("GATLING_PROTOCOL")+"-"+sys.env("GATLING_USERS")+"-"+sys.env("GATLING_DELAY")+"-"+sys.env("GATLING_REPETITIONS")+"-"+sys.env("GATLING_SCENARIO")

println("Ejecutando " + reportName)

val gatlingPath = sys.env.get("GATLING_HOME").getOrElse(throw new Exception("No esta definida GATLING_HOME")) + "/bin/gatling.sh"

val cmd = s"$gatlingPath -s io.redbee.proxytest.ProxyTest -m -rf /tmp/proxy-reports -sf ./src"

var buffer = new StringBuffer()
val processLogger = ProcessLogger(line => {
	println(line)
	buffer.append(line).append("\n")
}, line => {
	println("ERROR: " + line)
})

cmd ! processLogger

val tag = "Please open the following file:"

buffer.toString.split("\n").find(_.startsWith(tag)) match {

	case Some(line) => {
		val folder = new File(line.drop(tag.size)).getParentFile
		val dst = new File(folder.getParentFile, reportName)
		val mv = s"mv $folder $dst"
		mv !
	}
	case None => println("No se genero salida. Revisar output:\n" + buffer)
}



