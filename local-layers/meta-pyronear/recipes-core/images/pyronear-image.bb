inherit core-image 

# =========================================================================
# 1. IMAGE FORMAT AND FEATURES
# =========================================================================
IMAGE_FSTYPES = "wic.bz2 wic.bmap"

# Core features configuration
IMAGE_FEATURES += " \
    ssh-server-dropbear \
"

# Set read-only rootfs with overlayfs on /etc to allow configuration changes
IMAGE_FEATURES += " \
    read-only-rootfs \
    overlayfs-etc \
"

# Enable rootfs access without password for debug
IMAGE_FEATURES += " \
    allow-empty-password \
    empty-root-password \
    allow-root-login \
"


# =========================================================================
# 2. SOFTWARE TO INSTALL
# =========================================================================
# Yocto includes automatically 'packagegroup-core-boot', we add the rest
IMAGE_INSTALL += " \
    net-tools \
    nano \
    zram \
    expand-data \
"

# For Ansible deployment :
IMAGE_INSTALL += " \
    git \
    openvpn \
    networkmanager-nmcli \
    ca-certificates \
    python3 \
    python3-docker \
    cronie \
    user-pi \
"
# Pyro-setup
IMAGE_INSTALL += " \
    pyro-setup \
"

# Use a custom wic file for hardware partitioning
WKS_FILE = "pyronear-raspberrypi.wks.in"