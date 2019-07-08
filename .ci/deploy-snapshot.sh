#!/usr/bin/env bash

# Find project version
ver=$(./gradlew properties -q | grep "version:" | awk '{print $2}')

# deploy if snapshot found
if [[ $ver == *"SNAPSHOT"* ]] 
then
    ./gradlew publish -PgluonNexusUsername=$NEXUS_USERNAME -PgluonNexusPassword=$NEXUS_PASSWORD
fi