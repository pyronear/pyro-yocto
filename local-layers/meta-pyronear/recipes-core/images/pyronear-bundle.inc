inherit bundle

# ============================================================
# PyroNear RAUC OTA Bundle
# ============================================================
# Generates a .raucb file ready for deployment via:
#   rauc install pyronear-bundle-<version>.raucb
#
# Build: bitbake pyronear-bundle-XXX
# Output: tmp/deploy/images/pyronear-rpi5/pyronear-bundle-*.raucb
# ============================================================

SUMMARY = "PyroNear OTA Update Bundle"
DESCRIPTION = "PyroNear OTA update bundle"
LICENSE = "Apache-2.0"

# Must correspond EXACTLY to the 'compatible' field in system.conf
RAUC_BUNDLE_COMPATIBLE = "pyronear-rpi5"
RAUC_BUNDLE_VERSION = "${PV}"

# 'plain' format (default): valid for production, verifies full integrity before installation.
# 'verity' format (recommended): optimizes update time and security via streaming verification (requires dm-verity in the kernel).
RAUC_BUNDLE_FORMAT = "plain"

# Bundle slots - only one rootfs slot to update
RAUC_BUNDLE_SLOTS = "rootfs"
RAUC_SLOT_rootfs[fstype] = "ext4"

RAUC_CERT_FILE = "${RAUC_CREDENTIALS_DIR}/development-1.cert.pem"
RAUC_KEY_FILE = "${RAUC_CREDENTIALS_DIR}/development-1.key.pem"

