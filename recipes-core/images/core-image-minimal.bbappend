inherit extrausers

# Votre mot de passe chiffré
PASSWD = "\$5\$/KuxK/HaUZeqqNfQ\$4A2lLvZhINJw.Rc2Qgc7Hxm9hJfrFBrRK49xxp0.cC5"

# 1. Création de l'utilisateur (Notez l'ajout de "-G docker" pour qu'il puisse lancer les conteneurs)
EXTRA_USERS_PARAMS = "useradd -p '${PASSWD}' -d /home/dev -m -s /bin/sh -G docker dev;"

# 2. On installe le programme "sudo" sur la Raspberry Pi
IMAGE_INSTALL:append = " sudo"

# 3. On injecte un fichier de configuration pour autoriser 'dev' à utiliser sudo sans mot de passe
donner_droits_sudo() {
    # On s'assure que le dossier sudoers.d existe
    install -d ${IMAGE_ROOTFS}/etc/sudoers.d
    
    # On crée la règle magique pour l'utilisateur dev
    echo 'dev ALL=(ALL) NOPASSWD: ALL' > ${IMAGE_ROOTFS}/etc/sudoers.d/01_dev_rights
    
    # On sécurise le fichier (obligatoire, sinon sudo refusera de fonctionner)
    chmod 0440 ${IMAGE_ROOTFS}/etc/sudoers.d/01_dev_rights
}

# On dit à Yocto d'exécuter cette fonction à la toute fin de la création de l'image
ROOTFS_POSTPROCESS_COMMAND += "donner_droits_sudo;"