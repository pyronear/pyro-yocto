SUMMARY = "Installation des fichiers de configuration pour PyroEngine Docker"
LICENSE = "CLOSED"

# On indique à Yocto que l'on va utiliser des services Systemd
inherit systemd

# On indique de quels outils Linux ce paquet a besoin pour fonctionner
RDEPENDS:${PN} += "parted e2fsprogs-resize2fs curl jq"

# Liste de tous nos fichiers
SRC_URI = " \
    file://docker-compose.yml \
    file://.env \
    file://data/credentials.json \
    file://expand-rootfs.sh \
    file://expand-rootfs.service \
    file://refresh_token.sh \
    file://pyro-engine.service \
"

# 1. On empêche Yocto d'essayer de compiler du code
do_configure[noexec] = "1"
do_compile[noexec] = "1"

# On configure l'activation automatique du service
SYSTEMD_SERVICE:${PN} = "expand-rootfs.service pyro-engine.service"
SYSTEMD_AUTO_ENABLE = "enable"

# 2. L'étape d'installation (on utilise WORKDIR directement)
do_install() {
    # On crée les dossiers de destination sur la Pi
    install -d ${D}/home/dev/pyro-engine
    install -d ${D}/home/dev/pyro-engine/data
    install -m 0644 ${WORKDIR}/docker-compose.yml ${D}/home/dev/pyro-engine/
    install -m 0600 ${WORKDIR}/.env ${D}/home/dev/pyro-engine/
    install -m 0644 ${WORKDIR}/data/credentials.json ${D}/home/dev/pyro-engine/data/

    # Installation du script de token avec droits d'exécution (0755)
    install -m 0755 ${WORKDIR}/refresh_token.sh ${D}/home/dev/pyro-engine/

    # 2. Installation du script d'agrandissement
    install -d ${D}/usr/bin
    install -m 0755 ${WORKDIR}/expand-rootfs.sh ${D}/usr/bin/

    # 3. Installation du fichier service Systemd
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/expand-rootfs.service ${D}${systemd_system_unitdir}/
    install -m 0644 ${WORKDIR}/pyro-engine.service ${D}${systemd_system_unitdir}/
}

# Déclaration de tous les fichiers appartenant au paquet
FILES:${PN} += " \
    /home/dev \
    /usr/bin/expand-rootfs.sh \
    ${systemd_system_unitdir}/expand-rootfs.service \
    ${systemd_system_unitdir}/pyro-engine.service \
"
pkg_postinst_ontarget:${PN}() {
    chown -R dev:dev /home/dev/pyro-engine
}