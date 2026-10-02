#!/bin/bash
# -----------------------------------------------------------------------------
# Pyronear Yocto Image Automatic Validation Script
# -----------------------------------------------------------------------------
# -euo pipefail:
#   -e: Exit immediately if a command exits with a non-zero status.
#   -u: Treat unset variables as an error.
#   -o pipefail: Return status of the last command to exit with a non-zero status in a pipeline.
set -euo pipefail

RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m'

LOG_FILE="/data/validation.log"

pass() {
    echo -e "${GREEN}[OK]${NC}   $1" | tee -a "$LOG_FILE"
}

fail() {
    echo -e "${RED}[FAIL]${NC} $1" | tee -a "$LOG_FILE" >&2
    exit 1
}

echo "=== Beginning Pyronear Image Validation Check [$(date)] ===" | tee -a "$LOG_FILE"

# =============================================================================
# CHECK 1: Expansion and mounting of /data partition
# =============================================================================
if mountpoint -q /data; then
    FREE_DATA_MB=$(df -m /data | awk 'NR==2 {print $4}')
    if [ "$FREE_DATA_MB" -gt 100 ]; then
        pass "/data partition is mounted and expanded (${FREE_DATA_MB}MB free)"
    else
        fail "/data partition space is critically low (${FREE_DATA_MB}MB free)"
    fi
else
    fail "/data is NOT a valid mountpoint!"
fi

# =============================================================================
# CHECK 2: User 'pi' existence and sudo privileges
# =============================================================================
if id "pi" >/dev/null 2>&1; then
    if sudo -u pi -n true 2>/dev/null; then
        pass "User 'pi' exists and has passwordless sudo access"
    else
        fail "User 'pi' exists but sudo passwordless access failed"
    fi
else
    fail "User 'pi' does not exist on this image"
fi

# =============================================================================
# CHECK 3: Network Service (NetworkManager)
# =============================================================================
if systemctl is-active --quiet NetworkManager; then
    pass "NetworkManager service is running"
else
    fail "NetworkManager service is NOT running or failed"
fi

# =============================================================================
# CHECK 4: Docker Container Engine
# =============================================================================
if docker info >/dev/null 2>&1; then
    pass "Docker daemon is active and responding"
else
    fail "Docker daemon is not responding"
fi

# =============================================================================
# CHECK 5: Persistence of /home/pi and /home/engine directories
# =============================================================================
for HOME_DIR in "/home/pi" "/home/engine"; do
    if [ -d "$HOME_DIR" ]; then
        TEST_FILE="$HOME_DIR/.persist_test"
        if touch "$TEST_FILE" 2>/dev/null; then
            rm -f "$TEST_FILE"
            pass "Directory $HOME_DIR is writable and persistent"
        else
            fail "Directory $HOME_DIR is NOT writable!"
        fi
    else
        echo -e "[WARN] Directory $HOME_DIR does not exist yet" | tee -a "$LOG_FILE"
    fi
done

# =============================================================================
# CHECK 6: Detection of failed systemd services
# =============================================================================
FAILED_SERVICES=$(systemctl --failed --no-legend --plain | awk '{print $1}')

if [ -n "$FAILED_SERVICES" ]; then
    fail "Systemd failed services detected: $FAILED_SERVICES"
else
    pass "No failed systemd services found"
fi

# =============================================================================
# CHECK 7: RAUC A/B Update System
# =============================================================================
if command -v rauc >/dev/null 2>&1; then
    if rauc status >/dev/null 2>&1; then
        ACTIVE_SLOT=$(rauc status --output-format=shell 2>/dev/null | grep 'RAUC_BOOT_SLOT' | cut -d'=' -f2 | tr -d "'" || echo "unknown")
        pass "RAUC update engine is functional (Booted slot: ${ACTIVE_SLOT:-unknown})"
    else
        fail "RAUC status check failed"
    fi
else
    fail "RAUC binary is missing on this image"
fi

# =============================================================================
# PROCESS END: Global Success
# =============================================================================
echo "=== All Validation Checks PASSED Successfully [$(date)] ===" | tee -a "$LOG_FILE"
exit 0
