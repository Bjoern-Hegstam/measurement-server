# measurement-server
A simple server for storing and retrieving generic measurements. Serves a simple index page with a graph of the most recent measurements for each source.

## Release
1. `export APP_VERSION=<APP_VERSION>`
2. `git branch release-$APP_VERSION`
3. `sed -i s/1.0-SNAPSHOT/$APP_VERSION/g pom.xml`
4. Update CHANGELOG
5. `git commit -m $APP_VERSION`
6. `git tag -a $APP_VERSION`
7. Copy CHANGELOG entry to tag description
8. `git push --follow-tags`

## Build
1. `export APP_VERSION=<APP_VERSION>`
2. `git checkout $APP_VERSION`
3. `mvn clean package`
4. `cp target/measurement-server-$APP_VERSION.jar docker`
5. `cd docker`
6. `docker build -t measurement-server:latest -t measurement-server:$APP_VERSION --build-arg $APP_VERSION`

## Keeping project up to date
1. `mvn versions:use-latest-releases -DgenerateBackupPoms=false`
2. `mvn versions:update-properties -DgenerateBackupPoms=false`
3. `mvn clean site` and check updates reports for any remaining updates
4. If all tests pass, commit and push
