# pyro-yocto-engine

This repository allows you to flash a Yocto image onto a microSD card so you can run Pyro-Engine on a Raspberry Pi 5 in dev mode.

## ⚙️ Installation

### Prerequisites

* You will need a Raspberry Pi 5 (minimum 2 GB RAM)
* You will need a micro SD card (minimum 4 GB)
* Install the prerequisites for installing Yocto:

  ```bash
  sudo apt update
  sudo apt install build-essential chrpath cpio debianutils diffstat file gawk gcc git iputils-ping libacl1 liblz4-tool locales python3 python3-git python3-jinja2 python3-pexpect python3-pip python3-subunit socat texinfo unzip wget xz-utils zstd
  ```

* If you haven't installed bmaptool:

  ```bash
  sudo apt install bmap-tools
  ```

We will use the "Wrynose" version of Yocto (6.0 LTS).

### 1. Import meta-pyronear layer :

* Create a folder that will host the pyronear project.
* Clone the meta-pyronear layer inside this folder:
  ```bash
  repo address to be defined
  ```
### 2. Setup the Yocto Build Environment:

The project uses the official `bitbake-setup` tool (introduced in Yocto 5.3+) to automate the environment initialization using the `yocto-pyro.conf.json` file.

* **Method 1: Using VS Code**
  1. Open the project root folder in VS Code.
  2. Install the official **Yocto Project BitBake** extension.
  3. The extension will automatically detect `yocto-pyro.conf.json` and prompt you to initialize/configure the environment. Select the configuration named **"Pyro conf for rpi5"**.

* **Method 2: Using the Command Line**
  1. Clone the `bitbake` repository at the root of the project to access the `bitbake-setup` utility:

     ```bash
     git clone https://git.openembedded.org/bitbake
     ```

  2. Run the initialization command:

     ```bash
     ./bitbake/bin/bitbake-setup init yocto-pyro.conf.json
     ```

     When prompted, select **"Pyro conf for rpi5"** to initialize the layers and configure the build directory (in our case we have configured it as ```pyro-yocto```).

### 💻 Build
* Source the Yocto/BitBake build environment:

  ```bash
  cd bitbake-builds/pyro-yocto/build
  source init-build-env
  ```

Start the compilation of the `pyronear-image` custom image:
  * **Production image:**
    - Contains only what is strictly necessary for the engine to function:

      ```bash
      bitbake pyronear-image-prod
      ```

  * **Dev image:** 
    - Contains development and debugging tools:
      - rootfs access without a password
      - nano
      - htop 

      ```bash
      bitbake pyronear-image-dev
      ```

### 💾 Flash micro-sd card

* Insert the micro SD card into your computer.
* Note down the path to your microSD card (e.g. /dev/sdb or /dev/mmcblk0) to avoid any write errors :

  ```bash
  lsblk
  ```

* Unmount the partitions on the microSD card.
* Go to the deploy folder:

  ```bash
  cd bitbake-builds/pyro-yocto/build/tmp/deploy/images/pyronear-rpi5
  ```

* Run the following command (replace `/dev/location_microSD` with your microSD card device, e.g., `/dev/sdb` or `/dev/mmcblk0`):

  ```bash
  # image prod
  sudo bmaptool copy pyronear-image-prod-pyronear-rpi5.rootfs.wic.bz2 /dev/location_microSD

  # image dev
  sudo bmaptool copy pyronear-image-dev-pyronear-rpi5.rootfs.wic.bz2 /dev/location_microSD
  ```

* Run the command "sync" before removing your microSD card :

  ```bash
  sync
  ```
### 🌐 IP Addresses

In order to configure Ansible correctly, you need to know the IP address of your Raspberry Pi 5.

* **UART (Serial)** : Connect your Raspberry Pi 5 to your PC using a USB-to-TTL (3.3V) adapter. This allows you to access the debug console without a screen.
* **Ethernet** : Connect the Pi 5 to your PC
* Open a serial terminal on your PC (e.g. Minicom, Screen or PuTTY) set to a baud rate of 115,200. (It is recommended that you power the Pi 5 via your PC for this operation)

  ```bash
  # Replace /dev/ttyUSB0 with your UART device
  sudo screen /dev/ttyUSB0 115200
                or
  sudo picocom -b 115200 /dev/ttyUSB0
  ```

**How to identify your UART adapter ?**

Run "ls /dev/ttyUSB*" or "ls /dev/ttyACM*" before and after plugging in your adapter to see which name appears.

* Once you've logged in
* Run the following command to retrieve the IP address:

  ```bash
  ifconfig
  ```

* Finally, switch off the Pi 5, exit the UART connection interface and disconnect your UART connection tool.

## 🧩 Configuration

### 🤖 Ansible

Once the environment is configured, we need to install all the essential elements for the operation of Pyronear's AI.

* Create a folder for the Ansible configuration.
  ```
  mkdir Ansible-conf
  cd Ansible-conf
  ```

* Install the "pi-manager-template" repo inside this Ansible configuration folder:

  ```
  git clone https://github.com/pyronear/pi-manager-template.git
  ```
* Also install the "pi-manager-example" repo in this Ansible configuration folder:

  ```
  git clone https://github.com/pyronear/pi-manager-example.git
  ```
* To configure Ansible, we must first place ourselves in a Python venv environment:
  - We navigate to the same directory as the two folders we just cloned:
  ```
  python3 -m venv venv
  source venv/bin/activate
  ```


