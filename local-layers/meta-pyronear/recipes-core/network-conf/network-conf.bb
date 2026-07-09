SUMMARY = "Pre-configured NetworkManager connection for eth0"
LICENSE = "CLOSED"

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