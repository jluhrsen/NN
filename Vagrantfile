# -*- mode: ruby -*-
# vi: set ft=ruby :

# Vagrantfile API/syntax version.
VAGRANTFILE_API_VERSION = "2"

# This is a single-vm vagrant file that will install OpenDaylight and all the tools needed to run
# basic upstream CSIT
Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|

  # NFS is fragile, disable it and use rsync
  config.nfs.functional = false

  # Explicitly using the "insecure" private key below, so don't need the insert_key
  config.ssh.insert_key = false

  # Set x11 forwarding so we can run GUIs on host from ssh sessions to guest
  config.ssh.forward_x11 = true

  # Box setup (specs, tools, etc)
  config.vm.define "nn1" do |nn1|

    nn1.vm.hostname = "nn1"

    # Configure 4G RAM for virtualbox or libvirt.
    nn1.vm.provider :virtualbox do |c|
      c.memory = 2048
      c.cpus = 2
      c.name = "NN1"
    end

    # Build Vagrant box based on Fedora 25
    nn1.vm.box = "fedora/25-cloud-base"

    # Install dependencies and tools from dnf
    nn1.vm.provision "shell",
      inline: "dnf install -y --nogpgcheck git git-review java-openjdk java-1.8.0-openjdk-devel xauth ant"

    nn1.vm.provision "shell", inline: "git clone https://github.com/jluhrsen/NN.git"
    nn1.vm.provision "shell", inline: "git clone https://github.com/jluhrsen/nfl_examples"
    nn1.vm.provision "shell", inline: "git clone https://github.com/simbrain/simbrain.git"
    nn1.vm.provision "shell", inline: "cd simbrain; ant"
    nn1.vm.provision "shell", inline: "echo 'export CLASSPATH=/home/vagrant/simbrain/build/main/Simbrain.jar:./' >> ~vagrant/.bashrc"
    nn1.vm.provision "shell", inline: "mkdir -p networks/autonets"
    nn1.vm.provision "shell", inline: "chown -R vagrant:vagrant *"
    nn1.vm.provision "shell", inline: "cd NN/src; ln -s ../../nfl_examples/resources/data/ data; ln -s ../../networks networks"

  end

end
