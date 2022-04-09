package proj756

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._

object Utility_Alt {
  /*
    Utility to get an Int from an environment variable.
    Return defInt if the environment var does not exist
    or cannot be converted to a string.
  */
  def envVarToInt(ev: String, defInt: Int): Int = {
    try {
      sys.env(ev).toInt
    } catch {
      case e: Exception => defInt
    }
  }

  /*
    Utility to get an environment variable.
    Return defStr if the environment var does not exist.
  */
  def envVar(ev: String, defStr: String): String = {
    sys.env.getOrElse(ev, defStr)
  }
}

object RMusic_Alt {

  val feeder = csv("music.csv").eager.random

  val rmusic = forever("i") {
    feed(feeder)
    .exec(http("RMusic ${i}")
      .get("/api/v1/music/${UUID}"))
      .pause(1)
  }

}

object DMusic_Alt {

  val feeder = csv("music.csv").eager.random

  val dmusic = feed(feeder)
              exec(http("DMusic")
                .delete("/api/v1/music/${UUID}")
                //.delete("/api/v1/music/75228fe1-b435-40fe-b24c-50c33858d8a8")
                .check(status.is(200)))
                .pause(1)
 
}

object RWDMusic_Alt {

  val feeder = csv("music.csv").eager.circular

  val rwdmusic = forever("i"){
                feed(feeder)
                .exec(http("Write Music ${i}")
                        .post("/api/v1/music/")
                        .header("Content-Type" , "application/json")
                        .body(StringBody(string = """{
                            "Artist": "${Artist}",
                            "SongTitle": "${SongTitle}"
                          }""" ))
                        .check(status.is(200))
                        .check(jsonPath("$..music_id").ofType[String].saveAs("music_id")))
                .pause(1)
                .exec(http("Read Music ${i}")
                        .get("/api/v1/music/${music_id}")
                        .check(status.is(200)))
                .pause(1)
                .exec(http("Delete Music ${i}")
                        .delete("/api/v1/music/${music_id}")
                        .check(status.is(200)))
                .pause(1)
  }
}

object RUser_Alt {

  val feeder = csv("users.csv").eager.circular

  val ruser = forever("i") {
    feed(feeder)
    .exec(http("RUser ${i}")
      .get("/api/v1/user/${UUID}"))
    .pause(1)
  }

}

object RWUser_Alt {

  val feeder = csv("users.csv").eager.circular

  val rwuser = feed(feeder)
              .exec(http("WUser")
              .post("/api/v1/user/${UUID}"))
              .exec(http("Write User")
                        .post("/api/v1/user/${UUID}")
                        .header("Content-Type" , "application/json")
                        .body(StringBody(string = """{
                            "lname": "${lname}",
                            "fname": "${fname}"
                            "email": "${email}"
                          }""" ))
                        .check(status.is(200))
                        .check(jsonPath("$..user_id").ofType[String].saveAs("user_id")))
              .pause(1)
              .exec(http("RUser")
                .get("/api/v1/user/${user_id}")
                .check(status.is(200)))
              .pause(1)
}

object RPlaylist_Alt {

  val feeder = csv("playlist.csv").eager.circular

  val rplaylist = forever("i") {
    feed(feeder)
    .exec(http("RPlaylist ${i}")
      .get("/api/v1/playlist/${playlist_id}"))
    .pause(1)
  }

}

/*
  After one S1 read, pause a random time between 1 and 60 s
*/
object RUserVarying_Alt {
  val feeder = csv("users.csv").eager.circular

  val ruser = forever("i") {
    feed(feeder)
    .exec(http("RUserVarying ${i}")
      .get("/api/v1/user/${UUID}"))
    .pause(1, 60)
  }
}

/*
  After one S2 read, pause a random time between 1 and 60 s
*/

object RMusicVarying_Alt {
  val feeder = csv("music.csv").eager.circular

  val rmusic = forever("i") {
    feed(feeder)
    .exec(http("RMusicVarying ${i}")
      .get("/api/v1/music/${UUID}"))
    .pause(1, 60)
  }
}

/*
  After one S3 read, pause a random time between 1 and 60 s
*/

object RPlaylistVarying_Alt {
  val feeder = csv("playlist.csv").eager.circular

  val rplaylist = forever("i") {
    feed(feeder)
    .exec(http("RPlaylistVarying ${i}")
      .get("/api/v1/playlist/${playlist_id}"))
    .pause(1, 60)
  }
}

/*
  Failed attempt to interleave reads from User and Music tables.
  The Gatling EDSL only honours the second (Music) read,
  ignoring the first read of User. [Shrug-emoji] 
 */
object RBoth_Alt {

  val u_feeder = csv("users.csv").eager.circular
  val m_feeder = csv("music.csv").eager.random

  val rboth = forever("i") {
    feed(u_feeder)
    .exec(http("RUser ${i}")
      .get("/api/v1/user/${UUID}"))
    .pause(1);

    feed(m_feeder)
    .exec(http("RMusic ${i}")
      .get("/api/v1/music/${UUID}"))
      .pause(1)
  }

}

object RAll_Alt {

  val u_feeder = csv("users.csv").eager.circular
  val m_feeder = csv("music.csv").eager.random
  val p_feeder = csv("playlist.csv").eager.random

