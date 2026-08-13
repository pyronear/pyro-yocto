# meta-pyronear/recipes-core/rauc/rauc_%.bbappend
FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append = " \
    file://check_provisioning.sh \
    file://rauc-mark-good.service \
"

# Replace installation to point to /etc/rauc
do_install:append() {

    # 2. Install our custom business validation script
    install -d ${D}${bindir}
    install -m 0755 ${UNPACKDIR}/check_provisioning.sh ${D}${bindir}/check_provisioning.sh

    # 3. Replace the default RAUC systemd service
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/rauc-mark-good.service ${D}${systemd_system_unitdir}/rauc-mark-good.service
}

# Ensure the package integrates these paths
FILES:${PN}-mark-good += "${bindir}/check_provisioning.sh"
