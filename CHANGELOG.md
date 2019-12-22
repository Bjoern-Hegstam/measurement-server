### Unreleased
* Refactor domain model
  * Remove source as terminology and only use sensor to denote something that produces measurements.
  * Introduce instrumentations
    * Groups collections of sensors
    * Gives control over whether measurements from a sensor should be recorded or not.
    * Allows sensors to be reused over time
* Change build process
  * Add support for building docker image
  * Remove support for systemctl

### 3.3
* Upgrade dependencies
* Can run server as systemctl service
* Migrated to dropwizard

### 3.2
* gzip all responses
* Update mockito version
* Add README
* Save redux store to localStorage

### 3.1
* Refactor project structure (keep front end and back end code separate)
* Bugfix: JUnit 5 tests were not run by maven surefire

### 3.0
* Rebuild front end to use react

### 2.0.1
* Bugfix: Infinite loop when getting measurements

### 2.0
* Rebuild front end (npm + webpack)

### 1.4
* Java 9
* Junit 5
