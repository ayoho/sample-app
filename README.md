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
- JPA

## Application overview

### Endpoints

#### Simple

- `/simple/hello`
- `/simple/heartbeat`

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
