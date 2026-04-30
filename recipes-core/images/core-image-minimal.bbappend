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