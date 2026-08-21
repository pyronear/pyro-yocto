SUMMARY = "Pre-configured NetworkManager connection for eth0"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

SRC_URI = "file://eth0.nmconnection"

S = "${UNPACKDIR}"

RDEPENDS:${PN} += "networkmanager"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    # Create the destination directory for NetworkManager profiles
    install -d ${D}${sysconfdir}/NetworkManager/system-connections

    # Install file with 0600 permission
    install -m 0600 ${S}/eth0.nmconnection ${D}${sysconfdir}/NetworkManager/system-connections/eth0.nmconnection
}

FILES:${PN} += " \
    ${sysconfdir}/NetworkManager/system-connections/eth0.nmconnection \
"