This repository along with the repository present [here](https://github.com/nictbn/spring-boot-kafka-tracking-microservice) represent an implementation of the Udemy course: [Introduction to Kafka with Spring Boot
](https://www.udemy.com/course/introduction-to-kafka-with-spring-boot/)

### Prerequisites
Java 17, as the application uses Spring Boot 3\
The Docker Engine and Docker Compose for running Kafka in a container (alternatively, you can run kafka directly on your machine)\
The Kafka CLI Tools for producing messages

### Steps to run the applications
1. Clone this repository
2. Clone [this](https://github.com/nictbn/spring-boot-kafka-tracking-microservice) repository as well
3. Navigate to the docker folder in the present repository and run ```docker compose up``` or ```docker-compose up```
4. Start this application
5. start the kafka tracking microservice application
6. Run ```kafka-console-producer.sh --bootstrap-server localhost:9092 --topic order.created --property parse.key=true --property key.separator=:```
7. Send an event having this structure: ```"123":{"orderId":"2ceef9f5-256b-42b5-af49-6828a8892a06","item":"item-10"}```
8. Both microservices should process the event sent previously

