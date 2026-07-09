SUMMARY = "PyroEngine application orchestration and deployment"
LICENSE = "CLOSED"

inherit systemd useradd

SRC_URI = " \
    file://pyro-engine.service \
    file://home-engine.mount \
    file://home-pyro-engine.mount \
    file://pyro-setup-provisioning.conf \
"

S = "${UNPACKDIR}"

DEPENDS += "docker-moby"
RDEPENDS:${PN} += "docker-compose docker-moby"

# =========================================================================
# Creation of the engine user
# =========================================================================
USERADD_PACKAGES = "${PN}"

# System user without login
USERADD_PARAM:${PN} = "--system -m --shell /bin/false --user-group --groups docker engine"

# ========================================================================
# Install instruction
# ========================================================================
do_configure[noexec] = "1"
do_compile[noexec] = "1"

SYSTEMD_SERVICE:${PN} = "pyro-engine.service home-engine.mount home-pyro\x2dengine.mount"
SYSTEMD_AUTO_ENABLE = "enable"

do_install() {

    # Install systemd tmpfiles to update permission on boot
    install -d ${D}${sysconfdir}/tmpfiles.d
    install -m 0644 ${S}/pyro-setup-provisioning.conf ${D}${sysconfdir}/tmpfiles.d/pyro-setup-provisioning.conf

    # Install systemd service
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${S}/pyro-engine.service ${D}${systemd_system_unitdir}/pyro-engine.service

    # Install systemd mount point
    install -m 0644 ${S}/home-engine.mount ${D}${systemd_system_unitdir}/home-engine.mount
    # Single quotes protect the backslash in systemd's \x2d hyphen escape from being stripped by the shell.
    install -m 0644 ${S}/home-pyro-engine.mount '${D}${systemd_system_unitdir}/home-pyro\x2dengine.mount'

    # Create directory for the repository checkout performed by Ansible
    install -d ${D}/home/pyro-engine
}

FILES:${PN} += " \
    ${systemd_system_unitdir}/pyro-engine.service \
    ${systemd_system_unitdir}/home-engine.mount \
    ${systemd_system_unitdir}/home-pyro\x2dengine.mount \
    ${sysconfdir}/tmpfiles.d/pyro-setup-provisioning.conf \
    /home/pyro-engine \
"