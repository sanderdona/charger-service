# ⚡ EV Charge Session Logger
[![build](https://github.com/sanderdona/charger-service/actions/workflows/ci-build-and-tag.yml/badge.svg)](https://github.com/sanderdona/charger-service/actions/workflows/master.yml)

This service allows you to log your EV's charging sessions based on MQTT events from your charger.
It integrates with [Teslamate's](https://github.com/adriankumpf/teslamate) MQTT topics to determine whether a charging session belongs to your vehicle, 
and automatically sets the current mileage for each session.

## 🚀 Future Plans

- Support for different MQTT event formats from various types of chargers.
- Support for both single-topic and multi-topic MQTT message structures.