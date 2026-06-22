#!/bin/sh
echo "=== Application configuration and setup ==="

# Create application storage roots
mkdir -p /data/docker /data/containerd /data/dropbear

# Ownership reassignment to non-root user
if [ -d "/data/pyro-engine-home" ]; then
    chown -R dev:docker /data/pyro-engine-home
    chmod +x /data/pyro-engine-home/refresh_token.sh
fi