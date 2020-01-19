Event-service
======================

## This Microservice created for store events from other microservice.
At the moment microservice can:
- Store a new event
- Return the event list of a specific user

## How to run it?
### The easiest way is to clone the project
Open the project with your preferable IDE, and configure the project as a maven project. Your IDE will autoconfigure the project for you, so press the play.
### To run it from your command line:
The requirements are maven and java 11.
```bash
cd assignmentFidectus
mvn clean install
java -jar event-log.jar
```

### To run it with docker:
The requirement is docker and docker-compose.
```bash
cd assignmentFidectus
docker-compose up
```

## Now that your microservice is up and running, open your browser and use the link above:
http://localhost:8081/swagger-ui.html
http://localhost:8081/h2-console

The first is to test the request, and the second is to check your DB.

### Save Event Log:
To save an EventLog you need to send a post request. The body should be like:
```json
{
	"type":"REGISTRATION"
	"userId" : 5
}
```
Event types are : [REGISTRATION, UPDATE_REGISTRATION, DEACTIVATE, DELETED].

The response should look like:
```json
{
  "type": "REGISTRATION",
  "userId": 1,
  "createdDate": "2020-01-19T20:44:44.796948",
  "hash": "111c0d7c-4ee8-34a4-9ba3-5bc558f66b0a"
}
```
If your request body is not valid, or the event you are trying to save don't follow the state diagrame bellow your response body should look like:
```json
{
  "status": "BAD_REQUEST",
  "message": "Cannot Register an already registered user.",
  "errors": []
}
```
![alt text](https://github.com/Tsekourakos/assignmentFidectus/blob/master/src/EventTransition.jpg)
### Get events of a specific user:
http://localhost:8081/event/user/3
To get the events, you should send a request at the URL above, adding the UserId at the end.
If the user does not have any events, the API returns Bad Request with response body look like:
```json
{
  "status": "BAD_REQUEST",
  "message": "User with id '3' does not exists.",
  "errors": []
}
```
if the user has events history the response is ordered by id and will look like the bellow:
```json
[
  {
    "type": "DELETED",
    "userId": 3,
    "createdDate": "2020-01-19T21:04:20.823782",
    "hash": "562e152a-c432-3db5-9865-bc9ee35333b4"
  },
  {
    "type": "UPDATE_REGISTRATION",
    "userId": 3,
    "createdDate": "2020-01-19T21:04:03.40709",
    "hash": "cac71823-465b-393b-839b-51b9954ea8d2"
  },
  {
    "type": "REGISTRATION",
    "userId": 3,
    "createdDate": "2020-01-19T21:03:54.981871",
    "hash": "dc56a8ba-1b04-3655-ab7f-97e84ab837b9"
  }
]
```

### To Ensure that the Event-Service is fully operational, run the tests.

