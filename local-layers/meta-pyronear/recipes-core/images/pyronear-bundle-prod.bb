inherit bundle

# ============================================================
# PyroNear RAUC OTA Bundle
# ============================================================
# Generates a .raucb file ready for deployment via:
#   rauc install pyronear-bundle-<version>.raucb
#
# Build: bitbake pyronear-bundle
# Output: tmp/deploy/images/pyronear-rpi5/pyronear-bundle-*.raucb
# ============================================================

DESCRIPTION = "PyroNear OTA update bundle"
LICENSE = "CLOSED"

# Must correspond EXACTLY to the 'compatible' field in system.conf
RAUC_BUNDLE_COMPATIBLE = "pyronear-rpi5"
RAUC_BUNDLE_VERSION = "${PV}"

# 'plain' format for development
# Switch to 'verity' for production (requires dm-verity in the kernel)
RAUC_BUNDLE_FORMAT = "plain"

# Bundle slots - only one rootfs slot to update
RAUC_BUNDLE_SLOTS = "rootfs"

# Rootfs slot image source
RAUC_SLOT_rootfs = "pyronear-image-prod"
RAUC_SLOT_rootfs[fstype] = "ext4"

RAUC_CERT_FILE = "${THISDIR}/../../files/key-rauc/development-1.cert.pem"
RAUC_KEY_FILE = "${THISDIR}/../../files/key-rauc/development-1.key.pem"
