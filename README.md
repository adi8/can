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

Note this node has to be active before other peer nodes can join the overlay network.

### Peer node

To run a peer node run the following

```
bash node <dns-ip-address>
```
Here IP address of DNS server has to be passed as it serves the peer with a list of bootstrap nodes to allow the node to join the overlay network.

Once within the node terminal. The following commands are accepted
  * JOIN 
  
    ```
    peer-0 > join
    ```
    
  * INSERT
  
    ```
    peer-0 > insert <keyword> <filename>
    ```
    
    `<keyword>` is a keyword which is hashed to find a peer that would store the file.
    `<filename>` is the file that will be stored at a peer.
    
  * SEARCH 
  
    ```
    peer-0 > search <keyword> <filename>
    ```
    
    `<keyword>` is a keyword which is hashed to find a peer that would have stored the file.
    `<filename>` is the file that will be searched for at peers.
    
  * VIEW 
  
    ```
    peer-0 > view
    peer-0 > view 0
    ```
    
    Here '0' is a peer identifier and would display information of peer-0.
    Without a peer identifier the command will display information of all the peers in the overlay network.

  * LEAVE
  
    Currently node does not completely support leave command.