Then configure these two repo folders using their respective documentation :
* [pi-manager-template](https://github.com/pyronear/pi-manager-template)
* [pi-manager-example](https://github.com/pyronear/pi-manager-example)


## ▶️ Provisioning

We will now launch the Ansible configured previously.

* Verify that we are in the Python virtual environment:

* Go to the "pi-manager-template" folder
  ```bash
  cd pi-manager-template
  ```

* Launch the following command to set up docker:
  ```bash
  make ansible-up
  ```

* Deploy Ansible on the Raspberry Pi(s) with:
  ```
  make deploy-all-engines
  ```

## 🔄 OS Update

The OS update process consists of two steps:

* Build the RAUC bundle

* Manual update or using Ansible

**Build RAUC bundle image :**

* Build the ```.raucb``` image using the following command:
```bash
# Production image
bitbake pyronear-bundle-prod

# Development image
bitbake pyronear-bundle-dev
```

**Manual update :**

* Copy via scp the bundle image file (```.raucb```) :

  ```bash
  # bundle dev
  scp tmp/deploy/images/pyronear-rpi5/pyronear-bundle-dev-pyronear-rpi5.raucb your-user@X.X.X.X:/tmp/
  ```
  ```bash
  # bundle prod
  scp tmp/deploy/images/pyronear-rpi5/pyronear-bundle-prod-pyronear-rpi5.raucb your-user@X.X.X.X:/tmp/
  ```

* Connect to the Pi 5 via SSH

* Run the update command:
```bash
rauc install /tmp/pyronear-bundle-*.raucb
```

If everything goes well, you should see the following message:
```
installing
  0% Installing
  0% Determining slot states
 10% Determining slot states done.
 10% Checking bundle
 10% Verifying signature
 20% Verifying signature done.
 20% Checking bundle done.
 20% Checking manifest contents
 30% Checking manifest contents done.
 30% Determining target install group
 40% Determining target install group done.
 40% Updating slots
 40% Checking slot rootfs.1 (B)
 46% Checking slot rootfs.1 (B) done.
 46% Copying image to rootfs.1
 47% Copying image to rootfs.1
 48% Copying image to rootfs.1
 49% Copying image to rootfs.1
 50% Copying image to rootfs.1
 51% Copying image to rootfs.1
 52% Copying image to rootfs.1
 53% Copying image to rootfs.1

...

 95% Copying image to rootfs.1
 96% Copying image to rootfs.1
 97% Copying image to rootfs.1
 98% Copying image to rootfs.1
 99% Copying image to rootfs.1
 99% Copying image to rootfs.1 done.
 99% Updating slots done.
100% Installing done.
Installing `/tmp/pyronear-bundle-prod-pyronear-rpi5.raucb` succeeded
```

To finish, you just need to restart the Raspberry Pi 5 in order to boot on the new image.

**Using Ansible :**
For the update process using Ansible, you will need the previously presented github repo : [pi-manager-template](https://github.com/pyronear/pi-manager-template)

* Copy the bundle image file (```.raucb```) in the folder ``bundles`` of ``pi-manager-template`` by renaming it to "```pyronear-bundle-pyronear-rpi5.raucb```"

* Start the docker as explained in the **Provisioning** section

* Run the update command:
```bash
make update-os
```

## ⚠️ RAUC note

You will find in this section some useful commands to manage RAUC :

* **Check RAUC status** (it's very useful after an update to verify if the update was successful):
  ```bash
  rauc status
  ```
  Here is an example of what you should see if the update was successful:

  ```bash
  === System Info ===
  Compatible:  pyronear-rpi5
  Variant:     
  Booted from: rootfs.0 (A)

  === Bootloader ===
  Activated: rootfs.0 (A)

  === Slot States ===
  o [rootfs.1] (/dev/disk/by-partlabel/rootfsB, ext4, inactive)
      bootname: B
      boot status: good

  x [rootfs.0] (/dev/disk/by-partlabel/rootfsA, ext4, booted)
      bootname: A
      boot status: good

  ```

* **In case you want to roll back to the previous update:**
  ```bash
  # You must replace "X" by the number of the targeted slot.
  rauc status mark.active rootfs.X
  ```
  In this case, it is recommended to restart the Raspberry Pi 5 in order to boot on the previous image.

  ```bash
  reboot
  ```

## ⚠️ UART note:

In the machine configuration file "```pyronear-rpi5.conf```", you can choose the serial port to use for debugging:

```
# Enable serial port for debugging and communication
# Choice of console UART:
# - ENABLE_UART_UART0 = "1" : console on GPIO14/15 (RP1 uart0, via the uart0-pi5 overlay).
#   RP1 is only reachable once Linux brings up it PCIe link,
#   so U-Boot itself stays silent on this uart, only the kernel console works there.
# - ENABLE_UART_UART0 = "0": console on the J16 debug connector (uart10, SoC-native, always available).
#   Both U-Boot's and the kernel's boot logs become visible there.
```

* If you want to have U-boot debugging, you must set ```ENABLE_UART_UART0 = "0"```.

* If you don't have the adapter cable for the J16 port, use the GPIO ports (14/15) by setting ```ENABLE_UART_UART0 = "1"```.
  However, note that the kernel console will be available, but not U-Boot.

## 🛠️ Test

Run this command in the Pi 5 terminal to check that everything is working properly :

```bash
docker logs -f engine
```