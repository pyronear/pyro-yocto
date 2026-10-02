SUMMARY = "Pyronear image validation checklist script and systemd service"
DESCRIPTION = "Runs validation checks (data expansion, sudo, Docker, NetworkManager, RAUC, systemd status) on startup."
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit systemd

SRC_URI = " \
    file://pyronear-image-check.sh \
    file://pyronear-validation.service \
    file://pyronear-validation-failed.service \
"

S = "${UNPACKDIR}"

RDEPENDS:${PN} += " \
    bash \
    systemd \
    docker \
    rauc \
    networkmanager \
    sudo \
"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "pyronear-validation.service pyronear-validation-failed.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/pyronear-image-check.sh ${D}${bindir}/pyronear-image-check.sh

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${S}/pyronear-validation.service ${D}${systemd_system_unitdir}/pyronear-validation.service
    install -m 0644 ${S}/pyronear-validation-failed.service ${D}${systemd_system_unitdir}/pyronear-validation-failed.service
}

FILES:${PN} += " \
    ${bindir}/pyronear-image-check.sh \
    ${systemd_system_unitdir}/pyronear-validation.service \
    ${systemd_system_unitdir}/pyronear-validation-failed.service \
"
