enablePlugins(ScalaJSPlugin)

name := "ScalaJsTemplate"
version := "1.0"

scalaVersion := "2.11.8"

// Scala.js dependencies

// Scala.js facades
libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.0"
libraryDependencies += "be.doeraene" %%% "scalajs-jquery" % "0.9.0"
libraryDependencies += "com.lihaoyi" %%% "scalatags" % "0.5.5"

// Scala.js libraries
libraryDependencies += "com.lihaoyi" %%% "upickle" % "0.4.1"

// JavaScript dependencies
jsDependencies += "org.webjars" % "jquery" % "2.2.4" / "jquery.js" minified "jquery.min.js"
jsDependencies += "org.webjars" % "bootstrap" % "3.3.6" / "bootstrap.js" minified "bootstrap.min.js" dependsOn "jquery.js"
