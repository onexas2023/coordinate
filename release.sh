#!/bin/bash

#set home
BUILD_HOME=$(pwd)

#variable
BRANCH=master

F_PULL=false
F_PREBUILD=false
F_BUILD=false
F_DEPLOY=false
F_HELP=false
F_ERROR=false
F_DOCKER=false
F_DOCKER_PUSH=false
F_OFFICIAL=false
F_DAILY=false


for arg in "$@"
do
case $arg in
	-pull) F_PULL=true;;
	-prebuild) F_PREBUILD=true;;
	-build) F_BUILD=true;;
	-deploy) F_DEPLOY=true;;
	-docker) F_DOCKER=true;;
    -docker-push) F_DOCKER_PUSH=true;;
	-official) F_OFFICIAL=true;;
	-daily) F_DAILY=true;;
	-all) 
		F_PULL=true
		F_PREBUILD=true
		F_DEPLOY=true
		F_BUILD=true
		F_DOCKER=true
		F_DOCKER_PUSH=true
		;;
	-help)
		F_HELP=true
		;;
	*)
		echo '>>>> Unknow Arg ' $arg
		F_HELP=true
		F_ERROR=true
		;;
esac
done

if $F_HELP;then 
	echo -e 'Usage : '
	echo -e '\t-official \t official release, will contains stage in the version'
	echo -e '\t-daily \t daily release, will contains daily-daystamp in the version'
	echo -e '\t-pull \t pull projects from git (will discard local changes)'
	echo -e '\t-prebuild \t prebuild projects (e.g install 3rd lib)'
	echo -e '\t-build \t build projects'
	echo -e '\t-deploy \t deploy projects to rpository'
	echo -e '\t-docker \t build docker image'
	echo -e '\t-docker-push \t push docker image of projects to repository'
	echo -e '\t-all \t pull,prebuild,build,deploy,docker projects'	
fi

if $F_OFFICIAL && $F_DAILY; then
	echo -e "can't set both official and daily"
	F_ERROR=true
fi


if $F_ERROR;then 
	exit 1
fi
if $F_HELP;then 
	exit 0	
fi

echo '========================================================='
#read local brach info
TEMP="branch.local"
if [ -f "$TEMP" ]; then 
	while read line; do
		BRANCH=$line
		echo 'Use local branch : '$BRANCH
		break;
	done < $TEMP
fi


echo '=============== Start to release  ==============='
echo 'Variables : '
echo -e '\tHome Directory : ' $BUILD_HOME
echo -e '\tOfficial : ' $F_OFFICIAL
echo -e '\tDaily : ' $F_DAILY
echo -e '\tBranch : ' $BRANCH

echo -e '\tPull : ' $F_PULL
echo -e '\tPreBuild : ' $F_PREBUILD
echo -e '\tBuild : ' $F_BUILD
echo -e '\tDeploy : ' $F_DEPLOY
echo -e '\tDocker : ' $F_DOCKER
echo -e '\tDockerPush : ' $F_DOCKER_PUSH

#set exit shell when error
set -e

#pull git
if $F_PULL; then
	echo '>>>> Pull '
	echo '>>>> Pull latest branch : '$BRANCH
	git reset --hard HEAD
	git fetch origin	
	git checkout $BRANCH
	git reset --hard origin/$BRANCH
fi

#read version after pull
cd $BUILD_HOME
while read line; do
	VER=$line
	break;
done < version
while read line; do
	VER_STG=$line
	break;
done < version-stage
while read line; do
	VER_DEV=$line
	break;
done < version-dev

if [[ -z $VER ]] || [[ -z $VER_DEV ]]; then
	echo -e '>>>> Can not get version info ($VER,$VER_DEV)'
	exit 1
fi

#yyyyMMdd
VER_DATE=$(date +"%Y%m%d")
#HHmmss
VER_TIME=$(date +"%H%M")

if $F_OFFICIAL;then 
	if [[ -z $VER_STG ]];then 
		VER_FINAL=$VER
	else
		VER_FINAL=$VER'-'$VER_STG
	fi
else
	#daily build version , e.g 2.1.0-snapshot-timestamp
	if $F_DAILY;then
		VER_FINAL=$VER'-'$VER_DATE'-SNAPSHOT'
	else
		VER_FINAL=$VER'-'$VER_DATE$VER_TIME'-SNAPSHOT'
	fi
	
fi
echo -e '>>>> Version : ' $VER_DEV ' to ' $VER_FINAL
#prepare dist folder
NAMEVER_FINAL=coordinate-$VER_FINAL


DISTDIR=$BUILD_HOME'/../dist/'$NAMEVER_FINAL
LASTDIR=$BUILD_HOME'/../dist/last/coordinate'

mkdir -p $DISTDIR
cd "${DISTDIR}"; DISTDIR=`pwd`
mkdir -p $LASTDIR
cd "${LASTDIR}"; LASTDIR=`pwd`

echo -e '>>>> Dist dir : ' $DISTDIR
echo -e '>>>> Last dir : ' $LASTDIR

cd $BUILD_HOME
#prevuild
if $F_PREBUILD; then
	echo '>>>> Prebuild '
	echo '>>>> Install/Re-install 3rd lib : '
	# cd $BUILD_HOME/coordinate/coordinate-root/3rdlib
	# ./install-3rdlib.sh	
fi

