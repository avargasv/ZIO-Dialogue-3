val scalaVer = "3.4.1"

val zioVersion = "2.0.22"

lazy val dependencies = Seq(
  "dev.zio" %% "zio" % zioVersion
) map (_ % Compile)

lazy val settings = Seq(
  name := "ZIO-Dialogue-3",
  version := "1.0.0",
  scalaVersion := scalaVer,
  libraryDependencies ++= dependencies
)

lazy val root = (project in file("."))
  .settings(settings)
