#!/bin/sh
./gradlew library:assembleRelease
cp ~/devsoft/projects/android_st/AndroidCoreText/library/build/outputs/aar/library-release.aar /Users/yangzc/devsoft/projects/android/AndroidKnowboxBase/coretext/coretext.aar
