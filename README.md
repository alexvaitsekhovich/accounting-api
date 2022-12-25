# accounting-api


[![pipeline statu](https://gitlab.com/alex.vaitsekhovich/accounting-api/badges/main/pipeline.svg)](https://gitlab.com/alex.vaitsekhovich/accounting-api/pipelines) [![Build Status](https://travis-ci.org/alexvaitsekhovich/accounting-api.svg?branch=main)](https://travis-ci.org/alexvaitsekhovich/accounting-api)

[![codecov](https://codecov.io/gh/alexvaitsekhovich/accounting-api/branch/main/graph/badge.svg)](https://codecov.io/gh/alexvaitsekhovich/accounting-api) [![Codacy Badge](https://app.codacy.com/project/badge/Grade/0b0d328306c94036bbb320910b2f5cf9)](https://www.codacy.com/gh/alexvaitsekhovich/accounting-api/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=alexvaitsekhovich/accounting-api&amp;utm_campaign=Badge_Grade) [![Maintainability](https://api.codeclimate.com/v1/badges/932b96f3407d5d9f2ad1/maintainability)](https://codeclimate.com/github/alexvaitsekhovich/accounting-api/maintainability)

Swagger UI:<br>
{{url}}/swagger-ui.html

To get the JWT token, post request to:<br>
{{url}}/gettoken<br>
with json, containing parameters "email" and "password"

The only open endpoint is POST request to create a user. The created user can then request token with defined password and access its own data, his accounting positions and invoices.<br>

Admin can access user management with following credentials:<br>
Username: users-admin@api.com<br>
Password: admin-pass

<hr/>
All endpoints:
<br>

<img src="https://github.com/alexvaitsekhovich/images/blob/main/accounting_api.png" width="800px" height="520px" alt="Accounting API endpoints">
