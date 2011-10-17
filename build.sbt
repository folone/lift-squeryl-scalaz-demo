organization := "sk.softwave"

name := "LiftBasic"

version := "0.1"

scalaVersion := "2.9.1"

// scalacOptions += "-deprecation"

seq(webSettings :_*)

resolvers += "Java.net Maven2 Repository" at "http://download.java.net/maven/2/"

resolvers += "Scala Tools Releases" at "http://scala-tools.org/repo-releases/"

resolvers += "Scala Tools Snapshots" at "http://scala-tools.org/repo-snapshots/"

// necessary for gData-calendar
resolvers += "Mandubian Mvn" at "http://mandubian-mvn.googlecode.com/svn/trunk/mandubian-mvn/repository" 

libraryDependencies ++= {
    val liftVersion = "2.4-M4"
    Seq(
    "net.liftweb" %% "lift-squeryl-record" % liftVersion % "compile->default",
    "net.liftweb" %% "lift-widgets" % liftVersion % "compile->default")
  }

libraryDependencies ++= Seq(
  "javax.servlet" % "servlet-api" % "2.5" % "provided->default",
  "com.novocode" % "junit-interface" % "0.7" % "test->default", //sbt's JUnit4 test interface
  "org.scala-tools.testing" %% "specs" % "1.6.9" % "test->default", 
  //"org.eclipse.jetty" % "jetty-webapp" % "7.3.0.v20110203" % "container,test", // For Jetty 7
  "org.mortbay.jetty" % "jetty" % "6.1.25" % "container,test", // For Jetty 6
  "mysql" % "mysql-connector-java" % "5.1.15" % "compile->default",
  "ch.qos.logback" % "logback-classic" % "0.9.27" % "compile->default",
  "commons-io" % "commons-io" % "1.3.2" % "compile->default",
  "org.apache.lucene" % "lucene-core" % "3.2.0" % "compile->default",
  "org.apache.lucene" % "lucene-highlighter" % "3.2.0" % "compile->default",
  "net.tanesha.recaptcha4j" % "recaptcha4j" % "0.0.7" % "compile->default",
  "com.google.gdata" % "gdata-calendar-2.0" % "1.41.5" % "compile->default",
  "org.mindrot" % "jbcrypt" % "0.3m" % "compile->default",
  "org.apache.poi" % "poi" % "3.7" % "compile->default",
  "com.jolbox" % "bonecp" % "0.7.1.RELEASE" % "compile->default"
)


// If using JRebel uncomment next line
scanDirectories in Compile := Nil