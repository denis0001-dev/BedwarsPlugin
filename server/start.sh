#!/bin/bash
ALLOCATED_MEM=12000M

java -Xmx$ALLOCATED_MEM -Xms$ALLOCATED_MEM -jar server.jar -nogui # заменить на путь к java 22 в вашей системе
