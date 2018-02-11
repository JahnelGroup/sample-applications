#!/bin/bash

ES_URI=http://localhost:9200

curl -X "DELETE" $ES_URI/auctions
curl -X "DELETE" $ES_URI/users
