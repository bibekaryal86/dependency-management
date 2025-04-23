# migration

* This module is included to migrate data from [app-dependency-update](https://github.com/bibekaryal86/app-dependency-update)
* There are two MongoDb configs for old database and new database
* The data will be retrieved from the old, transformed and saved to new database
* Migration can be run locally only:
  * Run `./gradlew run --args="true"` from main project root
  * Running tha above command will delete any existing data in NEW database and copies from OLD database
