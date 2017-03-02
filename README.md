# ws_google_tools

## What's that ?

it's a webservice which access to Google Apps and make for me and my team life easier :)

## Install

1- Clone it.
2- add in resource a client_secret_oauth.json.

## Launch
``
mvn clean spring-boot:run
``
clean is optionnal

## As docker

```
mvn package docker:build
```

then tag

docker tag eu.gcr.io/formation-container-test/ws_google_tools eu.gcr.io/formation-container-test/ws_google_tools:0.0.2

then push to google docker repository:

gcloud docker -- push eu.gcr.io/formation-container-test/ws_google_tools:0.0.2

## More data

See articles on my Blog
