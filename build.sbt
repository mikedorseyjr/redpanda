name := "Red Panda"

version := "1.0"

scalaVersion := "2.9.0-1"


 
libraryDependencies ++= Seq("junit" % "junit" % "4.8" % "test",
"com.mongodb.casbah" % "casbah_2.8.1" % "2.1.5.0",
"net.debasishg" % "sjson_2.8.0" % "0.8",
"net.databinder" % "dispatch-http-json_2.9.0-1" %  "0.8.5",
"net.databinder" % "dispatch-oauth_2.9.0-1" % "0.8.5")
