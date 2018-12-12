# javaMetaprogramming

This project allows the validation of an XSD Schema against it's generated Java-POJOs. The way this prorgram works is as follows:

1.  Parse the XSD-File using DOM and store the structure in a Hashmap
2.  Parse Java-POJOs using JavaParser and store the structure in a Hashmap
3.  Compare both resulting Hashmaps


# Setup

## Prerequisites
* Java SDK 8+ (with Java SDK binaries in the PATH or JAVA_HOME set up)
* Gradle

## How to Build

* Go to the javaMetaprogramming folder
```
$ cd javaMetaprogramming
```
* If Gradle isn't installed, run the following command
```
$ ./gradlew
```
* Command for building the project:
```
$ ./gradlew build
```
* Command for preparing the project for Eclipse
```
$ ./gradlew eclipse
```


# How to setup your own tests

* Put your .XSD-File(s) into the inputs Folder
```
$ /javaMetaprogramming/inputs/
```
* Modify or copy a gradle task in the build.gradle file
```gradle
task xjc(type: Exec) {
    commandLine 'xjc'
    args = ['inputs/Company.xsd', '-d', 'src/main/java', '-p' , 'org.softlang.metaprogramming.dm.company']
    doFirst {
        mkdir 'src/main/java/org/softlang/metaprogramming/dm/company'
    }
}
```

* Build the project again

```
$ ./gradlew build

```
* Test your Schema by supplying the path to your Java data model
```java
String yourTest ="src/main/java/org/softlang/metaprogramming/dm/yourDataModel/";
```
* Parse the data model into a Hashmap

```java
Map<String, HashMap<String, String>> yourTestMap = 
					JCParser.parseJC(JCParser.initJC(yourTest));
```

* Provide the path to your XSD Schema File

```java
Node yourXSD = XSDParser.initXSD("inputs/yourSchema.xsd");
```

* Parse your Schema into a Hashmap

```java
Map<String, Map<String, String>> yourTestMapX =XSDParser.parseXSD(yourXSD);
```


* Compare

```java
assertTrue(yourTestMap.equals(yourTestMapX));
```



