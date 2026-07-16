# pyro-yocto-engine

This repository allows you to flash a Yocto image onto a microSD card so you can run Pyro-Engine on a Raspberry Pi 5 in dev mode.

## ⚙️ Installation

### Prerequisites

* You will need a Raspberry Pi 5 (minimum 2 GB RAM)
* You will need a micro SD card (minimum 4 GB)
* If you wish to carry out development work, you will need to install and configure the Pyronear development environment : [https://github.com/pyronear/pyro-envdev/tree/main](https://github.com/pyronear/pyro-envdev/tree/main)

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

#### Setup the Yocto Build Environment:

The project uses the official `bitbake-setup` tool (introduced in Yocto 5.3+) to automate the environment initialization using the `yocto-pyro.conf.json` file.

* **Method 1: Using VS Code (Recommended)**
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

     When prompted, select **"Pyro conf for rpi5"** to initialize the layers and configure the build directory.

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


Then configure these two repo folders using their respective documentation.

### 🌐 IP Addresses

In order to configure Ansible correctly, you need to know the IP address of your Raspberry Pi 5.

* **UART (Serial)** : Connect your Raspberry Pi 5 to your PC using a USB-to-TTL (3.3V) adapter. This allows you to access the debug console without a screen.
* **Ethernet** : Connect the Pi 5 to your PC
* Open a serial terminal on your PC (e.g. Minicom, Screen or PuTTY) set to a baud rate of 115,200. (It is recommended that you power the Pi 5 via your PC for this operation)

```bash
# Replace /dev/ttyUSB0 with your UART device
sudo screen /dev/ttyUSB0 115200
```

**How to identify your UART adapter ?**

Run "ls /dev/ttyUSB*" or "ls /dev/ttyACM*" before and after plugging in your adapter to see which name appears.

* Once you've logged in, log in as ‘dev’
* Run the following command to retrieve the IP address:

```bash
ifconfig
```

* Finally, switch off the Pi 5, exit the UART connection interface and disconnect your UART connection tool.
## 💻 Build
* Source the Yocto/BitBake build environment:

  ```bash
  cd bitbake-builds/pyro-yocto/build
  source init-build-env
  ```

* Start the compilation of the `pyronear-image` custom image:

  ```bash
  bitbake pyronear-image
  ```

## 💾 Flash micro-sd card

* Insert the micro SD card into your computer.
* Note down the path to your microSD card (e.g. /dev/sdb or /dev/mmcblk0) to avoid any write errors :

```bash
lsblk
```

* Unmount the Bootfs and Rootfs partitions on the microSD card.
* Go to the deploy folder:

  ```bash
  cd bitbake-builds/pyro-yocto/build/tmp/deploy/images/pyronear-rpi5
  ```

* Run the following command (replace `/dev/location_microSD` with your microSD card device, e.g., `/dev/sdb` or `/dev/mmcblk0`):

  ```bash
  sudo bmaptool copy pyronear-image-pyronear-rpi5.rootfs.wic.bz2 /dev/location_microSD
  ```

* Run the command "sync" before removing your microSD card :

```bash
sync
```

## ▶️ Run

Nous allons maintenant lancer le Ansible configurer précédement.

* Vérifier que l'on se trouve dans l'environnement virtuel Python:

* Ce placer dans le dossier "pi-manager-template"
  ```bash
  cd pi-manager-template
  ```

* lancer la commande:
  ```bash
  make ansible-up
  ```

* Puis déployer Ansible sur la ou les Raspberry Pi avec:
  ```
  make deploy-all-engines
  ```
## 🛠️ Test

Run this command in the Pi 5 terminal to check that everything is working properly :

```bash
docker logs -f engine
```