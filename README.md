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
