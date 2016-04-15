#!/bin/bash

mvn clean package
mvn exec:java -Dexec.mainClass="pluradj.titan.peapod.example.PeapodExample"
