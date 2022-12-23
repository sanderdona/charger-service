# charger-service
[![build](https://github.com/sanderdona/charger-service/actions/workflows/master.yml/badge.svg)](https://github.com/sanderdona/charger-service/actions/workflows/master.yml)
[![coverage](https://codecov.io/gh/sanderdona/charger-service/branch/main/graph/badge.svg?token=FQ9YFVGCFE)](https://codecov.io/gh/sanderdona/charger-service)

`charger-service` can be used to register your ev's charge sessions, based on events sent over MQTT. It uses:

- [ModBus Measurement Daemon](https://github.com/volkszaehler/mbmd) to detect *if* we are charging.
- [Teslamate](https://github.com/adriankumpf/teslamate) to detect *who* is charging.
