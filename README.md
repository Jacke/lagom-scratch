# Scala micro service: calendar with events for machines, resources



# Endpoints

```

GET /api/asset/:id
GET /api/assets


POST /api/asset
PUT /api/asset/:id
DELETE /api/asset/:id


GET /api/asset/:id/entries
curl -X GET \
  http://localhost:9000/api/asset/1/entries \
  -H 'cache-control: no-cache' \
  -H 'postman-token: 507e8e6e-4e8f-2532-02b3-780fa9b8c89a'

POST /api/asset/:id/entry

curl -X POST \
  http://localhost:9000/api/asset/1/entry \
  -H 'cache-control: no-cache' \
  -H 'content-type: application/json' \
  -H 'postman-token: 4fd2f2d5-a8f2-646c-4406-aaf6c485e653' \
  -d '{"asset_id": 1, 
"name": "String", 
"startDateUtc": "2017-11-20T09:25:43.511Z", 
"endDateUtc": "2017-11-20T18:25:43.511Z", 
"duration": 0,
"isAllDay":false,
"isRecuring":false,
"recurrencePattern": ""
}'

PUT /api/asset/entry/:id

curl -X PUT \
  http://localhost:9000/api/asset/1/entry \
  -H 'cache-control: no-cache' \
  -H 'content-type: application/json' \
  -H 'postman-token: 4fd2f2d5-a8f2-646c-4406-aaf6c485e653' \
  -d '{"asset_id": 1, 
"name": "String", 
"startDateUtc": "2017-11-20T09:25:43.511Z", 
"endDateUtc": "2017-11-21T18:25:43.511Z", 
"duration": 0,
"isAllDay":false,
"isRecuring":false,
"recurrencePattern": ""
}'

DELETE /api/asset/entry/:id


POST /api/entry/exception
curl -X POST \
  http://localhost:9000/api/entry/exception \
  -H 'cache-control: no-cache' \
  -H 'content-type: application/json' \
  -H 'postman-token: 02374723-6cb3-4a29-6e09-c62540328795' \
  -d '{"entry_id": 2, "startDateUtc": "2017-11-22T12:26:43.511+03:00", "endDateUtc": "2017-11-20T13:25:43.511+03:00"}
'

GET /api/asset/:id/availabilities

```