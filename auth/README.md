To build a new image:

mvn clean
mvn package

docker build --tag=iamfurukawa/oauth2-server:13 .
docker run --name oauth2-server -p6660:6660 iamfurukawa/oauth2-server:13

docker push iamfurukawa/oauth2-server:13