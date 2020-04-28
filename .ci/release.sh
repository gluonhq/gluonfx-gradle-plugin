#!/usr/bin/env bash

# Configure GIT
git config --global user.name "Gluon Bot"
git config --global user.email "githubbot@gluonhq.com"

# Release artifacts
./gradlew publish -PgluonNexusUsername=$NEXUS_USERNAME -PgluonNexusPassword=$NEXUS_PASSWORD -PrepositoryUrl=https://nexus.gluonhq.com/nexus/content/repositories/releases

# Update version by 1
newVersion=${TRAVIS_TAG%.*}.$((${TRAVIS_TAG##*.} + 1))

# Update README with the latest released version
sed -i "0,/id 'com.gluonhq.client-gradle-plugin' version '.*'/s//id 'com.gluonhq.client-gradle-plugin' version '$TRAVIS_TAG'/" README.md
sed -i "0,/'com.gluonhq:client-gradle-plugin:.*'/s//'com.gluonhq:client-gradle-plugin:$TRAVIS_TAG'/" README.md
git commit README.md -m "Use latest release v$TRAVIS_TAG in README"

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

git commit build.gradle -m "Prepare development of $newVersion"
git push https://gluon-bot:$GITHUB_PASSWORD@github.com/$TRAVIS_REPO_SLUG HEAD:master