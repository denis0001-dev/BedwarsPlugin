#!/bin/bash
ALLOCATED_MEM=2000M

java -Xmx$ALLOCATED_MEM -Xms$ALLOCATED_MEM -jar server.jar
