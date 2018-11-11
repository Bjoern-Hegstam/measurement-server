A simple server for storing and retrieving generic measurements. Serves a simple index page with a graph of the most recent measurements for each source.

### Updating
1. Run `mvn versions:use-latest-releases -DgenerateBackupPoms=false`
2. Run `mvn versions:update-properties -DgenerateBackupPoms=false`
3. Run `mvn clean site` and check updates reports for any remaining updates
4. If all tests pass, commit and push 