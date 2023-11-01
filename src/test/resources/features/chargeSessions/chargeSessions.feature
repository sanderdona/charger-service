Feature: Charge sessions

  Background:
    Given a user with username "read-user"
    And a car with the name "FooEV" with the odometer reading 43689 km

  Scenario: An anonymous session is started
    When I send a message on topic "home/charger/sdm1-1/Import" with payload "86.495"
    And I send a message on topic "home/charger/sdm1-1/Power" with payload "0.956"
    Then the service publishes a message on topic "home/charger-service" with payload:
      """
      {
        "id": "${json-unit.ignore-element}",
        "type": "anonymous",
        "startedAt": "${json-unit.ignore-element}",
        "startkWh": 86.495
      }
      """
    And a request on "/charges" returns status 200 with body:
      """
      {
        "content": [
          {
            "id": "${json-unit.ignore-element}",
            "type": "anonymous",
            "startedAt": "${json-unit.ignore-element}",
            "startkWh": 86.495
          }
        ]
      }
      """

  Scenario: A session is started and registered to a car
    When I send a message on topic "home/charger/sdm1-1/Import" with payload "86.495"
    And I send a message on topic "home/charger/sdm1-1/Power" with payload "0.956"
    Then the service publishes a message on topic "home/charger-service" with payload:
      """
      {
        "id": "${json-unit.ignore-element}",
        "type": "anonymous",
        "startedAt": "${json-unit.ignore-element}",
        "startkWh": 86.495
      }
      """
    When the car is charging at home
    Then the service publishes a message on topic "home/charger-service" with payload:
      """
      {
        "id": "${json-unit.ignore-element}",
        "car": {
          "id": "${json-unit.ignore-element}",
          "name": "FooEV"
        },
        "odoMeter": 43689,
        "type": "registered",
        "startedAt": "${json-unit.ignore-element}",
        "startkWh": 86.495
      }
      """
    And a request on "/charges" returns status 200 with body:
      """
      {
        "content": [
          {
            "id": "${json-unit.ignore-element}",
            "car": {
              "id": "${json-unit.ignore-element}",
              "name": "FooEV"
            },
            "odoMeter": 43689,
            "type": "registered",
            "startedAt": "${json-unit.ignore-element}",
            "startkWh": 86.495
          }
        ]
      }
      """

  Scenario: An anonymous session is ended
    When I send a message on topic "home/charger/sdm1-1/Import" with payload "86.495"
    And I send a message on topic "home/charger/sdm1-1/Power" with payload "0.956"
    And I let the charge session run for 5 seconds
    And I send a message on topic "home/charger/sdm1-1/Import" with payload "102.739"
    And I send a message on topic "home/charger/sdm1-1/Power" with payload "0.000"
    Then the service publishes a message on topic "home/charger-service" with payload:
      """
      {
        "id": "${json-unit.ignore-element}",
        "type": "anonymous",
        "startedAt": "${json-unit.ignore-element}",
        "startkWh": 86.495,
        "endedAt": "${json-unit.ignore-element}",
        "endkWh": 102.739,
        "totalkwH": 16.244
      }
      """
    And a request on "/charges" returns status 200 with body:
      """
      {
        "content": [
          {
            "id": "${json-unit.ignore-element}",
            "type": "anonymous",
            "startedAt": "${json-unit.ignore-element}",
            "startkWh": 86.495,
            "endedAt": "${json-unit.ignore-element}",
            "endkWh": 102.739,
            "totalkwH": 16.244
          }
        ]
      }
      """

  Scenario: A registered session is ended
    When I send a message on topic "home/charger/sdm1-1/Import" with payload "86.495"
    And I send a message on topic "home/charger/sdm1-1/Power" with payload "0.956"
    And I let the charge session run for 2 seconds
    And the car is charging at home
    And I let the charge session run for 5 seconds
    And I send a message on topic "home/charger/sdm1-1/Import" with payload "102.739"
    And I send a message on topic "home/charger/sdm1-1/Power" with payload "0.000"
    Then the service publishes a message on topic "home/charger-service" with payload:
      """
      {
        "id": "${json-unit.ignore-element}",
        "car": {
          "id": "${json-unit.ignore-element}",
          "name": "FooEV"
        },
        "odoMeter": 43689,
        "type": "registered",
        "startedAt": "${json-unit.ignore-element}",
        "startkWh": 86.495,
        "endedAt": "${json-unit.ignore-element}",
        "endkWh": 102.739,
        "totalkwH": 16.244
      }
      """
    And a request on "/charges" returns status 200 with body:
      """
      {
        "content": [
          {
            "id": "${json-unit.ignore-element}",
            "car": {
              "id": "${json-unit.ignore-element}",
              "name": "FooEV"
            },
            "odoMeter": 43689,
            "type": "registered",
            "startedAt": "${json-unit.ignore-element}",
            "startkWh": 86.495,
            "endedAt": "${json-unit.ignore-element}",
            "endkWh": 102.739,
            "totalkwH": 16.244
          }
        ]
      }
      """

  Scenario: A zero usage session is saved
    When I send a message on topic "home/charger/sdm1-1/Import" with payload "86.495"
    And I send a message on topic "home/charger/sdm1-1/Power" with payload "0.023"
    And I let the charge session run for 2 seconds
    And I send a message on topic "home/charger/sdm1-1/Import" with payload "86.495"
    And I send a message on topic "home/charger/sdm1-1/Power" with payload "0.000"
    Then the service publishes a message on topic "home/charger-service" with payload:
      """
      {
        "id": "${json-unit.ignore-element}",
        "type": "anonymous",
        "startedAt": "${json-unit.ignore-element}",
        "startkWh": 86.495,
        "endedAt": "${json-unit.ignore-element}",
        "endkWh": 86.495,
        "totalkwH": 0.0
      }
      """
    And a request on "/charges" returns status 200 with body:
      """
      {
        "content": [
          {
            "id": "${json-unit.ignore-element}",
            "type": "anonymous",
            "startedAt": "${json-unit.ignore-element}",
            "startkWh": 86.495,
            "endedAt": "${json-unit.ignore-element}",
            "endkWh": 86.495,
            "totalkwH": 0.0
          }
        ]
      }
      """

  Scenario: Variations in charge power are ignored during a session
    When I send a message on topic "home/charger/sdm1-1/Import" with payload "86.495"
    And I send a message on topic "home/charger/sdm1-1/Power" with payload "0.273"
    And I let the charge session run for 2 seconds
    And I send a message on topic "home/charger/sdm1-1/Power" with payload "6.964"
    And I let the charge session run for 2 seconds
    And I send a message on topic "home/charger/sdm1-1/Power" with payload "7.510"
    And I let the charge session run for 2 seconds
    And I send a message on topic "home/charger/sdm1-1/Import" with payload "113.865"
    And I send a message on topic "home/charger/sdm1-1/Power" with payload "0.000"
    Then a request on "/charges" returns status 200 with body:
      """
      {
        "content": [
          {
            "id": "${json-unit.ignore-element}",
            "type": "anonymous",
            "startedAt": "${json-unit.ignore-element}",
            "startkWh": 86.495,
            "endedAt": "${json-unit.ignore-element}",
            "endkWh": 113.865,
            "totalkwH": 27.37
          }
        ]
      }
      """


