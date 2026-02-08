# Nationalbanken Client

Nationalbanken is a Java client for fetching currency rates from Nationalbanken.


The client is meant to be used in Spring Boot applications, and will be auto-configured, if you provide the required configuration properties:

```
nationalbanken:
  url: https://www.nationalbanken.dk
  wiretap: false
```


## Testing

The project includes both unit tests and integration tests.

### Running Unit Tests

By default, only unit tests are executed:

```bash
mvn test
```

Integration tests are excluded from the default build to avoid external API calls during normal builds.

### Running Integration Tests

To run integration tests (which make real HTTP calls to the Nationalbanken API):

Using the integration profile:
```bash
mvn test -Pintegration
```

Or using command line parameters:
```bash
mvn test -DexcludedGroups= -Dgroups=integration
```

Integration tests are tagged with `@Tag("integration")` and can be identified by this annotation.
