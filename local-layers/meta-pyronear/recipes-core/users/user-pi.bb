SUMMARY = "Recipe dedicated to the creation of the pi user and its sudo access"
DESCRIPTION = "Configures the pi user with its home directory and sudoers privileges."
LICENSE = "CLOSED"

inherit useradd systemd

SRC_URI = " \
    file://home-pi.mount \
    file://pi-provisioning.conf \
    file://pi \
"

S = "${UNPACKDIR}"

RDEPENDS:${PN} += "sudo"

# =========================================================================
# Creation of the pi user
# =========================================================================
USERADD_PACKAGES = "${PN}"

# SHA-512 password hash
# Example generated with: openssl passwd -6 xxxxxxx
PASSWD = "\$6\$L6/DzLx2WAnQNk91\$43TLlF5Is.bmmgH/g9LqcgtTFpfE4sLWDPlh4.5WqVvwKCSwwhMus21OuZnz4vAZVta/YAPIamKtyC9i9fMMx/"

USERADD_PARAM:${PN} = "-p '${PASSWD}' -m -G sudo pi"

# ========================================================================
# Install instruction
# ========================================================================
do_configure[noexec] = "1"
do_compile[noexec] = "1"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "home-pi.mount"

do_install() {

    # Install sudo configuration
    install -d -m 0750 ${D}${sysconfdir}/sudoers.d
    install -m 0440 ${S}/pi ${D}${sysconfdir}/sudoers.d/pi

    # Install systemd tmpfiles to update permission on boot
    install -d ${D}${sysconfdir}/tmpfiles.d
    install -m 0644 ${S}/pi-provisioning.conf ${D}${sysconfdir}/tmpfiles.d/pi-provisioning.conf

    # Install mount point to bind data partition to pi home
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${S}/home-pi.mount ${D}${systemd_system_unitdir}/home-pi.mount
}

FILES:${PN} += " \
    ${sysconfdir}/sudoers.d/pi \
    ${systemd_system_unitdir}/home-pi.mount \
    ${sysconfdir}/tmpfiles.d/pi-provisioning.conf \
"