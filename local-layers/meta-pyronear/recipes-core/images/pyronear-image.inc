inherit core-image

LICENSE = "Apache-2.0"

# =========================================================================
# IMAGE FEATURES
# =========================================================================
# Core features configuration
IMAGE_FEATURES += " \
    ssh-server-dropbear \
"

# Set read-only rootfs with overlayfs on /etc to allow configuration changes
IMAGE_FEATURES += " \
    read-only-rootfs \
    overlayfs-etc \
"

# =========================================================================
# CORE-IMAGE CONFIGURATION
# =========================================================================
# Force only minimal packages
CORE_IMAGE_BASE_INSTALL = "packagegroup-core-boot packagegroup-base"

# =========================================================================
# SOFTWARES TO INSTALL
# =========================================================================
IMAGE_INSTALL += " \
    expand-data \
"

# Network configuration
# configuration using Networkmanager
# systemd-networkd is disable in systemd.bbappend
# add nmcli for user configuration
IMAGE_INSTALL += " \
    network-conf \
    networkmanager-nmcli \
"

# For Ansible deployment
IMAGE_INSTALL += " \
    git \
    openvpn \
    ca-certificates \
    curl \
    python3 \
    python3-docker \
    cronie \
    user-pi \
"

# Pyro-setup
IMAGE_INSTALL += " \
    pyro-setup \
"

# Update
IMAGE_INSTALL += " \
    rauc \
"

# =========================================================================
# IMAGE FORMAT AND MEMORY LAYOUT
# =========================================================================
IMAGE_FSTYPES = "wic.bz2 wic.bmap ext4 tar.bz2"

# Use a custom wic file for hardware partitioning
WKS_FILE = "pyronear-raspberrypi.wks.in"
WIC_CREATE_EXTRA_ARGS += " --no-fstab-update"
