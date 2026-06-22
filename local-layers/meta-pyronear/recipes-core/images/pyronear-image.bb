inherit core-image 
inherit extrausers

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
    sudo \
    net-tools \
    docker-moby \
    docker \
    docker-compose \
    curl \
    jq \
    zram \
    pyro-setup \
    expand-data \
    nano \
"


# =========================================================================
# 3. USER AND RIGHTS CONFIGURATION
# =========================================================================
# Encrypted password
PASSWD = "\$5\$/KuxK/HaUZeqqNfQ\$4A2lLvZhINJw.Rc2Qgc7Hxm9hJfrFBrRK49xxp0.cC5"

# User creation
EXTRA_USERS_PARAMS = "useradd -p '${PASSWD}' -d /data/pyro-engine-home -m -s /bin/sh -G docker dev;"

donner_droits_sudo() {
    install -d ${IMAGE_ROOTFS}/etc/sudoers.d
    echo 'dev ALL=(ALL) NOPASSWD: ALL' > ${IMAGE_ROOTFS}/etc/sudoers.d/01_dev_rights
    chmod 0440 ${IMAGE_ROOTFS}/etc/sudoers.d/01_dev_rights
}

# Root file system post-processing hook
ROOTFS_POSTPROCESS_COMMAND += "donner_droits_sudo;"

# Use a custom wic file for hardware partitioning
WKS_FILE = "pyronear-raspberrypi.wks.in"