SUMMARY = "Service for expanding the DATA partition on first boot"
LICENSE = "CLOSED"

inherit systemd

RDEPENDS:${PN} += "parted e2fsprogs-resize2fs"

SRC_URI = " \
    file://expand-rootfs.sh \
    file://expand-rootfs.service \
"

S = "${UNPACKDIR}"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

SYSTEMD_SERVICE:${PN} = "expand-rootfs.service"
SYSTEMD_AUTO_ENABLE = "enable"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${UNPACKDIR}/expand-rootfs.sh ${D}${bindir}/expand-rootfs.sh

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/expand-rootfs.service ${D}${systemd_system_unitdir}/expand-rootfs.service
}

FILES:${PN} += " \
    ${bindir}/expand-rootfs.sh \
    ${systemd_system_unitdir}/expand-rootfs.service \
"