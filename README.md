# Scala Configuration Cache Issue

There is an extremely minor issue with the Scala plugin's support for the configuration cache. Caching of the 
`scalaClasspath` field of the task `compileScala` fails in cases where it does not without the configuration cache
enabled on very old versions of Scala that do not support incremental compilation (less than `2.10.0`). This is 
because the `LazilyInitializedFileCollection` is initialized when the configuration cache is enabled and causes
artifact resolution to fail. 

While I understand supporting old versions of scala may not be very useful for many Gradle users, there are legitimate
reasons for wanting to use Gradle with old Scala versions such as in the 
[OpenTelemetry Java Instrumentation](https://github.com/open-telemetry/opentelemetry-java-instrumentation/blob/main/instrumentation/scala-fork-join-2.8/javaagent/build.gradle.kts#L18)
project's instrumentation of Scala library APIs, enabling them to test across scala versions and verify compatibility. 

This bug can be reproduced in the following ways:

```shell
$ ./gradlew build # demonstrates that this project can be built without the configuration cache enabled

BUILD SUCCESSFUL in 2s
1 actionable task: 1 executed

```

Successful build scan available [here](https://gradle.com/s/44aa37tzdlqio).


```shell
$ ./gradlew --configuration-cache build # demonstrates that this project fails to be built with the configuration cache enabled
Calculating task graph as no configuration cache is available for tasks: build

0 problems were found storing the configuration cache.

See the complete report at file:///Users/ryandens/git/ryandens/scala-cc/build/reports/configuration-cache/ayqbbyfb6dey203yri43upt4b/2w7jjb2oz6ngdfql62uh3s77a/configuration-cache-report.html

FAILURE: Build failed with an exception.

* What went wrong:
Configuration cache state could not be cached: field `scalaClasspath` of task `:compileScala` of type `org.gradle.api.tasks.scala.ScalaCompile`: error writing value of type 'org.gradle.api.tasks.ScalaRuntime$1'
> Could not resolve all files for configuration ':detachedConfiguration1'.
   > Could not find org.scala-sbt:compiler-bridge_2.9:1.6.1.
     Searched in the following locations:
       - https://repo.maven.apache.org/maven2/org/scala-sbt/compiler-bridge_2.9/1.6.1/compiler-bridge_2.9-1.6.1.pom
     If the artifact you are trying to retrieve can be found in the repository but without metadata in 'Maven POM' format, you need to adjust the 'metadataSources { ... }' of the repository declaration.
     Required by:
         project :

* Try:
> Run with --stacktrace option to get the stack trace.
> Run with --info or --debug option to get more log output.

* Get more help at https://help.gradle.org

BUILD FAILED in 599ms
```

Failed configuration cache build scan available [here](https://gradle.com/s/nkj7roei43bjk).


```shell
$ ./gradlew reproduce # demonstrates that this build can fail without configuration cahce enabled, if the scalaClasspath configuration is initialized 
> Task :reproduce FAILED

FAILURE: Build failed with an exception.

* What went wrong:
Execution failed for task ':reproduce'.
> Could not resolve all files for configuration ':detachedConfiguration1'.
   > Could not find org.scala-sbt:compiler-bridge_2.9:1.6.1.
     Searched in the following locations:
       - https://repo.maven.apache.org/maven2/org/scala-sbt/compiler-bridge_2.9/1.6.1/compiler-bridge_2.9-1.6.1.pom
     If the artifact you are trying to retrieve can be found in the repository but without metadata in 'Maven POM' format, you need to adjust the 'metadataSources { ... }' of the repository declaration.
     Required by:
         project :

* Try:
> Run with --stacktrace option to get the stack trace.
> Run with --info or --debug option to get more log output.

* Get more help at https://help.gradle.org

BUILD FAILED in 431ms
1 actionable task: 1 executed
```

Failed build scan without configuration cache enabled [here](https://gradle.com/s/zyoeh2mczrtnu)
