#!/usr/bin/env bash

# Release artifacts
./gradlew publish -PgluonNexusUsername=$NEXUS_USERNAME -PgluonNexusPassword=$NEXUS_PASSWORD -PrepositoryUrl=https://nexus.gluonhq.com/nexus/content/repositories/releases

# Update version by 1
newVersion=${TRAVIS_TAG%.*}.$((${TRAVIS_TAG##*.} + 1))

# Replace first occurrence of 
# version 'TRAVIS_TAG' 
# with 
# version 'newVersion-SNAPSHOT'
sed -i -z "0,/\nversion '$TRAVIS_TAG'/s//\nversion '$newVersion-SNAPSHOT'/" build.gradle

# Find substrate version
substrateVersion=$(grep com.gluonhq:substrate: build.gradle | tr -d "'" | cut -d \: -f 3)
# Update version by 1
newSubstrateVersion=${substrateVersion%.*}.$((${substrateVersion##*.} + 1))
# Update Substrate version
sed -i -z "0,/com.gluonhq:substrate:$substrateVersion/s//com.gluonhq:substrate:$newSubstrateVersion-SNAPSHOT/" build.gradle

git commit build.gradle -m "Upgrade version to $newVersion-SNAPSHOT" --author "Github Bot <githubbot@gluonhq.com>"
git push https://gluon-bot:$GITHUB_PASSWORD@github.com/gluonhq/client-gradle-plugin HEAD:master