# Dockerfile for nn training
FROM fedora:25
MAINTAINER Jamo Luhrsen <jamo@luhrsenlounge.net>

# Install required software
RUN dnf install -y --nogpgcheck git git-review java-openjdk java-1.8.0-openjdk-devel xauth ant

RUN git clone https://github.com/jluhrsen/NN.git
RUN git clone https://github.com/jluhrsen/nfl_examples.git
RUN git clone https://github.com/simbrain/simbrain.git
RUN export JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF8; cd simbrain; ant
RUN mkdir -p data; mkdir -p networks/autonets; cd NN/src; ln -s /data/ data; ln -s /networks networks
RUN cd NN/src; export CLASSPATH=/simbrain/build/main/Simbrain.jar:./; javac NetworkTrainer.java; java NetworkTrainer
