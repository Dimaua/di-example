#!/usr/bin/env bash

##############################################################################
##
##  Jenkins script for UN*X
##
##############################################################################

#Variables for local build test
#BUILD_URL="http://localhost"
#BUILD_ID="2016-02-11_14-07-21"
#BUILD_NUMBER="1"
#GIT_BRANCH="develop"

typeset err_code
echo "Build Url: $BUILD_URL"
echo "Build Id: $BUILD_ID"
echo "Build Number: $BUILD_NUMBER"
echo "Git Branch: $GIT_BRANCH"

git submodule update --init --recursive

XML_LOG=$(curl "${BUILD_URL}api/xml?wrapper=changes&xpath=//changeSet//comment")
CHANGE_LOG=$(echo "$XML_LOG" | sed -e "s/<\/comment>//g; s/<comment>//g; s/<\/*changes>//g")
echo 'Recent Changes: '
echo "$CHANGE_LOG"

last_commit=`git log --pretty=format:'%h' -n 1`
branch="$(echo $GIT_BRANCH | sed 's .\{7\}  ')"

chmod +x ./gradlew
./gradlew clean assemble -PbuildNumber=${BUILD_NUMBER} -PchangeLog="${CHANGE_LOG}" crashlyticsUploadDistributionProductionRelease
err_code=$?

rm -rf build_results/
mkdir build_results


ROOT_DIR=`pwd`
cd app/build/outputs/apk/

for flavor in *; do
    cd $flavor
    for build_type in *; do
        cd $build_type
        for f in *.apk; do
            echo $f
            output="${ROOT_DIR}/build_results/valeo_loyalty_${BUILD_NUMBER}_${flavor}_${build_type}_${branch}_${last_commit}.apk"
            cp "$f" "$output"
        done
        cd ..
    done
    cd ..
done

cd "${ROOT_DIR}/build_results"

# Copy files to CustomShare
mkdir -p /mnt/customshare/Products/Valeo-loyalty/Android/#${BUILD_NUMBER}
cp ./*.apk /mnt/customshare/Products/Valeo-loyalty/Android/#${BUILD_NUMBER}

# Print result values and links to file
commit_hash=`git log --pretty=format:'%H' -n 1`
echo ' '
echo ' '
echo '-----------------------------------'
echo ' ' | tee -a build-results.txt
echo ' ' | tee -a build-results.txt
echo 'Output Files: ' | tee -a build-results.txt
echo ' ' | tee -a build-results.txt
for f in *.apk; do
    echo "/mnt/customshare/Products/Valeo-loyalty/Android/#${BUILD_NUMBER}/${f}"  | tee -a build-results.txt
    echo ' ' | tee -a build-results.txt
done
echo ' ' | tee -a build-results.txt
echo ' ' | tee -a build-results.txt
echo ' ' | tee -a build-results.txt
echo "Gitlab link: http://gitlab.kharkov.dbbest.com/android/valeo-loyalty-app/commit/${commit_hash}" | tee -a build-results.txt
echo ' ' | tee -a build-results.txt
echo ' '
echo '-----------------------------------'
echo ' '
echo ' '

exit $err_code