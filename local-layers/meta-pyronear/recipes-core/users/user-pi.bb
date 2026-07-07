SUMMARY = "Recipe dedicated to the creation of the pi user and its sudo access"
DESCRIPTION = "Configures the pi user with its home directory and sudoers privileges."
LICENSE = "CLOSED"

inherit useradd systemd


RDEPENDS:${PN} += "sudo"

# =========================================================================
# Dependencies
# =========================================================================
SRC_URI = ""

S = "${UNPACKDIR}"

# =========================================================================
# Creation of the pi user
# =========================================================================
USERADD_PACKAGES = "${PN}"

PASSWD = "\$6\$L6/DzLx2WAnQNk91\$43TLlF5Is.bmmgH/g9LqcgtTFpfE4sLWDPlh4.5WqVvwKCSwwhMus21OuZnz4vAZVta/YAPIamKtyC9i9fMMx/"

USERADD_PARAM:${PN} = "-p '${PASSWD}' -d /home/pi -M -G sudo pi"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {

    install -d ${D}/home
    install -d ${D}/data/home/pi

    # Symbolic link /home/pi -> /data/home/pi
    ln -s /data/home/pi ${D}/home/pi

    # Change ownership of the directories
    chown -h pi:pi ${D}/home/pi
    chown -h pi:pi ${D}/data/home/pi

    # Writing sudo configuration directly
    install -d -m 0750 ${D}${sysconfdir}/sudoers.d
    echo 'pi ALL=(ALL) NOPASSWD: ALL' > ${D}${sysconfdir}/sudoers.d/pi
    chmod 0440 ${D}${sysconfdir}/sudoers.d/pi

}

FILES:${PN} += " \
    /home/pi \
    /data/home/pi \
    ${sysconfdir}/sudoers.d/pi \
"