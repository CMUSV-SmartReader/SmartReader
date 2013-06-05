import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "SmartReader"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    javaCore,
    javaJdbc,
    javaEbean,
    "com.google.code.morphia" % "morphia" % "0.99.1-SNAPSHOT",
    "org.mongodb" % "mongo-java-driver" % "2.7.2",
    "com.google.code.morphia" % "morphia-logging-slf4j" % "0.99",
    "org.apache.httpcomponents" % "httpclient" % "4.2.5",
    "securesocial" %% "securesocial" % "master-SNAPSHOT",
    "commons-beanutils" % "commons-beanutils" % "1.8.3",
    "com.github.jmkgreen.morphia" % "morphia-logging-slf4j" % "1.00",
    "com.google.code.gson" % "gson" % "2.2.4",
    "asm" % "asm" % "3.3.1",
    "cglib" % "cglib" % "2.2.2",
    "com.thoughtworks.proxytoys" % "proxytoys" % "1.0",
    "rome" % "rome" % "1.0"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here
    resolvers += "Maven repository" at "http://morphia.googlecode.com/svn/mavenrepo/",
    resolvers += "MongoDb Java Driver Repository" at "http://repo1.maven.org/maven2/org/mongodb/mongo-java-driver/",
    resolvers += Resolver.url("play-easymail (release)", url("http://joscha.github.com/play-easymail/repo/releases/"))(Resolver.ivyStylePatterns),
    resolvers += Resolver.url("play-easymail (snapshot)", url("http://joscha.github.com/play-easymail/repo/snapshots/"))(Resolver.ivyStylePatterns),
    resolvers += Resolver.url("sbt-plugin-snapshots", url("http://repo.scala-sbt.org/scalasbt/sbt-plugin-snapshots/"))(Resolver.ivyStylePatterns)
  )

}
