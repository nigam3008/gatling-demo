import java.util.UUID.randomUUID

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._
import scala.language.postfixOps

class UserSearchPerfTest extends Simulation {

  val httpConf = http.baseUrl("http://localhost:8080")
    .acceptHeader("application/json")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Mozilla/5.0 (Windows NT 5.1; rv:31.0) Gecko/20100101 Firefox/31.0")
    .header("correlation-id", randomUUID().toString())



  object apis {
    val userSearchApi = exec(
      http("UserSearchRequest")
        .get("/users")
        .header("business-context-id", "get-user")
        .check(status.is(200)) // verifying status response
        .check(jsonPath("$[*]").count.is(1)))

    val createUserFeed = jsonFile("feeders/users.json").circular.random

    val userCreateApi = feed(createUserFeed)
    .exec(http("UserCreateRequest")
      .post("/users")
        .header("Content-Type", "application/json")
        .body(ElFileBody("request.json"))
      .header("business-context-id", "create-user")
      .check(status.is(200)))

    val userDeleteApi = exec(http("UserDeleteRequest")
      .delete("/users")
      .header("business-context-id", "delete-user")
      .check(status.is(200)))
  }

  val scnForUserCreateNRead = scenario("user_Create_N_Read")
    .exec(apis.userCreateApi)
    .pause(1 second) // think time
    .exec(apis.userSearchApi) // verifying size of response

  val scnForUserDelete = scenario("user_Delete")
    .exec(apis.userDeleteApi)

  setUp(
    scnForUserCreateNRead.inject(
      incrementUsersPerSec(5)
        .times(5)
        .eachLevelLasting(10 seconds)
        .separatedByRampsLasting(10 seconds)
        .startingFrom(10)
    ).throttle(
      jumpToRps(100),
      holdFor(60)
    ),

    scnForUserDelete.inject(
      incrementUsersPerSec(5)
        .times(5)
        .eachLevelLasting(10 seconds)
        .separatedByRampsLasting(10 seconds)
        .startingFrom(10)
    ).throttle(
      jumpToRps(100),
      holdFor(60)
    )
  ).protocols(httpConf)
    .assertions(forAll.responseTime.max.lte(600))
}