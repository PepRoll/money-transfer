# About

Test project RESTful API for money transfers between internal users and accounts

[![Build Status](https://travis-ci.org/PepRoll/money-transfer.svg?branch=master)](https://travis-ci.org/PepRoll/money-transfer)
 
The application using:
* akka-http
* slick
* H2 in memory database

#### Run

`sbt run` - The app start server on localhost port 8080. 
An H2 initialized with several exchange rates.

#### Test

`sbt test` - Start all tests. 

# Available Services

API root URL: [http://localhost:8080/api/](http://localhost:8080/api/)

#### User resource ( [/api/users](http://localhost:8080/api/users) )

| HTTP METHOD        | PATH           |  USAGE |
| ------------- |:-------------|:-----|
| GET     | /users  |   get all users |
| GET     | /users/{userId} | get user by id |
| POST    | /users/    |  create a new user |
| PUT     | /users/{userId} | update user |
| DELETE  | /users/{userId}     | remove user |
| GET     | /users/{userId}/accounts | get all accounts by user id |
| GET     | /users/{userId}/accounts/{accountId} | get account by id with filter by user id |
| GET     | /users/{userId}/accounts/{accountId}/transfers | get all transfers with filer by user and account ids |
| GET     | /users/{userId}/accounts/{accountId}/transfers/{transferId} | get transfer by id with filter by user and account |
| GET     | /users/{userId}/accounts/{accountId}/transfers/{transferId}/rate | get rate by transfer id with filter by user and account  |

#### Account resource ( [/api/accounts](http://localhost:8080/api/accounts) )

| HTTP METHOD        | PATH           |  USAGE |
| ------------- |:-------------|:-----|
| GET     | /accounts | get all accounts |
| GET     | /accounts/{accountId} | get account by id |
| POST    | /accounts/ | create a new account |
| PUT     | /accounts/ | update account |
| DELETE  | /accounts/{accountId} | remove account |
| GET     | /accounts/{accountId}/transfers | get all transfers by account id |
| GET     | /accounts/{accountId}/transfers/{transferId} | get transfer by id with filter by account id |
| GET     | /accounts/{accountId}/transfers/{transferId}/rate | get rate by transfer id with filter by account id  |

#### Transfer resource ( [/api/transfers](http://localhost:8080/api/transfers) )

| HTTP METHOD        | PATH           |  USAGE |
| ------------- |:-------------|:-----|
| GET     | /transfers | get all transfers |
| GET     | /transfers/{transferId} | get transfer by id |
| POST    | /transfers/ | create a new transfer |
| GET     | /transfers/{transferId}/rate | get rate by transfer id |

#### Rate resource ( [/api/rates](http://localhost:8080/api/rates) )

| HTTP METHOD        | PATH           |  USAGE |
| ------------- |:-------------|:-----|
| GET     | /rates | get all rates |
| GET     | /rates/{rateId} | get rate by id |
| POST    | /rates/ | create a new rate |
| PUT     | /rates/ | update rate |
| DELETE  | /rates/{rateId}     | remove rate |

## Sample JSON

Currency values: `RUB | USD | EUR` 

##### User
```json
{
  "firstName": "Harrison",
  "lastName": "Ford"
}
```

##### Account
```json
{
  "currency": "USD",
  "balance": 10000,
  "userId": 1
}
```

##### Transfer
```json
{
  "sourceAccount": 1,
  "targetAccount": 2,
  "amount": 200,
  "exchangeRateId": 2
}
```

##### Rate
```json
{
  "sourceCurrency": "RUB",
  "targetCurrency": "USD",
  "exchangeRate": 30
}

```