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
- JSON Web Tokens (JWT)

## Application overview

### Endpoints

#### Simple

- `/simple/hello`
- `/simple/heartbeat`

#### JWT

- `/jwt`

    `GET`

    `/`

    Returns a JWT built by the default JWT builder.

    `/{builderId}`

    Returns a JWT built by the JWT builder with the corresponding ID in the server configuration.