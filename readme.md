# Simple time extractor from JIRA json format

This simple app allows you to insert json taken from JIRA worklog-query: 
```JavaScript
http://host/jira/rest/jira-worklog-query/1.2.1/find/worklogs?startDate=2016-06-01&endDate=2016-06-30&user=VojTos
```
and then extract and calculate total time, spending on different tasks by different users. 

## Prerequisites
**You should have installed and properly configured:**
- Java JDK (>= 7)
- Scala (version >= [2.11.8](http://www.scala-lang.org/download/2.11.8.html))
- Simple Build Tool (SBT) (version >= [0.13.11](http://www.scala-sbt.org/download.html)) for building Scala (JVM) and Scala.js projects



## How to build application
1. Go to the project root folder
2. Run `sbt` from the command line
3. Build application by running `fullOptJS` (it will generated the minified version of script)
4. open `view/extractor.html` and check if the path to generated JavaScript conforms to the generated JavaScript (from `target/scala-2.11`). For example if generated file has name 
```scalajstemplate-opt.js``` full path in `view/extractor.html` should look like  ```<script type="text/javascript" src="../target/scala-2.11/scalajstemplate-opt.js"></script>``` 
Correct this if needed.

## How to run application
1. Go to `view` folder and open `extractor.html` in your favorite browser.
