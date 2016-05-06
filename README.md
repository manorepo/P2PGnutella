# P2PGnutella
Gnutella Network style Peer to Peer file search in distributed files management using Java RMI

      Each peer should be both a server and a client. As a client, it provides interfaces through which users can issue queries and view search results.
      As a server, it accepts queries from other peers, checks for matches against its local data set, and responds with corresponding results. 
      In addition, since there's no central indexing server, is done in a distributed manner. 
      Each peer maintains a list of peers as its neighbor.
      Whenever a query request comes in, the peer will broadcast the query to all its neighbors in addition to searching its local storage (and responds if necessary)
