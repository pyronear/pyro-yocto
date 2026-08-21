# Add U-Boot user-space utilities (fw_printenv, fw_setenv) and configuration file (fw_env.config)
# required by RAUC to read and switch A/B boot slots via U-Boot environment variables.
RDEPENDS:${PN} += "u-boot-fw-utils u-boot-env"
