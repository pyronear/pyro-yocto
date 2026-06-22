SUMMARY = "PyroEngine application orchestration and deployment"
LICENSE = "CLOSED"

inherit systemd useradd

USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = "--system -d /data/pyro-engine-home -m --shell /bin/false --user-group --groups docker engine"
DEPENDS += "docker-moby"

RDEPENDS:${PN} += "curl jq docker-compose docker-moby"

SRC_URI = " \
    file://pyro-engine.service \
"

S = "${UNPACKDIR}"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

SYSTEMD_SERVICE:${PN} = "pyro-engine.service"
SYSTEMD_AUTO_ENABLE = "enable"

do_install() {

    install -d ${D}/home
    ln -s /data/pyro-engine-home ${D}/home/engine
    ln -s /data/pyro-engine-src ${D}/home/pyro-engine

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${S}/pyro-engine.service ${D}${systemd_system_unitdir}/pyro-engine.service
}

FILES:${PN} += " \
    /home/engine \
    /home/pyro-engine \
    ${systemd_system_unitdir}/pyro-engine.service \
"