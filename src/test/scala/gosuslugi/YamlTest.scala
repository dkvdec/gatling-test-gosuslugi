package gosuslugi

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import pureconfig.generic.auto.exportReader
import ru.tinkoff.gatling.config.SimulationConfig._
import ru.tinkoff.gatling.profile._
import ru.tinkoff.gatling.profile.http.HttpProfileConfig

class YamlTest extends Simulation {
	val profileConfigName = "src/test/resources/profile.conf"
//	val someTestPlan      = constantUsersPerSec(intensity) during stageDuration
	val httpProtocol      = io.gatling.http.Predef.http.baseUrl(baseUrl)
	val config: HttpProfileConfig = new ProfileBuilder[HttpProfileConfig].buildFromYaml(profileConfigName)
	val scn: ScenarioBuilder = config.toRandomScenario

	setUp(
		scn.inject(
			atOnceUsers(1)
		).protocols(httpProtocol)
	).maxDuration(10 )
}
