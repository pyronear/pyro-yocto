SUMMARY = "Pyronear Development Image"
DESCRIPTION = "Development image for PyroNear."
LICENSE = "Apache-2.0"

require pyronear-image.inc

# Enable rootfs access without password for debug
IMAGE_FEATURES += " \
    allow-empty-password \
    empty-root-password \
    allow-root-login \
"

IMAGE_INSTALL += " \
    nano \
    htop \
"
