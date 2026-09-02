# System override for systemd-timesyncd (Yocto Read-Only Standard)
FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += " \
    file://timesyncd-override.conf \
    file://10-watchdog.conf \
    file://timesync-state-tmpfiles.conf \
"

do_install:append() {
    install -d ${D}${sysconfdir}/systemd/system.conf.d

    install -m 0644 ${UNPACKDIR}/10-watchdog.conf ${D}${sysconfdir}/systemd/system.conf.d/10-watchdog.conf

    install -d ${D}${systemd_system_unitdir}/systemd-timesyncd.service.d
    install -m 0644 ${UNPACKDIR}/timesyncd-override.conf ${D}${systemd_system_unitdir}/systemd-timesyncd.service.d/override.conf

    install -d ${D}${sysconfdir}/tmpfiles.d
    install -m 0644 ${UNPACKDIR}/timesync-state-tmpfiles.conf ${D}${sysconfdir}/tmpfiles.d/timesync-state-tmpfiles.conf
}

# Ensure the file is attached to the main systemd package
FILES:${PN} += " \
    ${systemd_system_unitdir}/systemd-timesyncd.service.d/override.conf \
    ${sysconfdir}/systemd/system.conf.d/10-watchdog.conf \
    ${sysconfdir}/tmpfiles.d/timesync-state-tmpfiles.conf \
"

# Remove network managment
# Networkmanager is used
PACKAGECONFIG:remove = " \
    networkd \
"
