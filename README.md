# random-projects
This repository contains different projects for learning purpose.

## Branch info
| branch name  | project info |
| ------------- | ------------- |
| master  | Active mq project with java 9 modules.  |
| spark  | Apache spark - How to setup cluster using docker and submit a job  |

## Pending items in amq project
Add testing with docker. Refer [site](https:/codenotfound.com/jms-publish-subscribe-messaging-example-activemq-maven.html)


## How to run AMQ project
1) Install docker
2) build and run the docker image e.g: `docker build .` `docker run -p 61616:61616 -p 8161:8161 <image name>`
3) Run the producer and consumer main functions.
