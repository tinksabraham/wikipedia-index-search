# Wikipedia Dump Index and GUI for searching
Repo for indexing wikidump and able to search index via GUI. Basically it provides functionality indexing wiki dump file
and search for a contributor name or a word. 
>Frontend is using **Angular** framework.
>Backend is using **Spring boot** framework.

## System Requirement & Installation
* Java 8 or more
* Nodejs - preferably version 10.16.2 and npm (node package manager)
* Maven build tool

# First Tool- Creating lucene index on wiki dump file

## Usage
Make sure that you have downloaded the wikidump file before you have execute the jar file.

Navigate to dist/ directory in a terminal and run 
```
java -jar wikipedia-lucene-1.0-SNAPSHOT.jar
    --maxIndexCount [Max count of wikidump parsing to be indexed by lucene] 
    --wikiDumpFilePath [Absolute path to wiki dump file]

e.g.
-> Usage 1 (.bz2 file)
java -jar wikipedia-lucene-1.0-SNAPSHOT.jar 
    --maxIndexCount 1000
    --wikiDumpFilePath D:\UserData\temp\projects\Wikidump-search\pages\enwiki-latest-pages-articles.xml.bz2

-> Usage 2 (xml file)
java -jar wikipedia-lucene-1.0-SNAPSHOT.jar 
    --maxIndexCount 1000 
    --wikiDumpFilePath D:\UserData\temp\projects\Wikidump-search\pages\enwiki-latest-pages-articles.xml
    
```

**Note** Here the indexed directory will be generated and named as *indexedDir*. Also please delete this *indexedDir* 
before re-executing. In fact it will throw an error for user to know. 

### Possible troubleshooting
When executing the run, it will generate a log file *lucene.log*. Look for possible **error** which points to issues.
Also you will see logs (info, warn, error) in the terminal as well. 
Currently it will output some info logs to see *id*, *contributor*, *title* during indexing.

# Second Tool- GUI for searching index
Here both backend and frontend needs to started locally. Below are the steps

## Backend
Backend is built with Spring boot (2.1.7.RELEASE) - latest stable version

Navigate to backend/ directory in a terminal and run or also you can import it as a maven project in your IDE.

```
mvn clean install -DskipTests

mvn spring-boot:run -Dspring-boot.run.arguments=--indexed.directory.path=[indexed directory path]

e.g.
mvn spring-boot:run -Dspring-boot.run.arguments=--indexed.directory.path=/d/UserData/temp/projects/wikipedia-index-search/indexedDir
```

**Note** Here *-DskipTests* is needed because I have written some unit test to check searching on indexed directory which will fail.
It was added only to make sure search is working during development.

**Note** Here you should pass the absolute directory path where indexed files are located. Only directory name is needed.
Also when you pass a wrong directory and when GUI makes a search, BE will responds with custom IO exception and FE will
show a toaster. Please check the screenshots.

   
