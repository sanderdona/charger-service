Feature: charge sessions

  Background:
    Given a car with the name "FooEV"

  Scenario: A new charge session is started
    When I send a message on topic "home/charger/sdm1-1/Import" with payload "86.495"
    And I send a message on topic "home/charger/sdm1-1/Power" with payload "0.956"
    Then the service publishes a message on topic "home/charger-service" with payload:
      """
      {
        "id": "${json-unit.ignore-element}",
        "odoMeter": 0,
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
            "odoMeter": 0,
            "type": "anonymous",
            "startedAt": "${json-unit.ignore-element}",
            "startkWh": 86.495
          }
        ]
      }
      """

  Scenario: A new charge session is started and registered to a car
    When I send a message on topic "home/charger/sdm1-1/Import" with payload "86.495"
    And I send a message on topic "home/charger/sdm1-1/Power" with payload "0.956"
    Then the service publishes a message on topic "home/charger-service" with payload:
      """
      {
        "id": "${json-unit.ignore-element}",
        "odoMeter": 0,
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
        "odoMeter": 0,
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
            "odoMeter": 0,
            "type": "registered",
            "startedAt": "${json-unit.ignore-element}",
            "startkWh": 86.495
          }
        ]
      }
      """

