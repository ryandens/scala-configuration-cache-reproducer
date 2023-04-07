plugins {
    scala
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.scala-lang:scala-library:2.9.3")
}


java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}


tasks.register("reproduce") {
    doLast {
        print(scalaRuntime.inferScalaClasspath(configurations.compileClasspath.get()).isEmpty)
    }
}