#!/bin/sh
echo "=== Application configuration and setup ==="

# Create application storage roots
mkdir -p /data/docker /data/containerd /data/dropbear

# Ownership reassignment to non-root user
if [ -d "/data/pyro-engine-home" ]; then
    chown -R dev:docker /data/pyro-engine-home
    chmod -R g+w /data/pyro-engine-home
fi