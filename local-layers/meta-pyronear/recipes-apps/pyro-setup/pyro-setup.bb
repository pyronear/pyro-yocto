SUMMARY = "Installation des fichiers de configuration pour PyroEngine Docker"
LICENSE = "CLOSED"

inherit systemd

RDEPENDS:${PN} += "parted e2fsprogs-resize2fs curl jq"
#parted e2fsprogs-resize2fs
# List of all our files
SRC_URI = " \
    file://docker-compose.yml \
    file://.env \
    file://data/credentials.json \
    file://refresh_token.sh \
    file://pyro-engine.service \
    file://expand-rootfs.sh \
    file://expand-rootfs.service\
"

# We prevent Yocto from attempting to compile code
do_configure[noexec] = "1"
do_compile[noexec] = "1"

# Configure the service to start automatically
SYSTEMD_SERVICE:${PN} = "expand-rootfs.service pyro-engine.service"
# expand-rootfs.service

SYSTEMD_AUTO_ENABLE = "enable"

do_install() {
    # We create the destination folders on the Pi 5
    install -d ${D}/home/dev/pyro-engine
    install -d ${D}/home/dev/pyro-engine/data
    install -m 0644 ${WORKDIR}/docker-compose.yml ${D}/home/dev/pyro-engine/
    install -m 0600 ${WORKDIR}/.env ${D}/home/dev/pyro-engine/
    install -m 0644 ${WORKDIR}/data/credentials.json ${D}/home/dev/pyro-engine/data/

    # Installing the token script with execution permissions
    install -m 0755 ${WORKDIR}/refresh_token.sh ${D}/home/dev/pyro-engine/

    # Installing the enlargement script
    install -d ${D}/usr/bin
    install -m 0755 ${WORKDIR}/expand-rootfs.sh ${D}/usr/bin/

    # Installing the Systemd service file
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/expand-rootfs.service ${D}${systemd_system_unitdir}/
    install -m 0644 ${WORKDIR}/pyro-engine.service ${D}${systemd_system_unitdir}/
}

pkg_postinst_ontarget:${PN}() {
    chown -R dev:dev /home/dev/pyro-engine
}

# Listing all files included in the package
FILES:${PN} += " \
    /home/dev \
    ${systemd_system_unitdir}/pyro-engine.service \
    /usr/bin/expand-rootfs.sh \
    ${systemd_system_unitdir}/expand-rootfs.service \
"