# Reactive application

## components

* Mongo
* Webflux

## test

* curl -v http://localhost:8080/movies 
* curl -v http://localhost:8080/movies/60a950abe9119d4a75fa7d69

* Streaming events

```shell script
curl -v http://localhost:8080/movies/60a955ad14bbaa6f3a7b3e50/events 
```