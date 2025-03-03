#!/bin/bash

set -e
set -o pipefail

trap 'echo ERROR' ERR

cd "$(dirname "$0")"

#Ensure the project can be built before doing anything else.
./gradlew clean || exit 1
./gradlew core:build || exit 1
./gradlew desktop:dist || exit 1
./gradlew android:assembleRelease || exit 1

git add .
git commit -a -m "Autocommit for next release build." || true

version=$(head -n1 version)
version=$(($version + 1 ))
xversion="${version:0:${#version}-2}.${version: -2}"

echo "BUILD RELEASE: $xversion ($version)"

sed -i "s/version = '.*'/version = '$xversion'/g" build.gradle
sed -i "s/versionCode=\".*\"/versionCode=\"$version\"/g" android/AndroidManifest.xml
sed -i "s/versionName=\".*\"/versionName=\"$xversion\"/g" android/AndroidManifest.xml
sed -i "s/app.version=.*$/app.version=$xversion/g" ios/robovm.properties

if [ -f "gradle.properties" ]; then
	#VERSION=1.75
	sed -i "s/VERSION=.*/VERSION=$xversion/g" gradle.properties
	#VERSION_CODE=175
	sed -i "s/VERSION_CODE=.*/VERSION_CODE=$version/g" gradle.properties
	#VERSION_NAME=1.75
	sed -i "s/VERSION_NAME=.*/VERSION_NAME=$xversion/g" gradle.properties
fi

echo "$version" > version

git add .
git commit -a -m "Bump version for release build." || true
git tag "${xversion}" || true
git push --all
git push --tags

#Build the newly tagged version.
./gradlew clean
./gradlew core:build
./gradlew desktop:dist
./gradlew android:assembleRelease

exit 0
