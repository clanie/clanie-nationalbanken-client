# Nationalbanken Client

Nationalbanken is a Java client for fetching currency rates from Nationalbanken.


The client is meant to be used in Spring Boot applications, and will be auto-configured, if you provide the required configuration properties:

```
nationalbanken:
  url: https://www.nationalbanken.dk
  wiretap: false
```
