enablePlugins(ScalaJSPlugin)

name := "ScalaJsTemplate"

version := "1.0"

scalaVersion := "2.11.9"

libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.0"
libraryDependencies += "be.doeraene" %%% "scalajs-jquery" % "0.9.0"
libraryDependencies += "com.lihaoyi" %%% "scalatags" % "0.5.5"
libraryDependencies += "com.lihaoyi" %%% "upickle" % "0.4.1"