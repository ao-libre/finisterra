### Clone repo
```
git clone https://github.com/ao-libre/ao-java.git
```
### Run on console
* Use CMD o TERMINAL for run
* Go to folder and use following commands to run client on console ```./gradlew desktop:run``` or server ```./gradlew server:run```
* Or generate distribution jars ``` ./gradlew desktop:dist ``` ``` ./gradlew server:dist ```

### Conflicts running Client and Server in same machine
To avoid recompiling shared module and regeneration of .class files, which will probably bring some execution errors like ClassNotFoundException, you can add this argument to skip `fluid` task in both client and server run configurations:
* ```-x :shared:fluid```