#build
if $F_BUILD; then
	echo '>>>> Start to build : '

	#search replace version
	echo '>>>> Replace version : '$VER_DEV' to '$VER_FINAL
	cd $BUILD_HOME
	./replaceVer.sh $BUILD_HOME $VER_DEV $VER_FINAL

	#build
	cd $BUILD_HOME/coordinate-root
	./mvnw clean

	#hast to build with test for coordinate-api-sdk's swagger
	./mvnw install

	#pack site
	./mvnw site site:stage -DskipTests=true

	cd $BUILD_HOME/coordinate-root/target/staging
	echo '>>>> Make Report site.zip at '$DISTDIR
	zip -r -q -D $DISTDIR/site.zip .
	
	cp $DISTDIR/site.zip $LASTDIR/site.zip

	#copy coordinate-app
	TEMP=$DISTDIR/coordinate-app/config
	echo '>>>> Copy coordinate config to '$TEMP
	mkdir -p $TEMP
	cp $BUILD_HOME/coordinate-app/config/* $TEMP

	TEMP=$DISTDIR/coordinate-app/schema
	echo '>>>> Copy coordinate schema to '$TEMP
	mkdir -p $TEMP
	cp $BUILD_HOME/coordinate-app/target/*.sql $TEMP	

	TEMP=$DISTDIR/coordinate-app
	echo '>>>> Copy coordinate distribution to '$TEMP
	cp $BUILD_HOME/coordinate-app/target/coordinate-app-$VER_FINAL.jar $TEMP/.
	cp $BUILD_HOME/coordinate-api-sdk/target/coordinate-api-sdk-$VER_FINAL.jar $TEMP/.
	cp $BUILD_HOME/coordinate-api-sdk-typescript/target/coordinate-api-sdk-typescript-$VER_FINAL-typescript.zip $TEMP/.

	#copy axes-app
	TEMP=$DISTDIR/axes-app/config
	echo '>>>> Copy axes config to '$TEMP
	mkdir -p $TEMP
	cp $BUILD_HOME/axes-app/config/* $TEMP

	TEMP=$DISTDIR/axes-app
	echo '>>>> Copy axes distribution to '$TEMP
	cp $BUILD_HOME/axes-app/target/axes-app-$VER_FINAL.jar $TEMP/.

	#copy docker config
	TEMP=$DISTDIR/docker
	echo '>>>> Copy docker config to '$TEMP
	mkdir -p $TEMP
	cp $BUILD_HOME/misc/docker/coordinate.allinone.compose.yaml $TEMP/.

	#copy resources
	TEMP=$DISTDIR/font
	echo '>>>> Copy font to '$TEMP
	mkdir -p $TEMP
	cp $BUILD_HOME/misc/font/* $TEMP/

	#copy distribution to last
	echo '>>>> Copy distribution to '$LASTDIR
	cp -r $DISTDIR/. $LASTDIR/.	


	#sdk-typescript npm build
	echo '>>>> Build api sdk '
	cd $BUILD_HOME/coordinate-api-sdk-typescript/generated-sources
	npm install
	npm run build
fi

#deploy
if $F_DEPLOY; then
	echo '>>>> Start to deploy : '
	cd $BUILD_HOME/coordinate-root
	
	./mvnw deploy -DskipTests=true

	#sdk-typescript npm publish
	echo '>>>> Publish api sdk '
	cd $BUILD_HOME/coordinate-api-sdk-typescript/generated-sources

	if $F_DAILY; then
		echo '>>>>>> Append snapshot time for npm version T'$VER_TIME
		npm-snapshot 'T'$VER_TIME
	fi

	#Use local setting
	#The runtime should has token setting by 
	#npm config set -- //nexus.mshome.net:8081/repository/npm-releases/:_auth base64(YOUR_NAME:PASSWORD)
	echo '@onexas:registry=http://nexus.mshome.net:8081/repository/npm-releases/' > .npmrc
	npm publish
fi

#docker
if $F_DOCKER; then

	echo '>>>> Start to build docker image : '
	cd $BUILD_HOME/coordinate-app
	docker build . -t nexus.mshome.net:8082/repository/docker-releases/coordinate:latest

	cd $BUILD_HOME/axes-app
	docker build . -t nexus.mshome.net:8082/repository/docker-releases/axes:latest

	if $F_OFFICIAL;then 
		VER_DOCKER=$VER_FINAL

		docker tag nexus.mshome.net:8082/repository/docker-releases/coordinate:latest nexus.mshome.net:8082/repository/docker-releases/coordinate:$VER_DOCKER
		docker tag nexus.mshome.net:8082/repository/docker-releases/axes:latest nexus.mshome.net:8082/repository/docker-releases/axes:$VER_DOCKER

	fi

fi

#docker
if $F_DOCKER_PUSH; then

	#The runtime should has token setting by 
	#docker login -u YOUR_DEPLOY_TOKEN_USERNAME -p YOUR_DEPLOY_TOKEN nexus.mshome.net:8082
	#you also need to add "insecure-registries" : ["nexus.mshome.net:8082"] to /etc/docker/daemon.json to avoid server gave HTTP response to HTTPS client

	echo '>>>> Start to push docker image : '
	docker push nexus.mshome.net:8082/repository/docker-releases/coordinate:latest
	docker push nexus.mshome.net:8082/repository/docker-releases/axes:latest

	if $F_OFFICIAL;then 
		VER_DOCKER=$VER_FINAL

		docker push nexus.mshome.net:8082/repository/docker-releases/coordinate:$VER_DOCKER
		docker push nexus.mshome.net:8082/repository/docker-releases/axes:$VER_DOCKER
	fi

fi

echo '=============== Release Finished ==============='
