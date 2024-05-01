#!/bin/bash
ALLOCATED_MEM=12000M

~/.jdks/openjdk-22/bin/java -Xmx$ALLOCATED_MEM -Xms$ALLOCATED_MEM -jar server.jar -nogui # заменить на путь к java 22 в вашей системе 
