inherit core-image 
inherit extrausers

# =========================================================================
# 1. FORMAT ET FONCTIONNALITÉS DE L'IMAGE
# =========================================================================
IMAGE_FSTYPES = "wic.bz2 wic.bmap"

IMAGE_FEATURES += "ssh-server-dropbear"
IMAGE_FEATURES:remove = "debug-tweaks"

# =========================================================================
# 2. LOGICIELS À INSTALLER
# =========================================================================
# Yocto inclut automatiquement 'packagegroup-core-boot', on y ajoute le reste
IMAGE_INSTALL += " \
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
# IMAGE_INSTALL += "nano"


# =========================================================================
# 3. CONFIGURATION DE L'UTILISATEUR ET DES DROITS
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

ROOTFS_POSTPROCESS_COMMAND += "donner_droits_sudo;"