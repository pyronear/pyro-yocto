#!/bin/sh
echo "=== Application configuration and setup ==="

# Create application storage roots
mkdir -p /data/docker /data/containerd /data/dropbear

# Ownership reassignment to non-root user
if [ -d "/data/pyro-engine" ]; then
    chown -R dev:docker /data/pyro-engine
    chmod +x /data/pyro-engine/refresh_token.sh
fi