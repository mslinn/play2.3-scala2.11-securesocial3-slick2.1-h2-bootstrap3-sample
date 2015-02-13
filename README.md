secure-async
============

*Async all the way down!*

A sample proactive Play application with SecureSocial, ScalikeJdbc-async, Postgres database integrated with Bootstrap 3.

It will be visible at http://secure-async.herokuapp.com

## To run locally ##
1) Set up a Postgres database called `sample`
````
createdb sample
````

2) Define an environment variable that will get picked up by `conf/application.conf`
````
export DATABASE_URL="postgres://postgres:password@localhost:5432/sample"`
````

3) Run the web application
````
activator -jvm-debug 9999 run
````

## Heroku Setup ##

This app uses the free (and slow) [hobby-dev](https://addons.heroku.com/heroku-postgresql)
Postgres [plan](https://devcenter.heroku.com/articles/heroku-postgres-plans#hobby-tier).
This incantation created an empty database and set `DATABASE_URL` so it could be automagically picked up from `conf/application.conf`.

    heroku addons:add heroku-postgresql:hobby-dev
