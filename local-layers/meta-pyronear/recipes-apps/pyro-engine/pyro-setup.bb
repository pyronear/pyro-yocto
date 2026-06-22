SUMMARY = "PyroEngine application orchestration and deployment"
LICENSE = "CLOSED"

inherit systemd useradd

USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = "--system -d /data/pyro-engine-home -m --shell /bin/false --user-group --groups docker engine"
DEPENDS += "docker-moby"

RDEPENDS:${PN} += "curl jq docker-compose"

SRC_URI = " \
    file://pyro-init.sh \
    file://pyro-init.service \
    file://pyro-engine.service \
"

S = "${UNPACKDIR}"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

SYSTEMD_SERVICE:${PN} = "pyro-init.service pyro-engine.service"
SYSTEMD_AUTO_ENABLE = "enable"

do_install() {
    # 1. Install application software suite in the DATA space
    install -d ${D}/data/pyro-engine-home/data

    # 2. Application initialization scripts and services
    install -d ${D}${bindir}
    install -m 0755 ${S}/pyro-init.sh ${D}${bindir}/pyro-init.sh

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${S}/pyro-init.service ${D}${systemd_system_unitdir}/pyro-init.service
    install -m 0644 ${S}/pyro-engine.service ${D}${systemd_system_unitdir}/pyro-engine.service
}

FILES:${PN} += " \
    /data/pyro-engine-home \
    ${bindir}/pyro-init.sh \
    ${systemd_system_unitdir}/pyro-init.service \
    ${systemd_system_unitdir}/pyro-engine.service \
"