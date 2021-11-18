# charge-service
[![Build and Publish](https://github.com/sanderdona/charger-service/actions/workflows/master.yml/badge.svg)](https://github.com/sanderdona/charger-service/actions/workflows/master.yml)

`charger-service` can be used to register your ev's charge sessions, based on events sent over MQTT. It uses:

- [ModBus Measurement Daemon](https://github.com/volkszaehler/mbmd) to detect *if* we are charging.
- [Teslamate](https://github.com/adriankumpf/teslamate) to detect *who* is charging.
