#!/usr/bin/env bash

mvn jgitflow:release-start -DreleaseVersion=上线后的正式版本号 -DdevelopmentVersion=下一次使用的版本号 -DpushReleases=false -DallowSnapshots=true

mvn jgitflow:release-start -B -DreleaseVersion=1.0.0 -DdevelopmentVersion=1.0.1-SNAPSHOT -DpushReleases=false -DallowSnapshots=true


mvn jgitflow:release-finish -DnoReleaseBuild=true -DnoDeploy=true -DpushReleases=true

mvn jgitflow:release-finish -DnoReleaseBuild=true -DnoDeploy=true -DpushReleases=false