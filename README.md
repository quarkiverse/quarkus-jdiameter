# Quarkus Diameter Stack

<!-- ALL-CONTRIBUTORS-BADGE:START - Do not remove or modify this section -->
[![All Contributors](https://img.shields.io/badge/all_contributors-1-orange.svg?style=flat-square)](#contributors-) <!-- ALL-CONTRIBUTORS-BADGE:END -->
[![Build](https://github.com/quarkiverse/jdiameter/actions/workflows/build.yml/badge.svg)](https://github.com/quarkiverse/jdiameter/actions/workflows/build.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.quarkiverse.quarkus-zookeeper/quarkus-zookeeper.svg?label=Maven%20Central&style=flat-square)](https://search.maven.org/artifact/io.quarkiverse.jdiameter/quarkus-jdiameter)
[![GitHub](https://img.shields.io/github/license/quarkiverse/quarkus-cxf?color=blue&style=for-the-badge)](https://github.com/quarkiverse/quarkus-jdiameter/blob/main/LICENSE)

This is a fork of the RestComm jDiameter Stack with a couple of enhancements done:

- Update the minimum java compatibility to Java 21
- Added support for Virtual Threading
- Removed use of Pico Containers
- Added Quarkus Extension
- Updated all the dependencies to latest versions
- Removed support for SCTP protocol (Not used and deprecated for 5G)
- Reworked the jdiameter-ha component to use Infinispan as the caching provider

The project was only started in Jul 2024. The enhancement above is done, but I still need to implement a CI/CD pipeline
for the project and push the artefacts to a public repository.

**NOTE**: This is an unofficial community extension, and it is not directly related nor supported by RestComm Ltd.

## Virtual Threading

There is now a new parameter called "UseVirtualThreads" that if set to true will use virtual threads for the diameter
stack.

To use virtual threading, the minimum supported Java version had to be changed to Java 21.

## Quarkus Extension

The extension allows for the injection of configurations and stacks.
See the docs folder for the relevant documentation.
