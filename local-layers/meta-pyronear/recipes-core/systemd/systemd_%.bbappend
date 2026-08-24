# System override for systemd-timesyncd (Yocto Read-Only Standard)
FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += " \
    file://timesyncd-override.conf \
    file://10-watchdog.conf \
"

do_install:append() {
    install -d ${D}${sysconfdir}/systemd/system.conf.d

    install -m 0644 ${UNPACKDIR}/10-watchdog.conf ${D}${sysconfdir}/systemd/system.conf.d/10-watchdog.conf

    install -d ${D}${systemd_system_unitdir}/systemd-timesyncd.service.d
    install -m 0644 ${UNPACKDIR}/timesyncd-override.conf ${D}${systemd_system_unitdir}/systemd-timesyncd.service.d/override.conf
}

# Ensure the file is attached to the main systemd package
FILES:${PN} += " \
    ${systemd_system_unitdir}/systemd-timesyncd.service.d/override.conf \
    ${sysconfdir}/systemd/system.conf.d/10-watchdog.conf \
"

# Remove network managment
# Networkmanager is used
PACKAGECONFIG:remove = " \
    networkd \
"
