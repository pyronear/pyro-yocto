#!/bin/sh
# check_os_health.sh (Formerly check_provisioning.sh)

COMPOSE_FILE="/data/home/engine/docker-compose.yml"

# 1. Factory Mode (Not provisioned)
if [ ! -f "$COMPOSE_FILE" ]; then
    echo "File $COMPOSE_FILE missing. Unprovisioned mode."
    echo "Default OS validation."
    /usr/bin/rauc status mark-good
    exit 0
fi

# 2. Production Mode: OS health validation
echo "Production mode. Checking OS health..."

# Give the network 10 seconds to finish initializing
sleep 10 

# A. Execution engine check (Docker Daemon)
if ! systemctl is-active --quiet docker.service; then
    echo "OS ERROR: Docker service (dockerd) failed! RAUC aborted."
    exit 1
fi

# B. Network connectivity check (Ping to public DNS or your server)
# -c 1 (1 packet), -W 5 (5s timeout)
if ! ping -c 1 -W 5 8.8.8.8 >/dev/null 2>&1; then
    echo "OS ERROR: No internet connectivity detected! RAUC aborted."
    exit 1
fi

echo "OS health validated (Docker OK, Network OK). Update confirmed."
/usr/bin/rauc status mark-good
exit 0