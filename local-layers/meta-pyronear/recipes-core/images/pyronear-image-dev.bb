require pyronear-image.inc
SUMMARY = "Pyronear Development Image"

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
