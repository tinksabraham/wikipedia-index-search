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

**Note** Here the indexed directory will be generated and named as *indexedDir*. 

**Note** Also please delete this *indexedDir* before re-running once it already created.
In fact it will throw an error in the log for user to know. 

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

**Note** Now embedded tomcat server will be started at port 8080. 
If port already used, please use a different port and mention it in application.properties

**Note** Here *-DskipTests* is needed because I have written some unit test to check searching on indexed directory which will fail.
It was added only to make sure search is working during development.

**Note** Here you should pass the absolute directory path where indexed files are located. Only directory name is needed.
Also when you pass a wrong directory and when GUI makes a search, BE will responds with custom IO exception and FE will
show a toaster. Please check the screenshots.

## frontend
Frontend is built with Angular framework (version 7)

Make sure you have nodejs and npm installed on the machine. 
Navigate to frontend/ directory in a terminal and run. 

```
npm install -dd

npm run startwithbackend
```

**Note** npm install with *-dd* option shows status in the terminal. 

**Note** *npm run startwithbackend* will start the Angular development server with live reloading and proxy to BE server.

### GUI Usage
After both BE and FE server started, then open browser and goto http://localhost:4200/. 
Now you should be able to see the GUI application.

You can input your search term in the *search field* and also set how many items you want to see *10, 25 or 50* from BE
response.

**Note** FE handles cases when BE response some error. For e.g when a '*' or '?' is give in search term first. Please see the screenshots.

**Note** FE also handles cases when BE throws IOException. Please see the screenshots.

### GUI Screenshots

#### Success Search

![SuccessSearch](https://user-images.githubusercontent.com/7116963/63022286-d8a23800-bea2-11e9-8d11-eacbdaaab706.png)


   
