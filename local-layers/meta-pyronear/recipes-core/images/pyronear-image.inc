inherit core-image 

# =========================================================================
# 1. IMAGE FORMAT AND FEATURES
# =========================================================================
IMAGE_FSTYPES = "wic.bz2 wic.bmap ext4 tar.bz2"

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
# 2. SOFTWARE TO INSTALL
# =========================================================================
# Yocto includes automatically 'packagegroup-core-boot', we add the rest
IMAGE_INSTALL += " \
    net-tools \
    zram \
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

# For Ansible deployment :
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

IMAGE_INSTALL:append = " rauc"

IMAGE_INSTALL:append = " kernel-image kernel-devicetree"

IMAGE_BOOT_FILES:append = " boot.scr"

# Use a custom wic file for hardware partitioning
WKS_FILE = "pyronear-raspberrypi.wks.in"

WIC_CREATE_EXTRA_ARGS += " --no-fstab-update"
