SUMMARY = "Service for expanding the DATA partition on first boot"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit systemd

RDEPENDS:${PN} += "e2fsprogs-resize2fs util-linux"

SRC_URI = " \
    file://expand-data.sh \
    file://expand-data.service \
"

S = "${UNPACKDIR}"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

SYSTEMD_SERVICE:${PN} = "expand-data.service"
SYSTEMD_AUTO_ENABLE = "enable"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${UNPACKDIR}/expand-data.sh ${D}${bindir}/expand-data.sh

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/expand-data.service ${D}${systemd_system_unitdir}/expand-data.service
}

FILES:${PN} += " \
    ${bindir}/expand-data.sh \
    ${systemd_system_unitdir}/expand-data.service \
"