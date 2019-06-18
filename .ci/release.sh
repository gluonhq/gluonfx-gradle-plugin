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

git commit build.gradle -m "Upgrade version to $newVersion-SNAPSHOT" --author "Github Bot <githubbot@gluonhq.com>"
git push https://gluon-bot:$GITHUB_PASSWORD@github.com/gluonhq/client-gradle-plugin