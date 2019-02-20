## Compiling the project

1. Extract tar.gz file to obtain can_ds directory
2. Enter into can_ds directory
3. Run the following to compile project

```
mkdir -p build/classes/java/main/
javac -d build/classes/java/main/ src/main/java/can_ds/*/*
```

## Running different nodes

### DNS node

To run the dns node run the following

```
bash dnsnode
```

### Peer node

To run a peer node run the following

```
bash node <dns-ip-address>
```
We need to pass the IP address of DNS server as it serves the peer with a list of bootstrap nodes to allow the node to join the overlay network.

