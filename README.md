# OakGP
[![Maven Central](https://img.shields.io/maven-central/v/org.oakgp/oakgp.svg)](https://mvnrepository.com/artifact/org.oakgp/oakgp)
[![Build Status](https://travis-ci.org/s-webber/oakgp.png?branch=master)](https://travis-ci.org/s-webber/oakgp)
[![License](https://img.shields.io/badge/license-Apache%20v2.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)

## About
OakGP is a [genetic programming](http://www.oakgp.org/introduction-to-genetic-programming) (GP) framework written in Java. Genetic programming is part of the evolutionary computation subfield of artificial intelligence. Genetic programming uses techniques inspired by biological evolution to evolve computer programs that perform user-defined tasks. OakGP uses tree data structures to represent the programs it generates.

## Documentation
Please visit [oakgp.org](http://www.oakgp.org/) for more details. The website includes the following examples of how genetic programming can be used to automatically generate programs:

- [Symolic Regression](http://www.oakgp.org/symbolic-regression)
- [Grid War](http://www.oakgp.org/grid-war)
- [Towers of Hanoi](http://www.oakgp.org/towers-of-hanoi)
- [The Artificial Ant Problem](http://www.oakgp.org/artificial-ant-problem) (also known as the Santa Fe Trail problem)

## Integration

You can include the latest *release* version of OakGP as a dependency of your Java project by adding the following to its Maven `pom.xml` file:

```
<dependency>
  <groupId>org.oakgp</groupId>
  <artifactId>oakgp</artifactId>
  <version>0.0.4</version>
</dependency>
```

To use the latest *development* version of OakGP specify the following instead:

```
<dependency>
  <groupId>org.oakgp</groupId>
  <artifactId>oakgp</artifactId>
  <version>0.1.0-SNAPSHOT</version>
</dependency>
```

## Reporting Issues

We would be grateful for feedback. If you would like to report a bug, suggest an enhancement or ask a question then please [create a new issue](https://github.com/s-webber/oakgp/issues/new).
