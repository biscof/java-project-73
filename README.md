# Task Manager


[![Actions Status](https://github.com/biscof/java-project-73/workflows/hexlet-check/badge.svg)](https://github.com/biscof/java-project-73/actions)
[![build-and-test](https://github.com/biscof/java-project-73/actions/workflows/build-and-test.yml/badge.svg)](https://github.com/biscof/java-project-73/actions/workflows/build-and-test.yml)
[![Maintainability](https://api.codeclimate.com/v1/badges/3229c3b950c4c63bfdbb/maintainability)](https://codeclimate.com/github/biscof/java-project-73/maintainability)
[![Test Coverage](https://api.codeclimate.com/v1/badges/3229c3b950c4c63bfdbb/test_coverage)](https://codeclimate.com/github/biscof/java-project-73/test_coverage)


## Overview

Task Manager is a web-based task management application which allows its users to create, organize, and monitor tasks, as well as assign them to team members for efficient collaboration. It has a straightforward and intuitive interface. 

You will find the app's demo [here](https://task-manager-app-km58.onrender.com).


## Features

The app offers the following features:

- adding new tasks
- creating custom task labels
- tracking task status
- assigning tasks to other users
- leaving task-related comments


## Technologies and Dependencies

- Java 20
- Spring Boot 3.1
- Spring Security 6.1
- Spring Data 3.1
- PostgreSQL 42.6
- Liquibase 4.2
- Hibernate 6
- JUnit 5.9
- Mockito 5.3


## Getting Started

### Prerequisites

To run this app on your machine, you'll need:

- JDK 20
- Docker and Docker Compose

### Usage and development

1. Clone this repository to your local machine:

```bash
git clone https://github.com/biscof/task-manager.git
```

2. Navigate to the project directory:

```bash
cd task-manager
```

3. Build and run the app locally with a containerized PostgreSQL database (ensure Docker is started beforehand):

```bash
make run-dev-with-docker-db
```

- To build the final JAR, run the following command:

```bash
make build
```

### Deployment

To run this app in a production environment use this command:

```bash
make run-prod
```

Please note that you will also need to set values for these environment variables to establish connection to your database: `JDBC_DATABASE_URL`, `JDBC_DATABASE_USERNAME`, and `JDBC_DATABASE_PASSWORD`.


## Testing

This command will launch the tests:

```bash
make test
```


## API Documentation

Find the detailed API documentation [here](https://task-manager-app-km58.onrender.com/swagger.html).


##

Frontend by [Hexlet](https://hexlet.io)
