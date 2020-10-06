package gosuslugi

import scala.concurrent.duration._

import java.util.concurrent.ThreadLocalRandom

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class StepSimulation extends Simulation {

  val csvFeeder = csv("src/test/resources/data/requests.csv").circular

  def perMinute(rate : Double): Double = rate / 60

  val httpProtocol = http
    .baseUrl("https://www.gosuslugi.ru")
    .acceptHeader("""text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8""")
    .acceptEncodingHeader("""gzip, deflate""")
    .userAgentHeader("""Mozilla/5.0 (Macintosh; Intel Mac OS X 10.9; rv:31.0) Gecko/20100101 Firefox/31.0""")

  val stepSim = scenario("StepSimalation")
    .rendezVous(1)
    .during(1 second) {
      pace(1 second)
        .exec(http("mainPage")
          .get("/")
          .check(
            status is 200
          )
        )
    }
    .feed(csvFeeder)
    .rendezVous(1)
    .during(1 second) {
      pace(1 second)
        .exec(http("searchRndRq")
          .get("/search?query=${REQUEST}&serviceRecipient=all")
          .check(
            status is 200 //,
            //        regex("${FIND}")
          )
        )
    }


  setUp(
    stepSim.inject(
      constantUsersPerSec(perMinute(5)) during(5 minutes),
      constantUsersPerSec(perMinute(10)) during(5 minutes),
      constantUsersPerSec(perMinute(15)) during(5 minutes),
      constantUsersPerSec(perMinute(20)) during(5 minutes),
      constantUsersPerSec(perMinute(25)) during(5 minutes),
      constantUsersPerSec(perMinute(30)) during(5 minutes)
  ).protocols(httpProtocol))
}
