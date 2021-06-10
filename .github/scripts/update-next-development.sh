#!/usr/bin/env bash

# Assign parameter to variable
TAG=$1

# Configure GIT
git config --global user.name "Gluon Bot"
git config --global user.email "githubbot@gluonhq.com"

# Update version by 1
newVersion=${TAG%.*}.$((${TAG##*.} + 1))

echo "Update README with the latest released version"
sed -i "0,/id 'com.gluonhq.gluonfx-gradle-plugin' version '.*'/s//id 'com.gluonhq.gluonfx-gradle-plugin' version '$TAG'/" README.md
sed -i "0,/'com.gluonhq:gluonfx-gradle-plugin:.*'/s//'com.gluonhq:gluonfx-gradle-plugin:$TAG'/" README.md
git commit README.md -m "Use latest release v$TAG in README"

# Replace first occurrence of 
# version 'TAG' 
# with 
# version 'newVersion-SNAPSHOT'
sed -i -z "0,/\nversion '$TAG'/s//\nversion '$newVersion-SNAPSHOT'/" build.gradle

# Find substrate version
substrateVersion=$(grep com.gluonhq:substrate: build.gradle | tr -d "'" | cut -d \: -f 3)
# Update version by 1
newSubstrateVersion=${substrateVersion%.*}.$((${substrateVersion##*.} + 1))
echo "Update Substrate version"
sed -i -z "0,/com.gluonhq:substrate:$substrateVersion/s//com.gluonhq:substrate:$newSubstrateVersion-SNAPSHOT/" build.gradle

git commit build.gradle -m "Prepare development of $newVersion"
git push https://gluon-bot:$GITHUB_PASSWORD@github.com/$TRAVIS_REPO_SLUG HEAD:master