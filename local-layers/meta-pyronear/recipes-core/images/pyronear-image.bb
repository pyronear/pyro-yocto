inherit core-image 
inherit extrausers

# =========================================================================
# 1. IMAGE FORMAT AND FEATURES
# =========================================================================
IMAGE_FSTYPES = "wic.bz2 wic.bmap"

IMAGE_FEATURES += "ssh-server-dropbear read-only-rootfs overlayfs-etc"
IMAGE_FEATURES:remove = "debug-tweaks"


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
    expand-rootfs \
    docker-platform-config \
    pyro-engine \
"


# =========================================================================
# 3. USER AND RIGHTS CONFIGURATION
# =========================================================================
# Encrypted password
PASSWD = "\$5\$/KuxK/HaUZeqqNfQ\$4A2lLvZhINJw.Rc2Qgc7Hxm9hJfrFBrRK49xxp0.cC5"

# User creation
EXTRA_USERS_PARAMS = "useradd -p '${PASSWD}' -d /home/dev -m -s /bin/sh -G docker dev;"

donner_droits_sudo() {
    install -d ${IMAGE_ROOTFS}/etc/sudoers.d
    echo 'dev ALL=(ALL) NOPASSWD: ALL' > ${IMAGE_ROOTFS}/etc/sudoers.d/01_dev_rights
    chmod 0440 ${IMAGE_ROOTFS}/etc/sudoers.d/01_dev_rights
}

# Root file system post-processing hook
ROOTFS_POSTPROCESS_COMMAND += "donner_droits_sudo;"

# Use a custom wic file for hardware partitioning
WKS_FILE = "pyronear-raspberrypi.wks.in"