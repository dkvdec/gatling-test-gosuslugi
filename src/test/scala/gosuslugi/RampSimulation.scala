package gosuslugi

import scala.concurrent.duration._

import java.util.concurrent.ThreadLocalRandom

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class RampSimulation extends Simulation {

  val csvFeeder = csv("src/test/resources/data/requests.csv").circular

  def perMinute(rate : Double): Double = rate / 60

  val httpProtocol = http
    .baseUrl("https://www.gosuslugi.ru")
    .acceptHeader("""text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8""")
    .acceptEncodingHeader("""gzip, deflate""")
    .userAgentHeader("""Mozilla/5.0 (Macintosh; Intel Mac OS X 10.9; rv:31.0) Gecko/20100101 Firefox/31.0""")


  val rampSim = scenario("RampSimalation")
//    .rendezVous(1)
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
//    .rendezVous(1)
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
    rampSim.inject(
      rampUsersPerSec(0) to (perMinute(30)) during(5 minutes)
//      rampConcurrentUsers(0) to (1) during (5 minutes)
  ).protocols(httpProtocol)).throttle(
    jumpToRps(1),
    holdFor(5 minutes)
  )
}
