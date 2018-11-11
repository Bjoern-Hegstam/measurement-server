# measurement-server
A simple server for storing and retrieving generic measurements. Serves a simple index page with a graph of the most recent measurements for each source.

## Install/upgrade
1. Checkout tag for wanted release
2. `mvn clean package`
3. Copy distribution zip to server
4. `unzip <distribution>`
5. `cd <distribution>`
6. `chmod 777 install.sh`
7. `dos2unix install.sh`
8. `sudo ./install.sh`
9. To check status of service `systemctl status measurement-server`

## Keeping project up to date
1. `mvn versions:use-latest-releases -DgenerateBackupPoms=false`
2. `mvn versions:update-properties -DgenerateBackupPoms=false`
3. `mvn clean site` and check updates reports for any remaining updates
4. If all tests pass, commit and push
 