# Welcome to the Sample Application!

This is meant to be a bit of a catch-all for developing and exploring the various useful bits of functionality that make up a web application.

## Table of Contents

- [Run it now](#run-it-now)
- [Technologies](#technologies)
- [Application overview](#application-overview)

## Run it now

1. Clone this repository
1. In the project root, run
    1. `$ mvn package`
    1. `$ mvn liberty:dev`
1. Interact with the app
1. Type `Ctrl + C` to stop the server.

## Technologies

- JAX-RS
- Application security
- JPA

## Application overview

### Endpoints

#### Simple

- `/simple/hello`
- `/simple/heartbeat`

#### App security

- `/protected/heartbeat`

    Does not require any credentials or authentication.

    **Output:**
    
    Current time in milliseconds.

- `/protected/no-roles`

    Does not have a `@RolesAllowed` annotation, so this will always return a 403 regardless of authentication data provided.

- `/protected/echo`

    `@RolesAllowed("Echoer")`
    
    **Parameters:**

    - `input`: String used as a prefix in the text output.

    **Output:**

    The value of the `input` parameter plus the name of the authenticated user.

- `/protected/dump`

    `@RolesAllowed("Echoer")`

    **Output:**

    Dump of a bunch of information about the request and authenticated user:
    
    - URI info
    - Request headers
    - Security context data
    - Cookies
#### JPA

- `/notes`

    `POST`

    Creates a new Note in the database.

    **Parameters:**

    - `text`: Text of the Note to create.

    `GET`

    `/`

    Retrieves all Notes from the database.

    `/{id}`

    Retrieves the Note with the corresponding ID from the database.

    `PUT`

    `/{id}`

    Updates an existing Note.

    **Parameters:**

    - `text`: New text of the Note.

    `DELETE`

    `/{id}`

    Deletes the Note with the corresponding ID from the database.
