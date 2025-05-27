Feature: Charge sessions

  Background:
    Given a car with the name "FooEV" with the odometer reading 43689 km

  Scenario: An anonymous session is started
    When I send a message on topic "home/laadpaal" with payload:
      """
      {
        "timestamp": "123456",
        "values": {
          "power_total": 0.000,
          "total_kwh_import": 86.495
        }
      }
      """
    And I send a message on topic "home/laadpaal" with payload:
      """
      {
        "timestamp": "123456",
        "values": {
          "power_total": 0.956,
          "total_kwh_import": 86.495
        }
      }
      """
    Then the service publishes a message on topic "apps/charger-service" with payload:
      """
      {
        "id": "${json-unit.ignore-element}",
        "type": "anonymous",
        "started_at": "${json-unit.ignore-element}",
        "start_kwh": 86.495
      }
      """
    And a request on "/v1/charge-sessions" returns status 200 with body:
      """
      {
        "content": [
          {
            "id": "${json-unit.ignore-element}",
            "type": "anonymous",
            "started_at": "${json-unit.ignore-element}",
            "start_kwh": 86.495
          }
        ]
      }
      """

  Scenario: A session is started and registered to a car
    When I send a message on topic "home/laadpaal" with payload:
      """
      {
        "timestamp": "123456",
        "values": {
          "power_total": 0.000,
          "total_kwh_import": 86.495
        }
      }
      """
    And I send a message on topic "home/laadpaal" with payload:
      """
      {
        "timestamp": "123456",
        "values": {
          "power_total": 0.263,
          "total_kwh_import": 86.495
        }
      }
      """
    Then the service publishes a message on topic "apps/charger-service" with payload:
      """
      {
        "id": "${json-unit.ignore-element}",
        "type": "anonymous",
        "started_at": "${json-unit.ignore-element}",
        "start_kwh": 86.495
      }
      """
    When the car is charging at home
    Then the service publishes a message on topic "apps/charger-service" with payload:
      """
      {
        "id": "${json-unit.ignore-element}",
        "vehicle_id": "${json-unit.ignore-element}",
        "odometer": 43689,
        "type": "registered",
        "started_at": "${json-unit.ignore-element}",
        "start_kwh": 86.495
      }
      """
    And a request on "/v1/charge-sessions" returns status 200 with body:
      """
      {
        "content": [
          {
            "id": "${json-unit.ignore-element}",
            "vehicle_id": "${json-unit.ignore-element}",
            "odometer": 43689,
            "type": "registered",
            "started_at": "${json-unit.ignore-element}",
            "start_kwh": 86.495
          }
        ]
      }
      """

  Scenario: An anonymous session is ended
    When I send a message on topic "home/laadpaal" with payload:
      """
      {
        "timestamp": "123456",
        "values": {
          "power_total": 0.000,
          "total_kwh_import": 86.495
        }
      }
      """
    And I send a message on topic "home/laadpaal" with payload:
      """
      {
        "timestamp": "123456",
        "values": {
          "power_total": 0.956,
          "total_kwh_import": 86.495
        }
      }
      """
    And I let the charge session run for 5 seconds
    And I send a message on topic "home/laadpaal" with payload:
      """
      {
        "timestamp": "123456",
        "values": {
          "power_total": 0.956,
          "total_kwh_import": 102.739
        }
      }
      """
    And I send a message on topic "home/laadpaal" with payload:
      """
      {
        "timestamp": "123456",
        "values": {
          "power_total": 0.000,
          "total_kwh_import": 102.739
        }
      }
      """
    Then the service publishes a message on topic "apps/charger-service" with payload:
      """
      {
        "id": "${json-unit.ignore-element}",
        "type": "anonymous",
        "started_at": "${json-unit.ignore-element}",
        "start_kwh": 86.495,
        "ended_at": "${json-unit.ignore-element}",
        "end_kwh": 102.739,
        "total_kwh": 16.244
      }
      """
    And a request on "/v1/charge-sessions" returns status 200 with body:
      """
      {
        "content": [
          {
            "id": "${json-unit.ignore-element}",
            "type": "anonymous",
            "started_at": "${json-unit.ignore-element}",
            "start_kwh": 86.495,
            "ended_at": "${json-unit.ignore-element}",
            "end_kwh": 102.739,
            "total_kwh": 16.244
          }
        ]
      }
      """

  Scenario: A registered session is ended
    When I send a message on topic "home/laadpaal" with payload:
      """
      {
        "timestamp": "123456",
        "values": {
          "power_total": 0.000,
          "total_kwh_import": 86.495
        }
      }
      """
    And I send a message on topic "home/laadpaal" with payload:
      """
      {
        "timestamp": "123456",
        "values": {
          "power_total": 0.956,
          "total_kwh_import": 86.495
        }
      }
      """
    And I let the charge session run for 2 seconds
    And the car is charging at home
    And I let the charge session run for 5 seconds
    And I send a message on topic "home/laadpaal" with payload:
      """
      {
        "timestamp": "123456",
        "values": {
          "power_total": 0.956,
          "total_kwh_import": 102.739
        }
      }
      """
    And I send a message on topic "home/laadpaal" with payload:
      """
      {
        "timestamp": "123456",
        "values": {
          "power_total": 0.000,
          "total_kwh_import": 102.739
        }
      }
      """
    Then the service publishes a message on topic "apps/charger-service" with payload:
      """
      {
        "id": "${json-unit.ignore-element}",
        "vehicle_id": "${json-unit.ignore-element}",
        "odometer": 43689,
        "type": "registered",
        "started_at": "${json-unit.ignore-element}",
        "start_kwh": 86.495,
        "ended_at": "${json-unit.ignore-element}",
        "end_kwh": 102.739,
        "total_kwh": 16.244
      }
      """
    And a request on "/v1/charge-sessions" returns status 200 with body:
      """
      {
        "content": [
          {
            "id": "${json-unit.ignore-element}",
            "vehicle_id": "${json-unit.ignore-element}",
            "odometer": 43689,
            "type": "registered",
            "started_at": "${json-unit.ignore-element}",
            "start_kwh": 86.495,
            "ended_at": "${json-unit.ignore-element}",
            "end_kwh": 102.739,
            "total_kwh": 16.244
          }
        ]
      }
      """

  Scenario: A zero usage session is saved
    When I send a message on topic "home/laadpaal" with payload:
      """
      {
        "timestamp": "123456",
        "values": {
          "power_total": 0.000,
          "total_kwh_import": 86.495
        }
      }
      """
    And I send a message on topic "home/laadpaal" with payload:
      """
      {
        "timestamp": "123456",
        "values": {
          "power_total": 0.023,
          "total_kwh_import": 86.495
        }
      }
      """
    And I let the charge session run for 2 seconds
    And I send a message on topic "home/laadpaal" with payload:
      """
      {
        "timestamp": "123456",
        "values": {
          "power_total": 0.000,
          "total_kwh_import": 86.495
        }
      }
      """
    Then the service publishes a message on topic "apps/charger-service" with payload:
      """
      {
        "id": "${json-unit.ignore-element}",
        "type": "anonymous",
        "started_at": "${json-unit.ignore-element}",
        "start_kwh": 86.495,
        "ended_at": "${json-unit.ignore-element}",
        "end_kwh": 86.495,
        "total_kwh": 0.0
      }
      """
    And a request on "/v1/charge-sessions" returns status 200 with body:
      """
      {
        "content": [
          {
            "id": "${json-unit.ignore-element}",
            "type": "anonymous",
            "started_at": "${json-unit.ignore-element}",
            "start_kwh": 86.495,
            "ended_at": "${json-unit.ignore-element}",
            "end_kwh": 86.495,
            "total_kwh": 0.0
          }
        ]
      }
      """

  Scenario: Variations in charge power are ignored during a session
    When I send a message on topic "home/laadpaal" with payload:
      """
      {
        "timestamp": "123456",
        "values": {
          "power_total": 0.000,
          "total_kwh_import": 86.495
        }
      }
      """
    And I send a message on topic "home/laadpaal" with payload:
      """
      {
        "timestamp": "123456",
        "values": {
          "power_total": 0.273,
          "total_kwh_import": 86.495
        }
      }
      """
    And I let the charge session run for 2 seconds
    And I send a message on topic "home/laadpaal" with payload:
      """
      {
        "timestamp": "123456",
        "values": {
          "power_total": 6.964,
          "total_kwh_import": 97.589
        }
      }
      """
    And I let the charge session run for 2 seconds
    And I send a message on topic "home/laadpaal" with payload:
      """
      {
        "timestamp": "123456",
        "values": {
          "power_total": 7.510,
          "total_kwh_import": 104.738
        }
      }
      """
    And I let the charge session run for 2 seconds
    And I send a message on topic "home/laadpaal" with payload:
      """
      {
        "timestamp": "123456",
        "values": {
          "power_total": 0.000,
          "total_kwh_import": 113.865
        }
      }
      """
    Then a request on "/v1/charge-sessions" returns status 200 with body:
      """
      {
        "content": [
          {
            "id": "${json-unit.ignore-element}",
            "type": "anonymous",
            "started_at": "${json-unit.ignore-element}",
            "start_kwh": 86.495,
            "ended_at": "${json-unit.ignore-element}",
            "end_kwh": 113.865,
            "total_kwh": 27.37
          }
        ]
      }
      """


