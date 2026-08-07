FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append:raspberrypi5 = " file://rpi5-fix-uart.cfg"
