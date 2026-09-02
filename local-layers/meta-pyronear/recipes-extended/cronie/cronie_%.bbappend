FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += " \
    file://cron-spool-tmpfiles.conf \
    file://var-spool-cron.mount \
"

do_install:append() {
    install -d ${D}${sysconfdir}/tmpfiles.d
    install -m 0644 ${UNPACKDIR}/cron-spool-tmpfiles.conf ${D}${sysconfdir}/tmpfiles.d/cron-spool-tmpfiles.conf

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/var-spool-cron.mount ${D}${systemd_system_unitdir}/var-spool-cron.mount
}

FILES:${PN} += " \
    ${sysconfdir}/tmpfiles.d/cron-spool-tmpfiles.conf \
    ${systemd_system_unitdir}/var-spool-cron.mount \
"

# /var/spool is bind-mounted onto a tmpfs by volatile-binds (see var-volatile-spool.service)
# so crontabs written there vanish on reboot
# Persist just /var/spool/cron on /data instead.
SYSTEMD_SERVICE:${PN} += "var-spool-cron.mount"
