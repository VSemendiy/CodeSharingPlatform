# CodeSharingPlatform    

Two types of interfaces are implemented: API and web interface. The API is accessed through endpoints that start with / api, while the endpoints of the web interface start with /. The API interface returns data as JSON, while the web interface uses HTML.    

Two limitations on the code snippet's visibility are added:    
- A limit on the number of views allows viewing the snippet only a certain number of times, after which the snippet is deleted from the database.    
- A limit on the viewing time allows viewing a code snippet for a certain period of time, and after its expiration, the code snippet is deleted from the database.    

## Requests    

- `POST /api/code/new` adds a snippet from its body and returns a UUID of the snippet.
Request takes a JSON object with a field code and two other fields: `time` (field contains the time (in seconds) during which the snippet is accessible) and `views` (field contains a number of views allowed for this snippet).
0 and negative values corresponds to the absence of the restriction.

- `GET /code/new` return an html-form containing three elements: `time`, `views` and `textarea` for code snippets.    

- `GET /api/code/latest` and `GET /code/latest` return 10 latest snippets without any restrictions.    

- `GET /api/code/UUID` and `GET /code/UUID` return the code snippet with the specified UUID and information about `time` and `views` restrictions.    

- `GET /api/code` and `GET /code` return the latest code snippet.

## Examples

 - Request `POST /api/code/new` with the following body:   
```
{
    "code": "class Code { ...",
    "time": 0,
    "views": 0
}
```
Response: `{ "id" : "2187c46e-03ba-4b3a-828b-963466ea348c" }`.    

- Request: `GET /api/code/2187c46e-03ba-4b3a-828b-963466ea348c`    
Response:
```
{
    "code": "Secret code",
    "date": "2020/05/05 12:01:45",
    "time": 4995,
    "views": 4
}
```
Another request with the same UUID `GET /api/code/2187c46e-03ba-4b3a-828b-963466ea348c`    
Response:
```
{
    "code": "Secret code",
    "date": "2020/05/05 12:01:45",
    "time": 4991,
    "views": 3
}
```

- Request: `GET /api/code/latest`    
Response:
```
[
    {
        "code": "public static void ...",
        "date": "2020/05/05 12:00:43",
        "time": 0,
        "views": 0
    },
    {
        "code": "class Code { ...",
        "date": "2020/05/05 11:59:12",
        "time": 0,
        "views": 0
    }
]
```
