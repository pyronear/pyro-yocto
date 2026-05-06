# =========================================================================
# 1. FORMAT ET FONCTIONNALITÉS DE L'IMAGE
# =========================================================================
IMAGE_FSTYPES = "wic.bz2 wic.bmap"

# ---CONFIGURATION RAUC---
# IMAGE_FSTYPES:append = " ext4 raucb"
# WKS_FILE = "sdimage-dual-raspberrypi.wks.in"

EXTRA_IMAGE_FEATURES:append = " ssh-server-dropbear"
EXTRA_IMAGE_FEATURES:remove = " debug-tweaks"

# =========================================================================
# 2. LOGICIELS À INSTALLER
# =========================================================================
IMAGE_INSTALL:append = " \
    sudo \
    net-tools \
    docker-moby \
    docker \
    docker-compose \
    pyro-setup \
    curl \
    jq \
    zram \
"
# IMAGE_INSTALL:append = " nano"
# IMAGE_INSTALL:append = " pyro-engine"

# ---CONFIGURATION RAUC---
# IMAGE_INSTALL:append = " rauc rauc-hawkbit-updater"

# =========================================================================
# 3. CONFIGURATION DE L'UTILISATEUR ET DES DROITS
# =========================================================================

inherit extrausers

# Encrypted password
PASSWD = "\$5\$/KuxK/HaUZeqqNfQ\$4A2lLvZhINJw.Rc2Qgc7Hxm9hJfrFBrRK49xxp0.cC5"

# User creation
EXTRA_USERS_PARAMS = "useradd -p '${PASSWD}' -d /home/dev -m -s /bin/sh -G docker dev;"

IMAGE_INSTALL:append = " sudo"

donner_droits_sudo() {
    install -d ${IMAGE_ROOTFS}/etc/sudoers.d
    
    echo 'dev ALL=(ALL) NOPASSWD: ALL' > ${IMAGE_ROOTFS}/etc/sudoers.d/01_dev_rights
    
    chmod 0440 ${IMAGE_ROOTFS}/etc/sudoers.d/01_dev_rights
}

ROOTFS_POSTPROCESS_COMMAND += "donner_droits_sudo;"