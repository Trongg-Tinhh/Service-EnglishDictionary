# Run project on Docker
___
Package the project into .jar file
```shell
./mvnw package
```
Step one: Remove all image and volumes in of the projects 
```shell
docker-compose down --rmi all --volumes
```
Step two: run the docker image
```shell
docker-compose up
```
---