  val rall = forever("i") {
    feed(u_feeder)
    .exec(http("RUser ${i}")
      .get("/api/v1/user/${UUID}"))
    .pause(1);

    feed(m_feeder)
    .exec(http("RMusic ${i}")
      .get("/api/v1/music/${UUID}"))
      .pause(1)

    feed(p_feeder)
    .exec(http("RPlaylist ${i}")
      .get("/api/v1/playlist/${playlist_id}"))
      .pause(1)
  }

}
// Get Cluster IP from CLUSTER_IP environment variable or default to 127.0.0.1 (Minikube)
class ReadTablesSim_Alt extends Simulation {
  val httpProtocol = http
    .baseUrl("http://" + Utility_Alt.envVar("CLUSTER_IP", "127.0.0.1") + "/")
    .acceptHeader("application/json,text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    .authorizationHeader("Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoiZGJmYmMxYzAtMDc4My00ZWQ3LTlkNzgtMDhhYTRhMGNkYTAyIiwidGltZSI6MTYwNzM2NTU0NC42NzIwNTIxfQ.zL4i58j62q8mGUo5a0SQ7MHfukBUel8yl8jGT5XmBPo")
    .acceptLanguageHeader("en-US,en;q=0.5")
}

class ReadUserSim_Alt extends ReadTablesSim_Alt {
  val scnReadUser = scenario("ReadUser")
      .exec(RUser_Alt.ruser)

  setUp(
    scnReadUser.inject(atOnceUsers(Utility_Alt.envVarToInt("USERS", 1)))
  ).protocols(httpProtocol)
}

class ReadWriteUserSim_Alt extends ReadTablesSim_Alt {
  val scnReadWriteUser = scenario("ReadWriteUser")
    .exec(RWUser_Alt.rwuser)

  setUp(
    scnReadWriteUser.inject(atOnceUsers(Utility_Alt.envVarToInt("USERS", 1)))
  ).protocols(httpProtocol)
}

class ReadMusicSim_Alt extends ReadTablesSim_Alt {
  val scnReadMusic = scenario("ReadMusic")
    .exec(RMusic_Alt.rmusic)

  setUp(
    scnReadMusic.inject(atOnceUsers(Utility_Alt.envVarToInt("USERS", 1)))
  ).protocols(httpProtocol)
}

class DeleteMusicSim_Alt extends ReadTablesSim_Alt {
  val scnDeleteMusic = scenario("DeleteMusic")
    .exec(DMusic_Alt.dmusic)

  setUp(
    scnDeleteMusic.inject(atOnceUsers(Utility_Alt.envVarToInt("USERS", 1)))
  ).protocols(httpProtocol)
}

class ReadWriteDeleteMusicSim_Alt extends ReadTablesSim_Alt {
  val scnReadWriteDeleteMusic = scenario("ReadWriteDeleteMusic")
    .exec(RWDMusic_Alt.rwdmusic)

  setUp(
    scnReadWriteDeleteMusic.inject(atOnceUsers(Utility_Alt.envVarToInt("USERS", 1)))
  ).protocols(httpProtocol)
}

class ReadPlaylistSim_Alt extends ReadTablesSim_Alt {
  val scnReadPlaylist = scenario("ReadPlaylist")
    .exec(RPlaylist_Alt.rplaylist)

  setUp(
    scnReadPlaylist.inject(atOnceUsers(Utility_Alt.envVarToInt("USERS", 1)))
  ).protocols(httpProtocol)
}

/*
  Read both services concurrently at varying rates.
  Ramp up new users one / 10 s until requested USERS
  is reached for each service.
*/
class ReadBothVaryingSim_Alt extends ReadTablesSim_Alt {
  val scnReadMV = scenario("ReadMusicVarying")
    .exec(RMusicVarying_Alt.rmusic)

  val scnReadUV = scenario("ReadUserVarying")
    .exec(RUserVarying_Alt.ruser)

  val scnReadPV = scenario("ReadPlaylistVarying")
    .exec(RPlaylistVarying_Alt.rplaylist)

  val users = Utility_Alt.envVarToInt("USERS", 10)

  setUp(
    // Add one user per 10 s up to specified value
    scnReadMV.inject(rampConcurrentUsers(1).to(users).during(10*users)),
    scnReadUV.inject(rampConcurrentUsers(1).to(users).during(10*users)),
    scnReadPV.inject(rampConcurrentUsers(1).to(users).during(10*users))
  ).protocols(httpProtocol)
}

class ReadAllSim_Alt extends ReadTablesSim_Alt {
  val scnReadM = scenario("ReadMusic")
    .exec(RMusic_Alt.rmusic)

  val scnReadU = scenario("ReadUser")
    .exec(RUser_Alt.ruser)

  val scnReadP = scenario("ReadPlaylist")
    .exec(RPlaylist_Alt.rplaylist)

  val users = Utility_Alt.envVarToInt("USERS", 10)

  setUp(
    // Add one user per 10 s up to specified value
    scnReadU.inject(atOnceUsers(users)),
    scnReadM.inject(atOnceUsers(users)),
    scnReadP.inject(atOnceUsers(users))
  ).protocols(httpProtocol)
}
/*
  This doesn't work---it just reads the Music table.
  We left it in here as possible inspiration for other work
  (or a warning that this approach will fail).
 */
/*
class ReadBothSim extends ReadTablesSim {
  val scnReadBoth = scenario("ReadBoth")
    .exec(RBoth.rboth)

  setUp(
    scnReadBoth.inject(atOnceUsers(1))
  ).protocols(httpProtocol)
}
*/